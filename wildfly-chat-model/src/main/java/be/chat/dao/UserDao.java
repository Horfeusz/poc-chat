package be.chat.dao;

import be.chat.model.User;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@LocalBean
public class UserDao {

    @PersistenceContext(unitName = "pocrepository")
    private EntityManager em;


    public User findByLogin(String login) {
        return em.find(User.class, login);
    }

}
