package battleships.domain.Game;

import lombok.Getter;
import lombok.Setter;

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
    private Game game;

    @NotEmpty
    private String username;

    @Min(1)
    @Max(5)
    private int size;

    @OneToMany(mappedBy = "ship")
    private List<ShipPart> parts;


    public Ship() {
        parts = new ArrayList<>();
    }

    public Ship(int size, String username) throws Exception {
        if (size < 1 || size > 5) throw new Exception("Ship is too big");
        this.size = size;
        this.username = username;
        parts = new ArrayList<>();
    }


    public boolean destroy(int x, int y) {
        AtomicBoolean result = new AtomicBoolean(false);
        parts.stream().filter(part -> part.getX() == x && part.getY() == y).findFirst().ifPresent(part -> {
            part.setDestroyed(true);
            result.set(true);
        });
        return result.get();
    }

    public boolean addPart(ShipPart part) {
        if (!parts.contains(part) && parts.size() < size) {
            parts.add(part);
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
