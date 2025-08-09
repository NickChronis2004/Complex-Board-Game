package Model;

/**
 * Represents Theseus, a pawn with special abilities.
 */
public class Theseus extends Pawn {
    private boolean isFrozen;         // Flag indicating if the pawn is frozen
    public int remainingDestroys;    // Number of remaining destroys

    /**
     * Constructs a Theseus pawn.
     *
     * @param player           The player who owns this pawn.
     * @param initialPosition  The initial position of the pawn.
     * @param path             The path associated with the pawn.
     */
    public Theseus(Player player, Position initialPosition, Path path) {
        super("Theseus", player, initialPosition, path, false);
        this.isFrozen = false;
        this.remainingDestroys = 3;
    }

    /**
     * Freezes Theseus for one round and reveals the pawn.
     */
    public void freeze() {
        if (!isFrozen()) {
            this.isFrozen = true;
            revealPawn(); // Reveal Theseus when frozen
            System.out.println("Theseus is frozen for 1 round.");
        } else {
            System.out.println("Theseus is already frozen.");
        }
    }

    /**
     * Checks if Theseus is currently frozen.
     *
     * @return true if frozen, false otherwise.
     */
    public boolean isFrozen() {
        return isFrozen;
    }

    /**
     * Unfreezes Theseus.
     */
    public void unfreeze() {
        if (isFrozen) {
            this.isFrozen = false;
            System.out.println("Theseus is no longer frozen.");
        } else {
            System.out.println("Theseus is not frozen.");
        }
    }

    /**
     * Uses a special ability to destroy a box or bypass obstacles.
     *
     * @param findingPosition The position of the finding.
     * @return true if the box is destroyed, false otherwise.
     */
    public boolean destroyBox(Position findingPosition) {
        if (remainingDestroys <= 0) {
            System.out.println("Theseus has no remaining destroys.");
            return false;
        }
        if (findingPosition == null) {
            System.out.println("Invalid position. Theseus cannot destroy a box.");
            return false;
        }
        if (findingPosition.getFinding() == null) {
            System.out.println("No box to destroy at this position.");
            return false;
        }

        // Destroy the box
        findingPosition.setFinding(null);
        System.out.println("Theseus destroys a box at position " + findingPosition.getIndex() + "!");
        remainingDestroys--;

        // reveal the pawn if it hasn't been revealed yet
        if (this.isHidden()) {
            revealPawn();
            System.out.println("Theseus has been revealed due to performing a destroy action.");
        }

        return true;
    }

    /**
     * Moves Theseus by the specified number of steps.
     * Overrides the move method to include additional logic if necessary.
     *
     * @param steps the number of steps to move (positive or negative).
     * @return true if the movement was successful, false otherwise.
     */
    @Override
    public boolean move(int steps) {
        if (isFrozen()) {
            System.out.println("Theseus is frozen and cannot move this turn.");
            return false;
        }
        return super.move(steps);
    }
}
