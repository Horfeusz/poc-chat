package be.chat.remote;

import be.chat.ChatRemote;
import com.google.common.base.Throwables;
import com.sun.enterprise.security.ee.auth.login.ProgrammaticLogin;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.*;
import java.security.Principal;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

@Stateless
@LocalBean
@PermitAll
public class GlassFishRemoteUtil {

    private static final String REMOTE_HOST = "127.0.0.1";

    private static final String REMOTE_PORT = "3700";

    private static final String AUTH_CONF_PATH = "C:\\tmp\\auth.conf";


    private Logger logger = Logger.getLogger(getClass().getName());

    @Resource
    private SessionContext sessionContext;

    private String getCallerPrincipalName() {
        return Optional.ofNullable(sessionContext)
                .map(SessionContext::getCallerPrincipal)
                .map(Principal::getName).orElseThrow(IllegalStateException::new);
    }

    private String getCallerPrincipalPassword() {
        //TODO take the Password from ... ???
        return "password123";
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

        System.setProperty("java.security.auth.login.config", AUTH_CONF_PATH);
        if (new ProgrammaticLogin().login(getCallerPrincipalName(),
                getCallerPrincipalPassword().toCharArray())) {
            try {
                final Context context = new InitialContext(props);
                return Optional.ofNullable(context.lookup(ChatRemote.class.getName()))
                        .map(o -> (T) o);
            } catch (NamingException e) {
                logger.warning(Throwables.getStackTraceAsString(e));
            }
        }
        return Optional.empty();
    }

}
