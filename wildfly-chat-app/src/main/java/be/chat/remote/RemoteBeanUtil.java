package be.chat.remote;

import be.chat.ChatRemote;
import com.google.common.base.Throwables;

import javax.annotation.Resource;
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
public class RemoteBeanUtil {

    private static final String REMOTE_HOST = "127.0.0.1";

    private static final String REMOTE_PORT = "3700";

    private Logger logger = Logger.getLogger(RemoteBeanUtil.class.getName());

    @Resource
    private SessionContext sessionContext;

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
        props.put(Context.SECURITY_PRINCIPAL, Optional.ofNullable(sessionContext)
                .map(SessionContext::getCallerPrincipal)
                .map(Principal::getName));
        props.put(Context.SECURITY_CREDENTIALS, "password123");

        try {
            final Context context = new InitialContext(props);
            return Optional.ofNullable(context.lookup(ChatRemote.class.getName()))
                    .map(o -> {
                        logger.info("I have remote Object");
                        return (T) o;
                    });
        } catch (NamingException e) {
            logger.warning("I not found remote bean: " + remoteClass.getName());
            logger.warning(Throwables.getStackTraceAsString(e));
        }
        return Optional.empty();
    }

}
