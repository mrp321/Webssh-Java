package cn.objectspace.webssh.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.crypto.asymmetric.KeyType;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.util.Objects;

/**
 * SecurityUtil
 *
 * @author qchen
 * @date 2021/6/30 8:51
 */
public class SecurityUtil {
    private static final String RSA_PUBLIC_KEY;
    private static final String RSA_PRIVATE_KEY;

    static {
        RSA_PUBLIC_KEY = FileUtil.readUtf8String(Objects.requireNonNull(SecurityUtil.class.getClassLoader().getResource("")).getPath() + "key/rsa.publicKey");
        RSA_PRIVATE_KEY = FileUtil.readUtf8String(Objects.requireNonNull(SecurityUtil.class.getClassLoader().getResource("")).getPath() + "key/rsa.privateKey");
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
