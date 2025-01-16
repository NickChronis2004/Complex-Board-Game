package src.Model;

import java.util.*;

/**
 * Represents the game board, managing paths, findings, and the deck.
 */
public class Board {
    private List<Card> deck; // the main deck of 100 cards
    private List<Path> paths; // the 4 paths of the palaces
    private List<Finding> findings; // all non-rare findings
    private List<RareFinding> rareFindings; // rare findings

    /**
     * Constructs a Board object and initializes the main components.
     */
    public Board(List<Path> paths) {
        this.deck = new ArrayList<>();
        this.findings = new ArrayList<>();
        this.rareFindings = new ArrayList<>();
        this.paths = paths;
    }

    /**
     * Returns the list of paths on the board.
     * @return A list of Path objects.
     */
    public List<Path> getPaths() {
        return paths;
    }


    /**
     * Initializes the findings on the paths.
     *
     * @param findings the list of findings to initialize.
     * @param rareFindings the list of rare findings to initialize.
     * @param paths the list of paths to initialize.
     *
     */
    public void initializeFindings(List<Finding> findings, List<RareFinding> rareFindings, List<Path> paths) {
        // Place rare findings on their specific paths
        for (RareFinding rareFinding : rareFindings) {
            Path path = paths.stream()
                    .filter(p -> p.getPathName().equals(rareFinding.getPathName()))
                    .findFirst()
                    .orElse(null);

            if (path != null) {
                Position position = path.getPositionByIndex(2);
                if (position != null && position.isFindingPosition()) {
                    position.setFinding(rareFinding);
                    System.out.println("Placed " + rareFinding.getName() + " on path " + path.getPathName());
                } else {
                    System.out.println("Error: Invalid position for rare finding on path " + path.getPathName());
                }
            } else {
                System.out.println("Error: Path not found for rare finding " + rareFinding.getName());
            }
        }

        // Place non-rare findings randomly on remaining positions
        Random random = new Random();
        for (Finding finding : findings) {
            boolean placed = false;
            while (!placed) {
                Path randomPath = paths.get(random.nextInt(paths.size())); // random path
                Position randomPosition = randomPath.getPositions().stream()
                        .filter(pos -> pos.isFindingPosition() && pos.getFinding() == null)
                        .findFirst()
                        .orElse(null);

                if (randomPosition != null) {
                    randomPosition.setFinding(finding);
                    placed = true;
                    System.out.println("Placed " + finding.getName() + " on path " + randomPath.getPathName());
                }
            }
        }

        // Shuffle findings on each path
        for (Path path : paths) {
            List<Finding> findingsOnPath = new ArrayList<>();
            for (Position position : path.getPositions()) {
                if (position.getFinding() != null) {
                    findingsOnPath.add(position.getFinding());
                }
            }

            Collections.shuffle(findingsOnPath); // Shuffle the findings

            // Deal the findings to the finding positions
            int index = 0;
            for (Position position : path.getPositions()) {
                if (position.isFindingPosition()) {
                    if (index < findingsOnPath.size()) {
                        position.setFinding(findingsOnPath.get(index));
                        index++;
                    } else {
                        position.setFinding(null);
                    }
                }
            }
            System.out.println("Shuffled findings on path: " + path.getPathName());
        }
    }


    /**
     * Deals cards from the deck to two players.
     * @param deck the deck of cards.
     * @param player1 the first player.
     * @param player2 the second player.
     */
    public void dealCards(Deck deck, Player player1, Player player2) {
        for (int i = 0; i < 8; i++) {
            if (deck.hasCards()) {
                player1.drawCard(deck);
            } else {
                System.out.println("Deck is empty. Cannot deal more cards.");
                break;
            }
            if (deck.hasCards()) {
                player2.drawCard(deck);
            } else {
                System.out.println("Deck is empty. Cannot deal more cards.");
                break;
            }
        }
    }


    /**
     * Initializes the entire board by shuffling the deck, placing findings, and dealing cards to players.
     * @param deck the deck of cards.
     * @param player1 the first player.
     * @param player2 the second player.
     */
    public void initializeBoard(Deck deck, Player player1, Player player2) {
        if (paths.isEmpty()) {
            System.out.println("Error: No paths available to initialize the board.");
            return;
        }

        if (deck.getCards().isEmpty()) {
            System.out.println("Error: Deck is empty. Cannot initialize board.");
            return;
        }

        // initialize findings on the paths
        initializeFindings(findings, rareFindings, paths);
        System.out.println("Findings initialized!");

        // deal cards to the players
        dealCards(deck, player1, player2);
        System.out.println("Cards dealt to players!");
    }


    /**
     * @return a list of all pawns on the board.
     */
    public List<Pawn> getAllPawns() {
        List<Pawn> allPawns = new ArrayList<>();

        // checks all paths
        for (Path path : paths) {
            // checks all positions on the path
            for (Position position : path.getPositions()) {
                List<Pawn> pawnsAtPosition = position.getPawns();
                allPawns.addAll(pawnsAtPosition);
            }
        }

        return allPawns;
    }


    /**
     * Returns the path with the specified name.
     * @param pathName The name of the path to search for.
     * @return The Path object with the specified name, or null if not found.
     */
    public Path getPathByName(String pathName) {
        // checks all paths
        for (Path path : paths) {
            if (path.getPathName().equalsIgnoreCase(pathName)) {
                return path; // returns the path that matches
            }
        }
        return null;
    }
}