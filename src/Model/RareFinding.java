package Model;

/**
 * Represents a Rare Finding in the game.
 */
public class RareFinding extends Finding {
    private final String pathName; // The path associated with the finding
    /**
     * Constructs a RareFinding with a specified name, path, and points.
     *
     * @param name     the name of the finding.
     * @param points   the points the finding awards.
     */
    public RareFinding(String name, int points ,String pathName) {
        super(name, true, points, "rare");
        this.pathName = pathName;
    }

    public String getPathName() {
        return pathName;
    }
}
