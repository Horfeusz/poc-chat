package be.chat.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
class User {

    private String user;

    private String password;

    @Singular
    private List<Role> roles;

    public String getHashPassword() throws NoSuchAlgorithmException {
        return DigestUtils.md5Hex(password);
    }

}
