package battleships.repositories;

import battleships.domain.Game.BattleshipGame;
import battleships.domain.user.RegisteredUser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private SessionFactory sessionFactory;


    @Nullable
    public RegisteredUser getUser(String username) {
        RegisteredUser u = null;
        try (Session session = sessionFactory.openSession()) {
            u = session.get(RegisteredUser.class, username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return u;
    }

    public String saveUser(RegisteredUser registeredUser) {
        Transaction t = null;
        try (Session session = sessionFactory.openSession()) {
            t = session.beginTransaction();
            session.saveOrUpdate(registeredUser);
            t.commit();
            return registeredUser.getUsername();
        } catch (Exception e) {
            if (t != null && t.getStatus().canRollback()) {
                t.rollback();
            }
            e.printStackTrace();
        }
        return null;
    }

    public List<BattleshipGame> getUserGames(String username) {
        List<BattleshipGame> result = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            RegisteredUser u = session.get(RegisteredUser.class, username);
            if (u != null) result = u.getBattleshipGames(); //games will be loaded when trying to access it
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public void remove(Object object) {
        Transaction t = null;
        try (Session session = sessionFactory.openSession()) {
            t = session.beginTransaction();
            session.delete(object);
            t.commit();
        } catch (Exception e) {
            if (t != null && t.getStatus().canRollback()) {
                t.rollback();
            }
            e.printStackTrace();
        }
    }

    public boolean userExists(String username) {
        if (username == null) return false;
        int result = 0;
        try (Session session = sessionFactory.openSession()) {
            String query = "SELECT COUNT(*) FROM user WHERE username = :name; ";
            result = (int) session
                    .createSQLQuery(query)
                    .setParameter("name", username)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result > 0;
    }
}
