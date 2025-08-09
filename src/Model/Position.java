package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a position on a path.
 */
public class Position {
    private final int index;              // The index of the position within the path
    private final boolean isFindingPosition; // Indicates if this position can hold a finding
    private final List<Pawn> pawns;       // The pawns currently located in this position
    private Finding finding;             // The finding in this position
    private final Path path;

    /**
     * Constructs a Position.
     * @param index the index of the position within the path.
     * @param isFindingPosition whether this position can hold a finding.
     */
    public Position(int index, Path path ,boolean isFindingPosition) {
        this.index = index;
        this.isFindingPosition = isFindingPosition;
        this.pawns = new ArrayList<>();  // Initially, no pawns in this position
        this.finding = null;            // Initially, no finding in this position
        this.path = path;

        System.out.println("Position created: index " + index + " on path " + path.getPathName());
    }

    /**
     * Returns the index of the position.
     * @return the index of the position.
     */
    public int getIndex() {
        return index;
    }

    public Path getPath(){
        return path;
    }

    /**
     * Checks if this position can hold a finding.
     * @return true if it can hold a finding, false otherwise.
     */
    public boolean isFindingPosition() {
        return isFindingPosition;
    }

    /**
     * Returns the list of pawns currently in this position.
     * @return an unmodifiable list of pawns in this position.
     */
    public List<Pawn> getPawns() {
        return List.copyOf(pawns);
    }

    /**
     * Attempts to add a pawn to this position.
     * <p>
     *  - We allow up to 2 pawns in total.
     *  - We do NOT allow a second pawn from the same player.
     *
     * @param pawn the pawn to place in this position.
     * @return true if the pawn was added successfully, false otherwise.
     */
    public boolean addPawn(Pawn pawn) {

        if (pawns.size() >= 2) {
            System.out.println("Position " + index + " already has 2 pawns!");
            return false;
        }

        for (Pawn existingPawn : pawns) {
            if (existingPawn.getPlayer().equals(pawn.getPlayer())) {
                System.out.println("This position already has a pawn for the same player!");
                return false;
            }
        }

        pawns.add(pawn);
        return true;
    }

    /**
     * Removes a pawn from this position.
     *
     * @param pawn the pawn to remove.
     */
    public void removePawn(Pawn pawn) {
        pawns.remove(pawn);
    }

    /**
     * Returns the finding in this position (if any).
     * @return the finding in this position, or null if none.
     */
    public Finding getFinding() {
        return finding;
    }

    /**
     * Places a finding in this position (only if this position can hold a finding).
     * @param finding the finding to place in this position.
     */
    public void setFinding(Finding finding) {
        if (isFindingPosition) {
            this.finding = finding;
        } else {
            System.out.println("This position cannot hold a finding.");
        }
    }

    /**
     * Removes the finding from this position.
     */
    public void removeFinding() {
        this.finding = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Position{");
        sb.append("index=").append(index);
        sb.append(", finding=").append((finding != null) ? finding.getName() : "none");
        sb.append(", pawns=[");
        for (Pawn p : pawns) {
            sb.append(p.getType())
                    .append(" of ")
                    .append(p.getPlayer().getName())
                    .append(", ");
        }
        if (!pawns.isEmpty()) {
            sb.setLength(sb.length() - 2); // remove last comma and space
        }
        sb.append("]}");
        return sb.toString();
    }
}
