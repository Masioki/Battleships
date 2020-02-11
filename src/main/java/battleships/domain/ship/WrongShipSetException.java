package battleships.domain.ship;

public class WrongShipSetException extends Exception {

    private final String username;

    public WrongShipSetException(String username) {
        this.username = username;
    }

    @Override
    public String getMessage() {
        return "Illegal ship set for user: " + username;
    }
}
