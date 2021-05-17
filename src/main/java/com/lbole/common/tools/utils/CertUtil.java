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
package com.lbole.common.tools.utils;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * @Author 马嘉祺
 * @Date 2020/5/28 0028 15 29
 * @Description <p></p>
 */
public final class CertUtil {
    
    private CertUtil() {
    }
    
    public static X509Certificate loadX509CertificateFromPEM(File file) {
        return null;
    }
    
    public static X509Certificate loadX509CertificateFromPEM(byte[] bytes) throws CertificateException {
        return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bytes));
    }
    
    public static X509Certificate loadX509CertificateFromPKCS12(File file, String password, String alias) {
        return null;
    }
    
    public static X509Certificate loadX509CertificateFromPKCS12(byte[] bytes, String password, String alias) {
        return null;
    }
    
    public static X509Certificate loadX509CertificateFromJKS(File file, String password, String alias) {
        return null;
    }
    
    public static X509Certificate loadX509CertificateFromJKS(byte[] bytes, String password, String alias) {
        return null;
    }
    
    /**
     * @param storeType     仓库类型: JKS; PKCS12
     * @param certFile
     * @param privateKey
     * @param certChainFile
     * @param password
     * @param alias
     * @return
     */
    public static KeyStore loadKeyStoreFromPEM(String storeType, File certFile, byte[] privateKey, File certChainFile, String password, String alias) {
        
        
        return null;
    }
    
    /**
     * @param storeType      仓库类型: JKS; PKCS12(PFX)
     * @param certBytes
     * @param privateKey
     * @param certChainBytes
     * @param password
     * @param alias
     * @return
     */
    public static KeyStore loadKeyStoreFromPEM(String storeType, byte[] certBytes, byte[] privateKey, byte[][] certChainBytes, String password, String alias) {
        return null;
    }
    
    public static KeyStore loadKeyStoreFromPKCS12(File file, String password) {
        return null;
    }
    
    public static KeyStore loadKeyStoreFromPKCS12(byte[] bytes, String password) {
        return null;
    }
    
    public static KeyStore loadKeyStoreFromJKS(File file, String password) {
        return null;
    }
    
    public static KeyStore loadKeyStoreFromJKS(byte[] bytes, String password) {
        return null;
    }
    
    public static SSLContext createSSLContextFromPEM() {
        return null;
    }
    
}
