package nz.blair.npcs.npcs;

public class Skin {
    private final String value;
    private final String signature;

    public Skin(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    /**
     * Get the value of the skin.
     *
     * @return The value of the skin
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the Mojang signature of the skin.
     *
     * @return The Mojang signature of the skin
     */
    public String getSignature() {
        return signature;
    }
}
