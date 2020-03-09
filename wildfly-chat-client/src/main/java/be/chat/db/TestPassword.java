package be.chat.db;

import org.wildfly.common.iteration.ByteIterator;
import org.wildfly.security.WildFlyElytronProvider;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.interfaces.DigestPassword;
import org.wildfly.security.password.spec.DigestPasswordAlgorithmSpec;
import org.wildfly.security.password.spec.DigestPasswordSpec;
import org.wildfly.security.password.spec.EncryptablePasswordSpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.spec.InvalidKeySpecException;

public class TestPassword {

    static final Provider ELYTRON_PROVIDER = new WildFlyElytronProvider();

    static final String TEST_REALM = "PocRealm";

    static final String TEST_PASSWORD = "Rolf123";

    static final String TEST_USERNAME = "Rolf";

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        PasswordFactory passwordFactory = PasswordFactory.getInstance(DigestPassword.ALGORITHM_DIGEST_SHA_256, ELYTRON_PROVIDER);

        DigestPasswordAlgorithmSpec digestAlgorithmSpec = new DigestPasswordAlgorithmSpec(TEST_USERNAME, TEST_REALM);
        EncryptablePasswordSpec encryptableSpec = new EncryptablePasswordSpec(TEST_PASSWORD.toCharArray(), digestAlgorithmSpec);

        DigestPassword original = (DigestPassword) passwordFactory.generatePassword(encryptableSpec);

        byte[] digest = original.getDigest();

        DigestPasswordSpec digestPasswordSpec = new DigestPasswordSpec(TEST_USERNAME, TEST_REALM, digest);

        DigestPassword restored = (DigestPassword) passwordFactory.generatePassword(digestPasswordSpec);

        System.out.println(String.format("Password Verified '%b'", passwordFactory.verify(restored, TEST_PASSWORD.toCharArray())));


        String maskedPassword = ByteIterator.ofBytes(digest).base64Encode().drainToString();
        System.out.println(String.format("Masked Password: " + maskedPassword));

    }
}
