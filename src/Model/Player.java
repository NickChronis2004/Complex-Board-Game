package Model;

import java.util.*;

/**
 * Represents a player in the game with their name, score, hand of cards, and statues collected.
 */
public class Player {
    private final String name; // The name of the player (e.g., Player 1)
    private final int id; // The ID of the player (e.g., 1)
    private int score; // The current score of the player
    public List<Card> hand; // The cards in the player's hand
    public boolean PlayedAnyCard;
    private List<Finding> playerFindings;

    /**
     * Constructs a Player with a specified name.
     * @param name the name of the player.
     */
    public Player(String name , int id) {
        this.name = name;
        this.score = 0;
        this.hand = new ArrayList<>(); // Initialize as a dynamic list
        this.playerFindings = new ArrayList<>();
        this.PlayedAnyCard = false;
        this.id = id;
    }

    /**
     * Returns the name of the player.
     * @return the name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the ID of the player.
     * @return the ID of the player.
     */
    public int getId() {
        return id;
    }

    /**
     */
    public void setPlayedAnyCard(boolean condition){
        PlayedAnyCard = condition;
    }

    /**
     * Returns true if the player has played any card, false otherwise.
     * @return true if the player has played any card, false otherwise.
     */
    public boolean hasPlayedAnyCard() {
        return PlayedAnyCard;
    }

    /**
     * Adds points to the player's score.
     * @param points the number of points to add.
     */
    public void addScore(int points) {
        score += points;
    }

    /**
     * Retrieves the current score of the player.
     * @return the current score.
     */
    public int getScore() {
        return score;
    }



    /**
     * Retrieves the cards in the player's hand.
     * @return a list of cards in the player's hand.
     */
    public List<Card> getCardsOnHand() {
        return hand;
    }

    /**
     * Draws a card from the deck and adds it to the player's hand.
     * Pre-condition: The deck is not empty.
     * Post-condition: A card is added to the player's hand if it has less than 8 cards.
     *
     * @param deck the deck from which to draw a card.
     */
    public void drawCard(Deck deck) {
        if (hand.size() < 8) {
            Card drawnCard = deck.topCard();
            if (deck.hasCards()) {
                hand.add(drawnCard);
                deck.popCard();
                return;
            }
            System.out.println("The deck is empty!");
        } else {
            System.out.println("Hand is full! Cannot draw more cards.");
        }
    }


    /**
     * Collects a finding from the current position on the path.
     *
     * @param archaeologist The archaeologist performing the action.
     * @param position      The position containing the finding.
     */
    public void collectFinding(Archaeologist archaeologist, Position position) {
        Finding finding = position.getFinding();

        if (finding == null) {
            System.out.println("No finding to collect at this position.");
            return;
        }

        // Attempt to open the box using the archaeologist
        if (archaeologist.openBox(finding, position, this)) {
            // if the finding is a fresco, it is not removed from the position
            if (finding instanceof FrescoFinding) {
                ((FrescoFinding) finding).isPhotographedBy(this);
                playerFindings.add(finding);
                System.out.println("Player " + getName() + " successfully photographed the fresco: " + finding.getName());
            }

            // if the finding is a rare finding, it is not removed from the position and added to the player's score
            if (finding.isRare()) {
                playerFindings.add(finding);
                addScore(finding.getPoints());
                position.removeFinding();
                System.out.println("Player " + getName() + " successfully collected the rare finding: " + finding.getName());
            }

            // if the finding is a statue, it is not removed from the position
            if (finding.getType().equalsIgnoreCase("statue")) {
                playerFindings.add(finding);
                position.removeFinding();
                System.out.println("Player " + getName() + " successfully collected the statue: " + finding.getName());
            }
        } else {
            System.out.println("Player " + getName() + " failed to collect the finding.");
        }
    }
}
