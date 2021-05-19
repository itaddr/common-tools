package com.itaddr.common.tools;

import javassist.*;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.text.DecimalFormat;

/**
 * @Author 马嘉祺
 * @Date 2020/5/30 0030 16 06
 * @Description <p></p>
 */
public class ProxyLibraryTest {
    
    @Test
    public void test01() throws Exception {
        CountService delegate = new CountServiceImpl();
        
        long time = System.currentTimeMillis();
        CountService jdkProxy = createJdkDynamicProxy(delegate);
        time = System.currentTimeMillis() - time;
        System.out.println("Create JDK Proxy: " + time + " ms");
        
        time = System.currentTimeMillis();
        CountService cglibProxy = createCglibDynamicProxy(delegate);
        time = System.currentTimeMillis() - time;
        System.out.println("Create CGLIB Proxy: " + time + " ms");
        
        time = System.currentTimeMillis();
        CountService javassistProxy = createJavassistDynamicProxy(delegate);
        time = System.currentTimeMillis() - time;
        System.out.println("Create JAVAASSIST Proxy: " + time + " ms");
        
        time = System.currentTimeMillis();
        CountService javassistBytecodeProxy = createJavassistBytecodeDynamicProxy(delegate);
        time = System.currentTimeMillis() - time;
        System.out.println("Create JAVAASSIST Bytecode Proxy: " + time + " ms");
        
        time = System.currentTimeMillis();
        CountService asmBytecodeProxy = createAsmBytecodeDynamicProxy(delegate);
        time = System.currentTimeMillis() - time;
        System.out.println("Create ASM Proxy: " + time + " ms");
        System.out.println("================");
        
        for (int i = 0; i < 3; i++) {
            test(jdkProxy, "Run JDK Proxy: ");
            test(cglibProxy, "Run CGLIB Proxy: ");
            test(javassistProxy, "Run JAVAASSIST Proxy: ");
            test(javassistBytecodeProxy, "Run JAVAASSIST Bytecode Proxy: ");
            test(asmBytecodeProxy, "Run ASM Bytecode Proxy: ");
            System.out.println("----------------");
        }
    }
    
    public interface CountService {
        
        int count();
        
    }
    
    public class CountServiceImpl implements CountService {
        
        private int count = 0;
        
        public int count() {
            return count++;
        }
    }
    
    private static void test(CountService service, String label) {
        service.count(); // warm up
        int count = 10000000;
        long time = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            service.count();
        }
        time = System.currentTimeMillis() - time;
        System.out.println(label + time + " ms, " + new DecimalFormat().format(count * 1000 / time) + " t/s");
    }
    
    private static CountService createJdkDynamicProxy(CountService delegate) {
        InvocationHandler handler = (object, method, objects) -> method.invoke(delegate, objects);
        return (CountService) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{CountService.class}, handler);
    }
    
    private static CountService createCglibDynamicProxy(CountService delegate) {
        MethodInterceptor mi = (object, method, objects, methodProxy) -> methodProxy.invoke(delegate, objects);
        
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(mi);
        enhancer.setInterfaces(new Class[]{CountService.class});
        
        return (CountService) enhancer.create();
    }
    
    private static CountService createJavassistDynamicProxy(CountService delegate) throws Exception {
        MethodHandler mh = (self, m, proceed, args) -> m.invoke(delegate, args);
        
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(new Class[]{CountService.class});
        Class proxyClass = proxyFactory.createClass();
        CountService javassistProxy = (CountService) proxyClass.newInstance();
        ((ProxyObject) javassistProxy).setHandler(mh);
        
        return javassistProxy;
    }
    
    private static CountService createJavassistBytecodeDynamicProxy(CountService delegate) throws Exception {
        ClassPool mPool = new ClassPool(true);
        CtClass mCtc = mPool.makeClass(CountService.class.getName() + "JavaassistProxy");
        mCtc.addInterface(mPool.get(CountService.class.getName()));
        mCtc.addConstructor(CtNewConstructor.defaultConstructor(mCtc));
        mCtc.addField(CtField.make("public " + CountService.class.getName() + " delegate;", mCtc));
        mCtc.addMethod(CtNewMethod.make("public int count() { return delegate.count(); }", mCtc));
        Class pc = mCtc.toClass();
        CountService bytecodeProxy = (CountService) pc.newInstance();
        Field filed = bytecodeProxy.getClass().getField("delegate");
        filed.set(bytecodeProxy, delegate);
        return bytecodeProxy;
    }
    
    private static CountService createAsmBytecodeDynamicProxy(CountService delegate) throws Exception {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        String className = CountService.class.getName() + "AsmProxy";
        String classPath = className.replace('.', '/');
        String interfacePath = CountService.class.getName().replace('.', '/');
        classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, classPath, null, "java/lang/Object", new String[]{interfacePath});
        
        MethodVisitor initVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        initVisitor.visitCode();
        initVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        initVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        initVisitor.visitInsn(Opcodes.RETURN);
        initVisitor.visitMaxs(0, 0);
        initVisitor.visitEnd();
        
        FieldVisitor fieldVisitor = classWriter.visitField(Opcodes.ACC_PUBLIC, "delegate", "L" + interfacePath + ";", null, null);
        fieldVisitor.visitEnd();
        
        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "count", "()I", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitFieldInsn(Opcodes.GETFIELD, classPath, "delegate", "L" + interfacePath + ";");
        methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, interfacePath, "count", "()I", true);
        methodVisitor.visitInsn(Opcodes.IRETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
        
        ByteArrayClassLoader classLoader = new ByteArrayClassLoader();
        
        classWriter.visitEnd();
        byte[] code = classWriter.toByteArray();
        
        CountService bytecodeProxy = (CountService) classLoader.getClass(className, code).newInstance();
        Field filed = bytecodeProxy.getClass().getField("delegate");
        filed.set(bytecodeProxy, delegate);
        return bytecodeProxy;
    }
    
    private static class ByteArrayClassLoader extends ClassLoader {
        
        public ByteArrayClassLoader() {
            super(ByteArrayClassLoader.class.getClassLoader());
        }
        
        public synchronized Class getClass(String name, byte[] code) {
            if (name == null) {
                throw new IllegalArgumentException("");
            }
            return defineClass(name, code, 0, code.length);
        }
        
    }
    
}
