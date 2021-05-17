package com.lbole.common.tools;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastConstructor;
import net.sf.cglib.reflect.FastMethod;
import org.junit.Test;

/**
 * @Author 马嘉祺
 * @Date 2020/5/30 0030 16 43
 * @Description <p></p>
 */
public class ReflectLibraryTest {
    
    @Test
    public void test01() throws Exception {
        jdkReflectInvoke();
    }
    
    public interface CountService {
        
        int count();
        
    }
    
    public static class CountServiceImpl implements ProxyLibraryTest.CountService {
        
        private int count = 0;
        
        public CountServiceImpl() {
        }
    
        public CountServiceImpl(Integer count) {
            this.count = count;
        }
        
        public int count() {
            return count++;
        }
    }
    
    private static void jdkReflectInvoke() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        FastClass fastClass = FastClass.create(classLoader, CountServiceImpl.class);
        
        // -----------default constructor----------
        FastConstructor defaultConstructor = fastClass.getConstructor(new Class[]{});
        CountServiceImpl o1 = (CountServiceImpl) defaultConstructor.newInstance();
        System.out.println(o1.count());
        // 直接通过newInstance也可以创建对象,底层实现一样,找到默认的FastConstructor
        CountServiceImpl o2 = (CountServiceImpl) fastClass.newInstance();
        System.out.println(o2.count());
        
        // ------------parameter constructor----------
        FastConstructor paramsConstructor = fastClass.getConstructor(new Class[]{Integer.class});
        CountServiceImpl o3 = (CountServiceImpl) paramsConstructor.newInstance(new Object[]{0});//传递参数
        System.out.println(o3.count());
        // 通过newInstance方式
        CountServiceImpl o4 = (CountServiceImpl) fastClass.newInstance(new Class[]{Integer.class}, new Object[]{0});
        System.out.println(o4.count());
        
        FastMethod countMethod = fastClass.getMethod("count", new Class[]{}); // 获得count方法,无参数
        // testMethod.getJavaMethod();//获取java api中Method引用
        Integer result = (Integer) countMethod.invoke(o4, null);
        System.out.println("invoke result:" + result);
    }
    
    private static void cglibReflectInvoke() {
    
    }
    
    private static void javassistReflectInvoke() {
    
    }
    
    private static void asmReflectInvoke() {
    
    }
    
}
