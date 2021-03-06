package be.chat;

import be.chat.dto.MessageDTO;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.time.Instant;
import java.util.Date;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * Hello world!
 */
public class App {

    private static Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws NamingException {
        invokeStatelessBean();
    }

    private static void invokeStatelessBean() throws NamingException {
        final ChatRemote chat = lookupRemoteChat();

        logger.info("Obtained a remote stateless calculator for invocation");

        // invoke on the remote bean
        chat.sendMessageDTO(MessageDTO.builder()
                .owner("App-to-Wildfly")
                .time(Date.from(Instant.now()))
                .message("Wiadmość ze świata")
                .build());
    }

    private static ChatRemote lookupRemoteChat() throws NamingException {
        final Hashtable jndiProperties = new Hashtable();

        jndiProperties.put(Context.SECURITY_PRINCIPAL, "frank");
        jndiProperties.put(Context.SECURITY_CREDENTIALS, "password123");
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8090");

        final Context context = new InitialContext(jndiProperties);
        // The app name is the application name of the deployed EJBs. This is typically the ear name
        // without the .ear suffix. However, the application name could be overridden in the application.xml of the
        // EJB deployment on the server.
        // Since we haven't deployed the application as a .ear, the app name for us will be an empty string
        final String appName = "";
        // This is the module name of the deployed EJBs on the server. This is typically the jar name of the
        // EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
        // In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar, so the module name is
        // jboss-as-ejb-remote-app
        final String moduleName = "wildfly-chat";
        // AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
        // our EJB deployment, so this is an empty string
        final String distinctName = "";
        // The EJB name which by default is the simple class name of the bean implementation class
        final String beanName = "ChatBean";
        // the remote view fully qualified class name
        final String viewClassName = ChatRemote.class.getName();
        // let's do the lookup
        return (ChatRemote) context.lookup("ejb:" + appName + "/" + moduleName + "/" + beanName + "!" + viewClassName);
    }


}
