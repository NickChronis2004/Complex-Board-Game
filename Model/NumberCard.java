package src.Model;

/**
 * Represents a NumberCard, which has a value and can be used to move a pawn.
 */
public class NumberCard extends Card {
    private final int value;
    Path path; // The path associated with the number card

    /**
     * Constructs a NumberCard with a specified path name.
     * @param pathName the path associated with the card.
     */
    public NumberCard(int value, String pathName, String imageName ,Path path) {
        super(pathName ,imageName);
        this.value = value;
        this.path = path;
    }

    public Path getPath(){
        return path;
    }

    /**
     * Returns the value of the card.
     * @return the value of the card.
     */
    public int getValue() {
        return value;
    }

    /**
     * Updates the last played card value for the given path.
     * @param path the path to update.
     */
    public void updateLastPlayedCard(Path path) {
        if (path != null) {
            path.setLastCardPlayedValue(this.value);
            System.out.println("Path " + path.getPathName() + ": Last played card value updated to " + this.value);
        }
    }

    /**
     * Checks if this card is playable based on the last played card value.
     * @param playedCard the value of the last played card.
     * @return true if this card is playable, false otherwise.
     */
    public boolean isPlayable(NumberCard playedCard ,Path path) {
        if (playedCard.getValue() >= path.lastCardPlayedValue){
            updateLastPlayedCard(path);
            return true;
        }
        return false;
    }
}