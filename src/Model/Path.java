package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a path with positions and associated cards.
 */
public class Path {
    public String pathName;
    public int lastCardPlayedValue;
    public final int pathIndex;
    public final List<Position> positions; // List of positions in this path

    /**
     * Constructs a Path with a specific name, index, and associated pawn.
     * Automatically marks positions 2, 4, 6, 8, and 9 as finding positions.
     *
     * @param pathName  the name of the path.
     * @param pathIndex the index of the path.
     */
    public Path(String pathName, int pathIndex) {
        this.pathName = pathName;
        this.lastCardPlayedValue = -1;
        this.pathIndex = pathIndex;
        this.positions = new ArrayList<>();
        initializePositions();
        System.out.println("Path created: " + pathName + " with " + positions.size() + " positions.");
    }

    public int getLastCardPlayedValue(){
        return lastCardPlayedValue;
    }

    private void initializePositions() {
        for (int i = 1; i <= this.pathIndex; i++) {
            Position pos;
            // Mark even indices as finding positions; additionally mark index 9 if it exists (palace)
            if (i % 2 == 0 || i == 9) {
                pos = new Position(i, this, true);
            } else {
                pos = new Position(i, this, false);
            }
            positions.add(pos);
            System.out.println(
                    "Position created: Index=" + pos.getIndex() +
                            ", isFindingPosition=" + pos.isFindingPosition() +
                            ", Finding=" + (pos.getFinding() != null)
            );
        }
    }


    /**
     * Returns the name of the path.
     * @return the path name.
     */
    public String getPathName() {
        return pathName;
    }

    /**
     * Updates the value of the last card played on this path.
     * @param value the new value to set.
     */
    public void setLastCardPlayedValue(int value) {
        this.lastCardPlayedValue = value;
    }


    /**
     * Returns the list of positions in this path.
     * @return the list of positions.
     */
    public List<Position> getPositions() {
        return positions;
    }

    /**
     * Finds a position by its index.
     * @param index the index to find.
     * @return the position with the given index, or null if not found.
     */
    public Position getPositionByIndex(int index) {
        return (index >= 1 && index <= positions.size()) ? positions.get(index - 1) : null;
    }


    /**
     * Returns the first pawn of a specific player on this path.
     * @param player the player whose pawn is to be found.
     * @return the player's pawn or null if not found.
     */
    public Pawn getPlayerPawn(Player player) {
        for (Position position : positions) {
            for (Pawn p : position.getPawns()) {
                if (p.getPlayer().equals(player)) {
                    return p;
                }
            }
        }
        return null;
    }


    /**
     * Returns the first opponent pawn on this path (relative to the active player).
     * @param activePlayer the player whose opponent we are searching for.
     * @return an opponent pawn on this path, or null if none found.
     */
    public Pawn getOpponentPawn(Player activePlayer) {
        for (Position position : positions) {
            for (Pawn p : position.getPawns()) {
                if (!p.getPlayer().equals(activePlayer)) {
                    return p;
                }
            }
        }
        return null; // no opponent pawn found
    }

}
