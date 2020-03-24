package be.chat.remote;

import be.chat.ChatException;
import be.chat.dao.UserDao;
import be.chat.model.User;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Optional;
import java.util.logging.Logger;

@Stateless
@LocalBean
@PermitAll
public class RemoteProxy {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Resource
    private SessionContext sessionContext;

    @Inject
    private ContextFactory contextFactory;

    @EJB
    private UserDao userDao;

    private <T> String getJndiRemoteAddress(Class<T> remoteInterfaceClass) {
        //TODO take the JNDI name from the DB
        return remoteInterfaceClass.getName();
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> lookup(Class<T> remoteInterfaceClass) {
        String jndiRemoteAddress = getJndiRemoteAddress(remoteInterfaceClass);
        User user = userDao.findByLogin(sessionContext.getCallerPrincipal().getName());
        logger.info("I am looking remote bean: " + jndiRemoteAddress + " for " + user);
        try {
            Context context = contextFactory.getContext(user)
                    .orElseThrow(IllegalStateException::new);
            return Optional.ofNullable(context.lookup(jndiRemoteAddress))
                    .map(o -> (T) o);
        } catch (NamingException e) {
            throw new ChatException(e);
        }
    }

}
