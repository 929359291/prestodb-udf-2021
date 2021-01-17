package org.example.presto;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.facebook.presto.common.type.StandardTypes;
import com.facebook.presto.spi.function.Description;
import com.facebook.presto.spi.function.ScalarFunction;
import com.facebook.presto.spi.function.SqlType;
import io.airlift.slice.Slice;
import io.airlift.slice.Slices;

public class RSAEncryptFunctions {

    /**
     * @demo
     * <p>String privateKeyBase64 = "nba"
     * <p>String publicKeyBase64 = "cba"
     * <p>String return = "nba:cba"
     * @return privateKeyBase64:publicKeyBase64
     */
    @Description("rsa generate_key return privateKey:publicKey")
    @ScalarFunction("rsa_generate_key")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice generateKeyPairBase64Str(){
        RSA rsa = new RSA();
        return Slices.utf8Slice(rsa.getPrivateKeyBase64()+":"+rsa.getPublicKeyBase64());
    }

    @Description("rsa private_key encrypt")
    @ScalarFunction("rsa_private_encrypt")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice rsaPrivateKeyEncrypt(
        @SqlType(StandardTypes.VARCHAR) Slice text,
        @SqlType(StandardTypes.VARCHAR) Slice key) {

        RSA rsa = new RSA(key.toStringUtf8(), null);
        return Slices.utf8Slice(rsa.encryptBase64(text.toStringUtf8(), KeyType.PrivateKey));
    }

    @Description("rsa public_key encrypt")
    @ScalarFunction("rsa_public_encrypt")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice rsaPublicKeyEncrypt(
        @SqlType(StandardTypes.VARCHAR) Slice text,
        @SqlType(StandardTypes.VARCHAR) Slice key) {

        RSA rsa = new RSA(null, key.toStringUtf8());
        return Slices.utf8Slice(rsa.encryptBase64(text.toStringUtf8(), KeyType.PublicKey));
    }

    @Description("rsa private_key decrypt")
    @ScalarFunction("rsa_private_decrypt")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice rsaPrivateKeyDecrypt(
        @SqlType(StandardTypes.VARCHAR) Slice encryptText,
        @SqlType(StandardTypes.VARCHAR) Slice key) {

        RSA rsa = new RSA(key.toStringUtf8(), null);
        return Slices.utf8Slice(rsa.decryptStr(encryptText.toStringUtf8(), KeyType.PrivateKey));
    }

    @Description("rsa public_key decrypt")
    @ScalarFunction("rsa_public_decrypt")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice rsaPublicKeyDecrypt(
        @SqlType(StandardTypes.VARCHAR) Slice encryptText,
        @SqlType(StandardTypes.VARCHAR) Slice key) {

        RSA rsa = new RSA(null, key.toStringUtf8());
        return Slices.utf8Slice(rsa.decryptStr(encryptText.toStringUtf8(), KeyType.PublicKey));
    }

    public static void main(String[] args) {
        String priKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMh1AyShiE2PXzEe42qOh6T5k/O2idjkXZPDlAqZ8zqhBrCHzW63pmlWAaoor0dXsIoXH4CyONqW9weStC7O6ybHawe2O/os+alVkzrSqBDTSvPn64+1V7kTnU/PwLEE503b3rJ9411yOx3Dyw0+VW1W/3uBccJVVt/SZ2ySTUbHAgMBAAECgYAhYrOiTzDnUgCVqsJPhet7g4GrrnQo5mfba1Blr5w94IG/tu51CSszEfr2/w2qQSWK6iz5/zTO2yPLZBiUTywEnu5K4Qj2VSO+zowVM7BNMLDu2G2iSk7qNLtyf4prZApHNgkfurCouVSZvVU71yORog7iV2ThTn8G5k3iQd040QJBAOQCJa2e+GWg4AblOgl+XuDl82Ur4lClxL4LevkkTErfFbG5TEkIRuU+SHjWc9VSn/YT7GnlDI0ycnL26TtKGA8CQQDhEPthM+Cuyef0bnLfCJq6RC1kpWRb6yWGhrV1C1sTikoHeSiq6fi9saNcYeQCLd58O10I4r8JjC2kACHG/m3JAkEAwlPSKRfEPVnPlhDoVg8Obj+OIjw0tueRtyext4XoQCfCJab4DYawZdGfkt2z+6xgCSsR428EM5M7WCB4x79uoQJBANGKVgBxpEYXhPV1v00XXbb9sqV5tq49h3pwAJEwOWM/5RyAGPTHUOaLdGSWXwoIWpOfUUbslJ/2+qct2ETErMECQCOwbHBPJCXvER2aO3gyVVwDhMsAplkk9pb7gmnGBFAd8K2IPCSsB0WhhVlaEdpeI3tjc4YwzBAIFjB8Q680G5I=";
        String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIdQMkoYhNj18xHuNqjoek+ZPztonY5F2Tw5QKmfM6oQawh81ut6ZpVgGqKK9HV7CKFx+AsjjalvcHkrQuzusmx2sHtjv6LPmpVZM60qgQ00rz5+uPtVe5E51Pz8CxBOdN296yfeNdcjsdw8sNPlVtVv97gXHCVVbf0mdskk1GxwIDAQAB";
        String text = "china no.1";
        String enText = rsaPublicKeyEncrypt(Slices.utf8Slice(text), Slices.utf8Slice(pubKey)).toStringUtf8();
        String deText = rsaPrivateKeyDecrypt(Slices.utf8Slice(enText), Slices.utf8Slice(priKey)).toStringUtf8();
        System.out.println(deText);
        System.out.println(text.equals(deText));
    }
}
