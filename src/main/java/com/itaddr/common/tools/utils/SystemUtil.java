/*
 *  The lBole licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.itaddr.common.tools.utils;

import java.io.File;
import java.util.Objects;

/**
 * @Author 马嘉祺
 * @Date 2020/2/10 0010 14 07
 * @Description <p></p>
 */
public final class SystemUtil {
    
    public final static String BASE_DIR, CONFIG_DIR, LOGS_DIR, USER_TMPDIR;
    
    private static Class<?> LOGGER_FACTORY_CLASS = null, JORAN_CONFIGURATOR_CLASS = null, CONTEXT_CLASS = null, LOGGER_CONTEXT_CLASS = null, STATUS_PRINTER_CLASS = null;
    
    static {
        
        String baseDirKey = "base.dir";
        String configDirKey = "config.dir";
        String logsDirKey = "logs.dir";
        String loggerDeviceKey = "logger.device";
        String springProfilesKey = "spring.profiles.active";
        String javaTmpdirKey = "java.io.tmpdir";
        
        String classesPath = Objects.requireNonNull(SystemUtil.class.getClassLoader().getResource("")).getPath();
        String baseDir = System.getProperty(baseDirKey, "");
        if ("".equals(baseDir)) {
            baseDir = new File(classesPath).getParent();
            System.setProperty(baseDirKey, baseDir);
        }
        BASE_DIR = baseDir;
        
        String configDir = System.getProperty(configDirKey, "");
        if ("".equals(configDir)) {
            configDir = classesPath + "config";
            System.setProperty(configDirKey, configDir);
        }
        CONFIG_DIR = configDir;
        
        String logsDir = System.getProperty(logsDirKey, "");
        if ("".equals(logsDir)) {
            logsDir = BASE_DIR + File.separator + "logs";
            System.setProperty(logsDirKey, logsDir);
        }
        LOGS_DIR = logsDir;
        
        if ("".equals(System.getProperty(springProfilesKey, ""))) {
            System.setProperty(springProfilesKey, "dev");
        }
        
        String loggerDevice = System.getProperty(loggerDeviceKey, "");
        if ("".equals(loggerDevice)) {
            loggerDevice = "console";
            System.setProperty(loggerDeviceKey, loggerDevice);
        }
        
        USER_TMPDIR = System.getProperty(javaTmpdirKey);
        
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try {
            LOGGER_FACTORY_CLASS = classLoader.loadClass("org.slf4j.LoggerFactory");
            JORAN_CONFIGURATOR_CLASS = classLoader.loadClass("ch.qos.logback.classic.joran.JoranConfigurator");
            CONTEXT_CLASS = classLoader.loadClass("ch.qos.logback.core.Context");
            LOGGER_CONTEXT_CLASS = classLoader.loadClass("ch.qos.logback.classic.LoggerContext");
            STATUS_PRINTER_CLASS = classLoader.loadClass("ch.qos.logback.core.util.StatusPrinter");
        } catch (ClassNotFoundException e) {
        }
    }
    
    private SystemUtil() {
    }
    
    public static void loadLogbackConfig(String configFilePath) {
        File configFile = new File(configFilePath);
        if (!configFile.exists()) {
            System.err.println("[logback] Logback External Config File Parameter does not reference a file that exists");
            return;
        }
        if (!configFile.isFile()) {
            System.err.println("[logback] Logback External Config File Parameter exists, but does not reference a file");
            return;
        }
        if (!configFile.canRead()) {
            System.err.println("[logback] Logback External Config File exists and is a file, but cannot be read.");
            return;
        }
        
        if (null == LOGGER_FACTORY_CLASS) {
            System.out.println("[logback] Missing 'slf4j' dependency");
            return;
        }
        if (null == JORAN_CONFIGURATOR_CLASS || null == CONTEXT_CLASS || null == LOGGER_CONTEXT_CLASS || null == STATUS_PRINTER_CLASS) {
            System.err.println("[logback] Missing 'logback' dependency");
            return;
        }
        
        try {
            /*ILoggerFactory factory = LoggerFactory.getILoggerFactory();*/
            Object iLoggerFactoryInstance = LOGGER_FACTORY_CLASS.getMethod("getILoggerFactory").invoke(LOGGER_FACTORY_CLASS);
            /*JoranConfigurator configurator = new JoranConfigurator();*/
            Object joranConfiguratorInstance = JORAN_CONFIGURATOR_CLASS.newInstance();
            /*configurator.setContext(iLoggerFactoryInstance);*/
            JORAN_CONFIGURATOR_CLASS.getMethod("setContext", CONTEXT_CLASS).invoke(joranConfiguratorInstance, iLoggerFactoryInstance);
            /*lc.reset();*/
            LOGGER_CONTEXT_CLASS.getMethod("reset").invoke(iLoggerFactoryInstance);
            /*configurator.doConfigure(externalConfigFileLocation);*/
            JORAN_CONFIGURATOR_CLASS.getMethod("doConfigure", String.class).invoke(joranConfiguratorInstance, configFilePath);
            /*StatusPrinter.printInCaseOfErrorsOrWarnings(iLoggerFactoryInstance);*/
            STATUS_PRINTER_CLASS.getMethod("printInCaseOfErrorsOrWarnings", CONTEXT_CLASS).invoke(STATUS_PRINTER_CLASS, iLoggerFactoryInstance);
        } catch (Exception e) {
            System.err.println("[logback] Logback External Config file exists and is a file and readable, but loading error");
            e.printStackTrace();
        }
    }
    
}
