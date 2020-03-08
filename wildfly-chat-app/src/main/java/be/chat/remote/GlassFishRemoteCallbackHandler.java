package be.chat.remote;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.jboss.security.auth.callback.ObjectCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import java.util.Optional;

public class GlassFishRemoteCallbackHandler implements CallbackHandler {

    @Override
    public void handle(Callback[] callbacks) {
        Optional.ofNullable(callbacks)
                .map(Lists::newArrayList)
                .filter(CollectionUtils::isNotEmpty)
                .ifPresent(callbackCollection -> callbackCollection
                        .forEach(callback -> {
                            if (callback instanceof NameCallback) {
                                ((NameCallback) callback).setName("ejbuser");
                            } else if (callback instanceof PasswordCallback) {
                                ((PasswordCallback) callback).setPassword("password123".toCharArray());
                            } else if (callback instanceof ObjectCallback) {
                                ((ObjectCallback) callback).setCredential("password123".toCharArray());
                            }
                        }));

        //new GlassFishRemoteLoginDialog("ejbuser", "password123");
    }
}
