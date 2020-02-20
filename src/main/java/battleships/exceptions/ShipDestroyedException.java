package battleships.exceptions;

public class ShipDestroyedException extends Exception {
    private final int x;
    private final int y;

    public ShipDestroyedException(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String getMessage() {
        return "Ship was destroyed during move: x=" + x + " y=" + y;
    }
}
