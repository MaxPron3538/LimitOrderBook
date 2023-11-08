package main;

public enum TypeQuery {
    bestAsk("best_ask"),
    bestBid("best_bid"),
    size("size");

    private final String type;

    TypeQuery(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
