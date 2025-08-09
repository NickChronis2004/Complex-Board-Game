package Model;

/**
 * Represents an Archaeologist pawn, capable of collecting findings.
 */
public class Archaeologist extends Pawn {

    /**
     * Constructs an Archaeologist pawn.
     *
     * @param player           The player who owns this pawn.
     * @param initialPosition  The initial position of the pawn.
     * @param path             The path associated with the pawn.
     */
    public Archaeologist(Player player, Position initialPosition, Path path) {
        super("Archaeologist", player, initialPosition, path, false);
    }


    /**
     * Opens a box at the current position to collect a finding.
     *
     * @param finding The finding to be collected or photographed.
     * @param position The position containing the finding.
     * @param player  The player who collects the finding.
     * @return true if the finding is successfully handled, false otherwise.
     */
    public boolean openBox(Finding finding, Position position, Player player) {
        if (finding == null) {
            System.out.println("No finding in this box to collect.");
            return false;
        }

        boolean collected = false;

        if (finding.isRare()) {
            System.out.println("Collected a rare finding: " + finding.getName());
            player.collectFinding(this, position);
            collected = true;
        } else if (finding.getType().equalsIgnoreCase("statue")) {
            System.out.println("Collected a statue: " + finding.getName());
            player.collectFinding(this, position);
            collected = true;
        } else if (finding instanceof FrescoFinding fresco) {
            if (!fresco.isPhotographedBy(player)) {
                fresco.photograph(player);
                System.out.println("Photographed a fresco: " + fresco.getName());
                player.collectFinding(this, position);
                collected = true;
            } else {
                System.out.println("This fresco has already been photographed by " + player.getName() + ".");
            }
        } else {
            System.out.println("Unknown finding type. Cannot collect.");
        }

        // if the collection was successful, reveal the pawn if it hasn't been revealed yet
        if (collected && this.isHidden()) {
            revealPawn();
            System.out.println("Archaeologist has been revealed due to performing an openBox action.");
        }

        return collected;
    }


    /**
     * Moves the Archaeologist by the specified number of steps.
     * Overrides the move method to include additional logic if necessary.
     *
     * @param steps the number of steps to move (positive or negative).
     * @return true if the movement was successful, false otherwise.
     */
    @Override
    public boolean move(int steps) {
        return super.move(steps);
    }

}
