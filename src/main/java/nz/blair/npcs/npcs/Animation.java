package nz.blair.npcs.npcs;

@SuppressWarnings("unused") // This class is used by other plugins
public enum Animation {
    SWING_ARM(0),
    TAKE_DAMAGE(1),
    LEAVE_BED(2),
    EAT_FOOD(3),
    CRITICAL_EFFECT(4),
    MAGIC_CRITICAL_EFFECT(5);

    private final int id;

    Animation(int id) {
        this.id = id;
    }

    /**
     * Get the id of the animation
     *
     * @return The id of the animation
     */
    public int getId() {
        return id;
    }
}
