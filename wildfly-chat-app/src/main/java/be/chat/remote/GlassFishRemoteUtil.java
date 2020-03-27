package be.chat.remote;

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
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Deprecated
public class GlassFishRemoteUtil {

    //private static final String REMOTE_HOST = "192.168.65.209";
    private static final String REMOTE_HOST = "localhost";

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
        return "Hendrik123".toCharArray();
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> lookup(Class<T> remoteClass) {

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
                e.printStackTrace();
            }
        } else {
            logger.warning("Not authenticated !");
        }
        return Optional.empty();
    }

}
