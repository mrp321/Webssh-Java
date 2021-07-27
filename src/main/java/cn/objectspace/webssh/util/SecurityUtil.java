package cn.objectspace.webssh.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.crypto.asymmetric.KeyType;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;

/**
 * SecurityUtil
 *
 * @author qchen
 * @date 2021/6/30 8:51
 */
public class SecurityUtil {
    private static String RSA_PUBLIC_KEY;
    private static String RSA_PRIVATE_KEY;

    static {
        String rsaPublicKeyName = "key/rsa.publicKey";
        String rsaPrivateKeyName = "key/rsa.privateKey";
        RSA_PUBLIC_KEY = initRsaKey(rsaPublicKeyName);
        RSA_PRIVATE_KEY = initRsaKey(rsaPrivateKeyName);
    }

    private static String initRsaKey(String keyName) {
        String key = null;
        try {
            key = FileUtil.readUtf8String((SecurityUtil.class.getClassLoader().getResource("").getPath() + keyName));
        } catch (IORuntimeException e) {
            ClassPathResource classPathResource = new ClassPathResource(keyName);
            try {
                InputStream inputStream = classPathResource.getInputStream();
                key = IoUtil.readUtf8(inputStream);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                throw new RuntimeException(ioException);
            }
        }
        return key;
    }

    public static String rsaDec(String data) {
        return rsaDec(data, RSA_PRIVATE_KEY, RSA_PUBLIC_KEY);
    }

    public static String rsaDec(String data, String privateKey, String publicKey) {
        String decData = SecureUtil.rsa(privateKey, publicKey).decryptStr(data, KeyType.PrivateKey);
        return decData;
    }

    public static String rsaEnc(String data) {
        return rsaEnc(data, RSA_PRIVATE_KEY, RSA_PUBLIC_KEY);
    }

    public static String rsaEnc(String data, String privateKey, String publicKey) {
        String encData = SecureUtil.rsa(privateKey, publicKey).encryptBcd(data, KeyType.PublicKey);
        return encData;
    }

    public static KeyPair generateKeyPair() {
        KeyPair keyPair = SecureUtil.generateKeyPair(AsymmetricAlgorithm.RSA_None.getValue());
        return keyPair;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        KeyPair keyPair = generateKeyPair();


        String publicKey = Base64.encode(keyPair.getPublic().getEncoded());
        System.out.println(publicKey);
        System.out.println("==========================");

        String privateKey = Base64.encode(keyPair.getPrivate().getEncoded());
        System.out.println(privateKey);


        String str = "123456";
        String strEnc = rsaEnc(str);
        System.out.println("strEnc: " + strEnc);

        String strDec = rsaDec(strEnc);
        System.out.println("strDec: " + strDec);
    }
}
