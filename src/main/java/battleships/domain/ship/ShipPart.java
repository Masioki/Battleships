package battleships.domain.ship;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;


@Entity
@Getter
@Setter
public class ShipPart {

    @Id
    @GeneratedValue
    private int shipPartID;

    @ManyToOne
    private Ship ship;

    @Min(0)
    @Max(10)
    private int x;

    @Min(0)
    @Max(10)
    private int y;

    @NotNull
    private boolean destroyed;


    public ShipPart() {
        destroyed = false;
    }


    public ShipPart(int x, int y) {
        this.x = x;
        this.y = y;
        destroyed = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShipPart)) return false;
        ShipPart shipPart = (ShipPart) o;
        return x == shipPart.x &&
                y == shipPart.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
