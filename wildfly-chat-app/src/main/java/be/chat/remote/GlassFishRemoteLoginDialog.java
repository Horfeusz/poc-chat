package be.chat.remote;

import com.google.common.base.Strings;
import com.sun.enterprise.security.LoginDialog;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class GlassFishRemoteLoginDialog implements LoginDialog {

    private String userName;

    private String password;

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public char[] getPassword() {
        return Optional.ofNullable(password)
                .filter(s -> !Strings.isNullOrEmpty(s))
                .map(String::toCharArray)
                .orElse(new char[0]);
    }
}
