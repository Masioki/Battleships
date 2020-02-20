package battleships.repositories;

import battleships.domain.Game.BattleshipGameImpl;
import battleships.domain.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UniversalRepository {

    @Autowired
    private SessionFactory sessionFactory;


    public BattleshipGameImpl getBattleshipGame(int gameID) {
        BattleshipGameImpl game = null;
        try (Session session = sessionFactory.openSession()) {
            game = session.get(BattleshipGameImpl.class, gameID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return game;
    }

    public int saveBattleshipGame(BattleshipGameImpl game) throws Exception {
        Transaction t = null;
        try (Session session = sessionFactory.openSession()) {
            t = session.beginTransaction();
            session.saveOrUpdate(game);
            t.commit();
            return game.getGameID();
        } catch (Exception e) {
            if (t != null && t.getStatus().canRollback()) {
                t.rollback();
            }
            e.printStackTrace();
        }
        throw new Exception();
    }

    public User getUser(String username) {
        User u = null;
        try (Session session = sessionFactory.openSession()) {
            u = session.get(User.class, username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return u;
    }

    public String saveUser(User user) throws Exception {
        Transaction t = null;
        try (Session session = sessionFactory.openSession()) {
            t = session.beginTransaction();
            session.saveOrUpdate(user);
            t.commit();
            return user.getUsername();
        } catch (Exception e) {
            if (t != null && t.getStatus().canRollback()) {
                t.rollback();
            }
            e.printStackTrace();
        }
        throw new Exception();
    }

    public List<BattleshipGameImpl> getUserGames(String username) {
        List<BattleshipGameImpl> result = null;
        try (Session session = sessionFactory.openSession()) {
            User u = session.get(User.class, username);
            if (u != null) result = u.getBattleshipGameImpls(); //games will be loaded when trying to access it
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

}
