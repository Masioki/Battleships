package battleships.domain.Game;

import lombok.Getter;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Board {
    private final List<Point2D> ownAttacked;

    private final List<Point2D> ownShips;

    private final List<Point2D> opponentAttacked;

    private final List<Point2D> opponentDestroyed;


    public Board(String username, List<Move> moves, List<Point2D> ownShips) {
        ownAttacked = new ArrayList<>();
        this.ownShips = ownShips;
        opponentAttacked = new ArrayList<>();
        opponentDestroyed = new ArrayList<>();

        for (Move m : moves) {
            Point2D point = new Point2D.Double(m.getX(), m.getY());
            if (!m.getUsername().equals(username)) ownAttacked.add(point);
            else {
                if (m.isSuccess()) opponentDestroyed.add(point);
                opponentAttacked.add(point);
            }
        }
    }
}
