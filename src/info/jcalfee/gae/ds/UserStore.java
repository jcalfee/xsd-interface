package info.jcalfee.gae.ds;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UserStore {

    private static Logger log = Logger.getLogger(UserStore.class.getName());

    /**
     * @param user
     * @param callback
     *            or null
     * @return data store user (synced with logged in Google user)
     */
    public static User findCurrentUser() {
        UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User gUser =
            userService.getCurrentUser();

        User user = new User();
        user.from(gUser);
        user.setAdmin(userService.isUserAdmin());
        User existingUser = find(user);
        if (existingUser == null) {
            // first encounter
            log.info("Creating " + user);
            PersistenceManager pm = PMF.pm();
            pm.makePersistent(user);
        } else {
            // existing
            if (user.to(existingUser))
                log.info("Updated " + user);
            else
                log.info("Found " + user);

            user = existingUser;
        }
        return user;
    }

    /**
     * @param id
     * @return User
     */
    public static User findUserById(long id) {
        Key k = new KeyFactory.Builder("User", id).getKey();
        PersistenceManager pm = PMF.pm();
        return pm.getObjectById(User.class, k);
    }

    @SuppressWarnings("unchecked")
    public static User findUserByEmail(String email) {
        email = email.toLowerCase();
        PersistenceManager pm = PMF.pm();
        Query query = pm.newQuery(User.class);
        query.setFilter("email == emailParm");
        query.declareParameters("String emailParm");
        try {
            List<User> results = (List<User>) query.execute(email);

            Iterator<User> it = results.iterator();
            if (it.hasNext())
                return it.next();

        } finally {
            query.closeAll();
        }
        return null;
    }

    /**
     * Restrict, Administration Access Only
     * 
     * @param user
     * @return
     */
    @SuppressWarnings("unchecked")
    private static User find(User user) {
        log.info("Find " + user);
        PersistenceManager pm = PMF.pm();
        Query query = pm.newQuery(User.class);
        query
            .setFilter("authDomain == authDomainParm && externalId == externalIdParm");
        query.declareParameters("String authDomainParm, String externalIdParm");
        try {
            List<User> results =
                (List<User>) query.execute(user.getAuthDomain(), user
                    .getExternalId());

            Iterator<User> it = results.iterator();
            if (it.hasNext()) {
                User userIt = it.next();
                MemCache.service().put(userIt.hashCode(), userIt);
                return userIt;
            }

        } finally {
            query.closeAll();
        }
        return null;
    }

}
