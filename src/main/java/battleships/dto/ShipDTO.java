package battleships.dto;

import battleships.domain.ship.ShipOrientation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipDTO {
    private String username;
    private int size;
    private ShipOrientation shipOrientation;
    private int x;
    private int y;
}
