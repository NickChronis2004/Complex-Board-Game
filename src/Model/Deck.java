package Model;

import java.util.*;

/**
 * Represents the deck of cards in the game, implemented as a stack.
 */
public class Deck {
    private Stack<Card> cards; // The stack of cards

    /**
     * Constructs a Deck object and initializes it as an empty stack.
     */
    public Deck() {
        this.cards = new Stack<>();
    }

    /**
     * Adds a card to the top of the deck.
     * @param card the card to add.
     */
    public void addCard(Card card) {
        cards.push(card);
    }

    /**
     * Returns the top card of the deck without removing it.
     * @return the top card, or null if the deck is empty.
     */
    public Card topCard() {
        return cards.isEmpty() ? null : cards.peek();
    }

    /**
     * Removes and returns the top card of the deck.
     */
    public void popCard() {
        if (!cards.isEmpty()) {
            cards.pop();
        }
    }

    /**
     * Checks if the deck has any remaining cards.
     * @return true if the deck is not empty, false otherwise.
     */
    public boolean hasCards() {
        System.out.println("Deck has " + cards.size() + " cards remaining.");
        return !cards.isEmpty();
    }

    /**
     * Returns a list of all remaining cards in the deck.
     * @return a list of remaining cards.
     */
    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    /**
     * Shuffles the deck.
     */
    public void shuffle() {
        List<Card> cardList = new ArrayList<>(cards);
        Random random = new Random(); // Χρησιμοποιούμε έναν Random Generator
        for (int i = cardList.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1); // Επιλογή τυχαίου δείκτη
            // Swap
            Card temp = cardList.get(i);
            cardList.set(i, cardList.get(j));
            cardList.set(j, temp);
        }
        cards.clear();
        cards.addAll(cardList);
    }


    /**
     * Returns the number of cards currently in the deck.
     * @return the size of the deck.
     */
    public int size() {
        return cards.size();
    }

    /**
     * Clears all cards from the deck.
     */
    public void clear() {
        cards.clear();
    }

    @Override
    public String toString() {
        return "Deck{" +
                "cards=" + cards +
                '}';
    }
}
