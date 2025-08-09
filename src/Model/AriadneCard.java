package Model;

/**
 * Represents an AriadneCard, granting +2 steps to a pawn but cannot be played as the first card.
 */
public class AriadneCard extends Card {

    /**
     * Constructs an AriadneCard associated with a specific path.
     * @param pathName the path associated with the card.
     */
    public AriadneCard(String pathName , String imageName) {
        super(pathName ,imageName);
    }

    /**
     * Plays the AriadneCard, moving the pawn 2 steps forward if allowed.
     * @param player the player who plays the card.
     * @param pawn the pawn to move.
     * @return true if the card is successfully played, false otherwise.
     */
    public boolean playAriadneCard(Player player, Pawn pawn) {
        // Check if the player has already played a card
        if (!player.hasPlayedAnyCard()) {
            System.out.println("AriadneCard cannot be played first!");
            return false;
        }

        // Check if the pawn belongs to the same path as the card
        if (pawn.getPath() == null || pawn.getPath().getPathName() == null ||
                !pawn.getPath().getPathName().equalsIgnoreCase(this.getPalaceName())) {
            System.out.println("Error: The pawn does not belong to the same path as the AriadneCard!");
            return false;
        }

        // Move the pawn 2 steps forward
        pawn.move(2);
        System.out.println("AriadneCard: " + player.getName() + "'s pawn moved 2 steps forward.");
        return true;
    }
}
