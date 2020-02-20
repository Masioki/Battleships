package battleships.domain.ship;

import battleships.domain.Game.BattleshipGameImpl;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Entity
@Getter
@Setter
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int shipID;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BattleshipGameImpl battleshipGameImpl;

    @NotEmpty
    private String username;

    @NotEmpty
    private ShipOrientation orientation;

    @Min(1)
    @Max(5)
    private int size;

    @OneToMany(mappedBy = "ship", fetch = FetchType.EAGER)
    private List<ShipPart> parts;


    public Ship() {
        parts = new ArrayList<>();
    }


    public static Ship getShipWithoutParts(String username, int size, ShipOrientation orientation) throws Exception {
        if (size < 1 || size > 5) throw new Exception("Ship is too big");
        Ship s = new Ship();
        s.setSize(size);
        s.setUsername(username);
        s.setOrientation(orientation);
        return s;
    }

    public static Ship getShipWithParts(String username, int size, ShipOrientation orientation, int x, int y) throws Exception {
        Ship s = getShipWithoutParts(username, size, orientation);

        switch (orientation) {
            case VERTICAL -> {
                for (int i = y; i < y + size; i++)
                    if (!s.addPart(new ShipPart(x, i, s))) throw new Exception("Wrong data");
            }
            case HORIZONTAL -> {
                for (int i = x; i < x + size; i++)
                    if (!s.addPart(new ShipPart(i, y, s))) throw new Exception("Wrong data");
            }

        }
        return s;
    }


    public boolean contains(int x, int y) {
        return parts.stream().anyMatch(part -> part.getX() == x && part.getY() == y);
    }

    public boolean destroy(int x, int y) {
        AtomicBoolean result = new AtomicBoolean(false);
        parts.stream()
                .filter(part -> part.getX() == x && part.getY() == y && !part.isDestroyed())
                .findFirst()
                .ifPresent(part -> {
                    part.setDestroyed(true);
                    result.set(true);
                });
        return result.get();
    }

    public boolean addPart(ShipPart part) {
        if (parts == null) parts = new ArrayList<>();
        if (!parts.contains(part) && parts.size() < size) {
            parts.add(part); //TODO: is part in range?
            return true;
        }
        return false;
    }

    public boolean isDestroyed() {
        for (ShipPart p : parts) {
            if (!p.isDestroyed()) return false;
        }
        return true;
    }

}
