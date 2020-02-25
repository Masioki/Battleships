package battleships.repositories;

import battleships.domain.Game.BattleshipGame;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GameRepository {

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

    public int saveBattleshipGame(BattleshipGame game) throws Exception {
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
            throw new Exception();
        }
    }


    public void removeBattleshipGame(BattleshipGame object) {
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
