package be.chat.remote;

import be.chat.ChatRemote;
import com.google.common.base.Throwables;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.glassfish.internal.api.ORBLocator;

import javax.naming.*;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RemoteBeanUtil {

    private static final String REMOTE_HOST = "127.0.0.1";

    private static final String REMOTE_PORT = "3700";

    private static Logger logger = Logger.getLogger(RemoteBeanUtil.class.getName());

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> lookup(Class<T> remoteClass) {
        final Properties props = new Properties();

        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.enterprise.naming.SerialInitContextFactory");
        props.setProperty(Context.URL_PKG_PREFIXES,
                "com.sun.enterprise.naming");
        props.setProperty(Context.STATE_FACTORIES,
                "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
        props.setProperty(ORBLocator.OMG_ORB_INIT_HOST_PROPERTY, REMOTE_HOST);
        props.setProperty(ORBLocator.OMG_ORB_INIT_PORT_PROPERTY, ORBLocator.DEFAULT_ORB_INIT_PORT);

        try {
            final Context context = new InitialContext(props);
            Object object = getObjectInstance(context, context.lookup(ChatRemote.class.getName()));
            return Optional.ofNullable(object)
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

    private static Object getObjectInstance(Context ic, Object obj) throws NamingException {
        Reference ref = (Reference) obj;
        RefAddr refAddr = ref.get("url");
        Object genericRemoteHomeObj = ic.lookup((String) refAddr.getContent());
        String busInterface = ref.getClassName();

        //We get it object type of CORBAObjectImpl. It is not known what to do with him.
        //Using the gf-client library from ejb-full-container from GlassFish causes conflicts with WildFly
        //Below failed attempt copy of the GlassFish code

        return GlassFishEJBUtil.lookupRemote30BusinessObject(genericRemoteHomeObj, busInterface);
    }

}
