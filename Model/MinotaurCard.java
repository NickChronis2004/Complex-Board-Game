package src.Model;

/**
 * Represents a MinotaurCard, allowing a player to attack a target pawn.
 */
public class MinotaurCard extends Card {

    /**
     * Constructs a MinotaurCard associated with a specific path.
     */
    public MinotaurCard(String palaceName, String imageName) {
        super(palaceName, imageName);
    }


    /**
         * Attacks a target pawn, applying specific effects based on the pawn type.
         * Additionally, reveals the pawn if it hasn't been revealed yet.
         *
         * @param target the pawn to attack.
         * @param attackingPlayer the player who plays the card.
         */
        public void attack(Pawn target, Player attackingPlayer) {
            if (target == null) {
                System.out.println("Error: Target pawn does not exist.");
                return;
            }

            if (target.isCheckpointReached()) {
                System.out.println("Cannot attack. Target pawn has reached the checkpoint.");
                return;
            }

            // Apply specific effects based on the pawn type
            if (target instanceof Archaeologist) {
                boolean moved = target.move(-2);
                if (moved) {
                    // Reveal the pawn if it hasn't been revealed yet
                    if (target.isHidden()) {
                        target.revealPawn();
                        System.out.println(target.getType() + " has been revealed due to the attack.");
                    }
                    System.out.println(attackingPlayer.getName() + " attacked an Archaeologist! Pawn moved 2 positions back.");
                } else {
                    System.out.println("Attack failed.");
                }
            } else if (target instanceof Theseus) {
                ((Theseus) target).freeze(); // Freeze the pawn
                System.out.println(attackingPlayer.getName() + " attacked Theseus! Pawn is frozen for 1 round.");
            } else {
                System.out.println("Error: Unknown pawn type.");
            }
        }
}

