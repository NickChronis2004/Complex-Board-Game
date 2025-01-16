package src.Model;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Fresco Finding in the game.
 */
public class FrescoFinding extends Finding {
    private final Set<Player> photographedByPlayers; // players who have photographed the fresco

    /**
     * Constructs a FrescoFinding with specified details.
     * @param name the name of the fresco.
     * @param points the points the fresco awards.
     */
    public FrescoFinding(String name, int points) {
        super(name, false, points , "Fresco");
        this.photographedByPlayers = new HashSet<>();
    }

    /**
     * Checks if a specific player has photographed the fresco.
     * @param player the player to check.
     * @return true if the player has photographed the fresco, false otherwise.
     */
    public boolean isPhotographedBy(Player player) {
        return photographedByPlayers.contains(player);
    }

    /**
     * Marks the fresco as photographed by a specific player.
     * @param player the player photographing the fresco.
     * @return true if successfully photographed, false if already photographed by the player.
     */
    public boolean photograph(Player player) {
        if (isPhotographedBy(player)) {
            System.out.println(player.getName() + " has already photographed this fresco.");
            return false;
        }
        photographedByPlayers.add(player);
        System.out.println(player.getName() + " photographed the fresco: " + getName());
        return true;
    }
}