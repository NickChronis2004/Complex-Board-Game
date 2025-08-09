package Model;

/**
 * Represents a finding in the game.
 */
public class Finding {
    private final String name;
    private final boolean isRare;
    private final int points;
    private final String type;

    /**
     * Constructs a Finding with a specified name, rarity, path, and points.
     * @param name the name of the finding.
     * @param isRare whether the finding is rare.
     * @param points the points the finding awards.
     */
    public Finding(String name, boolean isRare, int points , String type) {
        this.name = name;
        this.isRare = isRare;
        this.points = points;
        this.type = type;
    }

    /**
     * Returns the name of the finding.
     * @return the name of the finding.
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the finding is rare.
     * @return true if the finding is rare, false otherwise.
     */
    public boolean isRare() {
        return isRare;
    }

    /**
     * Returns the points the finding awards.
     * @return the points the finding awards.
     */
    public int getPoints() {
        return points;
    }

    /**
     * Returns the type of the finding.
     * @return the type of the finding.
     */
    public String getType() {
        return type;
    }

}
