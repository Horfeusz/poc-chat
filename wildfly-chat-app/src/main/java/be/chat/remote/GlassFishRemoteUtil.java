package be.chat.remote;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.sun.enterprise.security.SecurityServicesUtil;
import com.sun.enterprise.security.UsernamePasswordStore;
import com.sun.enterprise.security.auth.login.LoginContextDriver;
import com.sun.enterprise.security.common.Util;
import com.sun.enterprise.security.ee.auth.login.ProgrammaticLogin;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
@PermitAll
public class GlassFishRemoteUtil {

    private static final String REMOTE_HOST = "127.0.0.1";

    private static final String REMOTE_PORT = "3700";

    private Logger logger = Logger.getLogger(getClass().getName());

    @Resource
    private SessionContext sessionContext;

    private String getCallerPrincipalName() {
        return Optional.ofNullable(sessionContext)
                .map(SessionContext::getCallerPrincipal)
                .map(Principal::getName).orElseThrow(IllegalStateException::new);
    }

    private char[] getCallerPrincipalPassword() {
        //TODO take the Password from ... ???
        return "password123".toCharArray();
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> lookup(Class<T> remoteClass) {
        Preconditions.checkArgument(Objects.nonNull(remoteClass));

        final Properties props = new Properties();

        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.enterprise.naming.SerialInitContextFactory");
        props.setProperty(Context.URL_PKG_PREFIXES,
                "com.sun.enterprise.naming");
        props.setProperty(Context.STATE_FACTORIES,
                "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
        props.setProperty("org.omg.CORBA.ORBInitialHost", REMOTE_HOST);
        props.setProperty("org.omg.CORBA.ORBInitialPort", REMOTE_PORT);

        final ProgrammaticLogin programmaticLogin = new ProgrammaticLogin();
        boolean authenticated = programmaticLogin.login(getCallerPrincipalName(),
                getCallerPrincipalPassword());
        if (authenticated) {
            try {
                final Context context = new InitialContext(props);
                return Optional.ofNullable(context.lookup(remoteClass.getName()))
                        .map(o -> (T) o);
            } catch (Exception e) {
                logger.warning(Throwables.getStackTraceAsString(e));
            }
        }
        return Optional.empty();
    }


    @Deprecated
    private boolean login() {
        final String user = "ejbuser";
        final char[] password = "password123".toCharArray();
        final String realm = "demoFsRealm";
        Boolean authenticated = false;
        try {
            authenticated = AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> {
                if ((SecurityServicesUtil.getInstance() == null || !SecurityServicesUtil.getInstance().isServer()) && !Util.isEmbeddedServer()) {
                    int type = 1;
                    UsernamePasswordStore.set(user, password);
                    try {
                        LoginContextDriver.doClientLogin(type, new GlassFishRemoteCallbackHandler());
                    } finally {
                        UsernamePasswordStore.resetThreadLocalOnly();
                    }
                } else {
                    LoginContextDriver.login(user, password, realm);
                }
                return true;
            });
        } catch (Exception var7) {
            logger.log(Level.SEVERE, "prog.login.failed", var7);
            authenticated = false;
        }
        return authenticated;
    }

}
