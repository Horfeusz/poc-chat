package be.chat.remote;

import be.chat.model.User;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sun.enterprise.security.ee.auth.login.ProgrammaticLogin;

import javax.ejb.Singleton;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.*;
import java.util.logging.Logger;

public class ContextFactory {

    private static final String REMOTE_HOST_PROPERTY_NAME = "poc-chat-glassfish-host";

    private static final String REMOTE_PORT_PROPERTY_NAME = "poc-chat-glassfish-port";

    private static final String DEFAULT_REMOTE_HOST = "localhost";

    private static final String DEFAULT_REMOTE_PORT = "3700";

    public Optional<Context> getContext(User user) throws NamingException {
        Preconditions.checkArgument(Objects.nonNull(user), "No user data !!!");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user.getLogin()), "Login is empty !!!");

        final Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.enterprise.naming.SerialInitContextFactory");
        props.setProperty(Context.URL_PKG_PREFIXES, "com.sun.enterprise.naming");
        props.setProperty(Context.STATE_FACTORIES, "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
        props.setProperty("org.omg.CORBA.ORBInitialHost", getRemoteHost());
        props.setProperty("org.omg.CORBA.ORBInitialPort", getRemotePort());
        final ProgrammaticLogin programmaticLogin = new ProgrammaticLogin();
        boolean authenticated = programmaticLogin.login(user.getLogin(),
                Optional.ofNullable(user.getHashPassword())
                        .filter(s -> !Strings.isNullOrEmpty(s))
                        .map(String::toCharArray)
                        .orElse("".toCharArray()));
        return authenticated ? Optional.of(new InitialContext(props)) : Optional.empty();
    }

    private String getRemoteHost() {
        String remoteHost = System.getProperties().getProperty(REMOTE_HOST_PROPERTY_NAME);
        return Strings.isNullOrEmpty(remoteHost) ? DEFAULT_REMOTE_HOST : remoteHost;
    }

    private String getRemotePort() {
        String remotePort = System.getProperties().getProperty(REMOTE_PORT_PROPERTY_NAME);
        return Strings.isNullOrEmpty(remotePort) ? DEFAULT_REMOTE_PORT : remotePort;
    }

}
