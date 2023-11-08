package main;

public enum TypeLimitOrder {
    ask("ask"),
    bid("bid");

    private final String type;

    TypeLimitOrder(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
