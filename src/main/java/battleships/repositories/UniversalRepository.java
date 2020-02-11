package battleships.repositories;

import battleships.domain.Game.BattleshipGame;
import battleships.domain.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UniversalRepository {

    @Autowired
    private SessionFactory sessionFactory;


    public BattleshipGame getBattleshipGame(int gameID) {
        BattleshipGame game = null;
        try (Session session = sessionFactory.openSession()) {
            game = session.get(BattleshipGame.class, gameID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return game;
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

    //TODO: get without childs
    /*
    public User getUserWithoutChild(String username) {

    }
    */
}
