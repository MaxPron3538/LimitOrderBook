package main;

public enum TypeMarketOrder {
    sell("sell"),
    buy("buy");

    private final String type;

    TypeMarketOrder(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
