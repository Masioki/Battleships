package battleships.domain;

public enum UserRole {
    GUEST(3),
    USER(2),
    ADMIN(1);

    private final int value;

    UserRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
