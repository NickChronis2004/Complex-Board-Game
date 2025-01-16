package src.Controller;

import src.Model.*;
import src.View.GameView;

import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller for the game.
 */
public class GameController {
    private final GameView view;
    public final List<Player> players;
    private Deck deck;
    private Board board;
    private List<Path> paths;
    private List<Pawn> pawns;
    private List<Finding> findings;
    private List<RareFinding> rareFindings;
    private int currentPlayerIndex;
    private boolean actionCompleted;
    public Player activePlayer; // για γρήγορη πρόσβαση στον ενεργό παίκτη
    private final MusicPlayer musicPlayer;

    /**
     * Constructs a GameController object.
     *
     * @param view     The GameView object.
     * @param players  The list of players.
     * @param deck     The deck of cards.
     * @param board    The board of paths.
     * @param paths    The list of paths.
     */
    public GameController(GameView view, List<Player> players, Deck deck, Board board, List<Path> paths) {
        this.view = view;
        this.players = players;
        this.board = board;
        this.paths = paths;
        this.musicPlayer = new MusicPlayer();

        // connect the controller with the view
        this.view.setController(this, players);

        // initialize actionCompleted
        this.actionCompleted = false;
        this.currentPlayerIndex = new Random().nextInt(players.size());
        this.activePlayer = players.get(currentPlayerIndex);

        // initialize pawns
        this.pawns = new ArrayList<>();
        for (Player player : players) {
            pawns.addAll(initializePawns(player));
        }

        // initialize findings
        this.findings = new ArrayList<>();
        this.rareFindings = new ArrayList<>();
    }

    public Player getActivePlayer(){
        return activePlayer;
    }

    public List<Player> getPlayers(){
        return players;
    }

    /**
     * Start the game.
     */
    public void startGame() {
        // place pawns on paths
        placePawnsOnPaths(pawns, paths, players.get(0));
        placePawnsOnPaths(pawns, paths, players.get(1));

        verifyPawnsPath(pawns);

        this.deck = new Deck();
        this.deck = initializeDeckData(this.deck);
        deck.shuffle();

        this.board = new Board(paths);

        // initialize findings
        this.findings = initializeCommonFindings();
        this.rareFindings = initializeRareFindings();


        // initialize board
        board.initializeBoard(deck, players.get(0), players.get(players.size() - 1));

        // start the game loop
        playGameLoop();
    }

    // ---------------------------------------------
    // Game loop method for looping the game
    // ---------------------------------------------

    /**
     * The main game loop that keeps looping until the game is over.
     */
    public void playGameLoop() {

        boolean gameOver = false;

        // select a random player to start
        currentPlayerIndex = new Random().nextInt(players.size());
        activePlayer = players.get(currentPlayerIndex);
        view.updateCurrentPlayer(activePlayer.getName());

        // main loop: keeps looping until gameOver = true
        while (!gameOver) {
            // update UI (board, cards)
            view.updateBoard(board);
            view.updatePlayerHandPanel(activePlayer, activePlayer.getCardsOnHand());

            // enable player interaction
            view.enablePlayerInteraction(players, currentPlayerIndex + 1, this);

            // wait until the player completes their action
            actionCompleted = false;
            while (!actionCompleted) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // disable player interaction
            view.disablePlayerInteraction();

            updatePlayerHand(activePlayer);

            gameOver = isGameOver();

            switchPlayerTurn();

        }

        view.displayGameOverMessage();
    }


    // ---------------------------------------------
    // Game initialization methods
    // ---------------------------------------------

    /**
     * Initializes pawns for each player.
     *
     * @param player the player to initialize pawns for.
     * @return a list of pawns for the player.
     */
    private List<Pawn> initializePawns(Player player) {
        List<Pawn> pawnsForPlayer = new ArrayList<>();
        pawnsForPlayer.add(new Archaeologist(player, null, null));
        pawnsForPlayer.add(new Archaeologist(player, null, null));
        pawnsForPlayer.add(new Archaeologist(player, null, null));
        pawnsForPlayer.add(new Theseus(player, null, null));
        return pawnsForPlayer;
    }

    /**
     * Places pawns on available paths.
     *
     * @param pawns the list of pawns to place.
     * @param paths the list of paths to place pawns on.
     * @param player the player to place pawns for.
     */
    private static void placePawnsOnPaths(
            List<Pawn> pawns,
            List<Path> paths,
            Player player
    ) {
        // We use the usedPathsByThisPlayer if we want to place multiple pawns on the same path.
        Set<String> usedPathsByThisPlayer = new HashSet<>();

        for (Pawn pawn : pawns) {
            if (!pawn.getPlayer().equals(player)) continue;

            List<Path> availablePaths = new ArrayList<>();
            for (Path path : paths) {

                if (!usedPathsByThisPlayer.contains(path.getPathName().toLowerCase())) {
                    availablePaths.add(path);
                }
            }

            if (availablePaths.isEmpty()) {
                System.out.println("No available paths remaining for player: " + player.getName());
                break;
            }
            // use lowercase for keys
            String[] pathOptions = availablePaths.stream()
                    .map(Path::getPathName)
                    .toArray(String[]::new);

            String selectedPathName = (String) JOptionPane.showInputDialog(
                    null,
                    "Select a path for " + pawn.getType() + " (" + player.getName() + ")",
                    "Path Selection",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    pathOptions,
                    pathOptions[0]
            );

            if (selectedPathName == null) {
                System.out.println(player.getName() + " cancelled path selection for " + pawn.getType());
                continue;
            }
            // find the selected path
            Path selectedPath = paths.stream()
                    .filter(path -> path.getPathName().equalsIgnoreCase(selectedPathName))
                    .findFirst()
                    .orElse(null);

            if (selectedPath != null) {
                Position startPosition = selectedPath.getPositionByIndex(1);
                if (startPosition != null) {
                    pawn.setCurrentPosition(startPosition);

                    // If the position was successful, add the path to the usedPathsByThisPlayer
                    if (pawn.getPosition() != null) {
                        usedPathsByThisPlayer.add(selectedPath.getPathName().toLowerCase());
                        System.out.println(pawn.getType() + " placed on path: " + selectedPathName);
                    } else {
                        System.out.println("Could NOT place " + pawn.getType() + " on path: " + selectedPathName);
                    }

                } else {
                    System.out.println("Error: No valid starting position on path: " + selectedPathName);
                }
            } else {
                System.out.println("Error: Selected path not found in available paths.");
            }
        }
    }

    /**
     * Verifies that pawns are on the correct paths.
     *
     * @param pawns the list of pawns to verify.
     */
    private static void verifyPawnsPath(List<Pawn> pawns) {
        for (Pawn pawn : pawns) {
            if (pawn.getPath() == null) {
                System.out.println("Verification Error: " + pawn.getType() + " of player " + pawn.getPlayer().getName() + " has a null path.");
            } else {
                System.out.println(pawn.getType() + " of player " + pawn.getPlayer().getName() + " is on path " + pawn.getPath().getPathName());
            }
        }
    }



    /**
     * Loads cards from a specified folder and adds them to the deck.
     *
     * @param deck the deck to add cards to.
     * @return the deck with the added cards.
     */
    public Deck initializeDeckData(Deck deck) {
        String folderPath = "project_assets/images/cards";
        File cardDirectory = new File(folderPath);

        if (!cardDirectory.exists() || !cardDirectory.isDirectory()) {
            System.err.println("Invalid directory: " + folderPath);
            return deck;
        }

        File[] cardFiles = cardDirectory.listFiles();
        if (cardFiles == null) {
            System.err.println("No files found in the directory: " + folderPath);
            return deck;
        }

        for (File cardFile : cardFiles) {
            if (!cardFile.isFile()) continue;

            String cardName = cardFile.getName().toLowerCase();

            // Παράκαμψη της backcard
            if (cardName.equals("backcard.jpg")) {
                continue;
            }

            try {
                String pathName = extractPalaceFromName(cardName);

                if (cardName.contains("ari")) {
                    // Κάρτα Αριάδνης
                    for (int i = 0; i < 3; i++) {
                        deck.addCard(new AriadneCard(pathName, cardFile.getName()));
                    }
                } else if (cardName.contains("min")) {
                    // Κάρτα Μινώταυρου
                    for (int i = 0; i < 2; i++) {
                        deck.addCard(new MinotaurCard(pathName, cardFile.getName()));
                    }
                } else {
                    // Αριθμητική Κάρτα
                    int value = extractValueFromName(cardName);
                    for (int i = 0; i < 2; i++) {
                        deck.addCard(new NumberCard(value, pathName, cardFile.getName() ,extractPathFromName(cardName)));
                    }
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Error processing card: " + cardName + " - " + e.getMessage());
            }
        }

        System.out.println("Deck initialized with " + deck.size() + " cards.");

        return deck;
    }

    /**
     * Extracts the path from the card name for Number Cards.
     *
     * @param cardName the name of the card (e.g., "knossos2.jpg").
     * @return the path associated with the card.
     * @throws IllegalArgumentException if the path is not found for the specified palaceName.
     */

    //Only for Number Cards
    private Path extractPathFromName(String cardName) {

        if (cardName.contains("knossos")) {
            return paths.get(0);
        }else if (cardName.contains("phaistos")) {
            return paths.get(1);
        }else if (cardName.contains("malia")) {
            return paths.get(2);
        }else if (cardName.contains("zakros")) {
            return paths.get(3);
        }
        return null;
    }

    /**
     * Extracts the palace name from the card name.
     *
     * @param cardName the name of the card (e.g., "knossos2.jpg").
     * @return the palace name associated with the card.
     * @throws IllegalArgumentException if the palace is not found for the specified cardName.
     */
    private String extractPalaceFromName(String cardName) {
        if (cardName.startsWith("knossos")) return "Knossos";
        if (cardName.startsWith("phaistos")) return "Phaistos";
        if (cardName.startsWith("malia")) return "Malia";
        if (cardName.startsWith("zakros")) return "Zakros";

        throw new IllegalArgumentException("Unknown palace in card name: " + cardName);
    }

    /**
     * Extracts the value from the card name.
     *
     * @param cardName the name of the card (e.g., "knossos2.jpg").
     * @return the value associated with the card.
     * @throws IllegalArgumentException if the value is not found for the specified cardName.
     */
    private int extractValueFromName(String cardName) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(cardName);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        throw new IllegalArgumentException("No numeric value found in card name: " + cardName);
    }

    /**
     * Initializes common findings.
     *
     * @return a list of common findings.
     */
    private static List<Finding> initializeCommonFindings() {
        List<Finding> findings = new ArrayList<>();
        String findingsPath = "project_assets/images/findings";
        File folder = new File(findingsPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".jpg")) {
                    String fileName = file.getName();

                    if (fileName.startsWith("fresco")) {
                        int points = Integer.parseInt(fileName.split("_")[1].replace(".jpg", ""));
                        FrescoFinding fresco = new FrescoFinding("Fresco", points);
                        findings.add(fresco);
                        System.out.println("Added Fresco Finding: " + fresco.getName() + " with points: " + points);
                    } else if (fileName.equals("snakes.jpg")) {
                        Finding snake = new Finding("Snake Goddess", false, 0, "statue");
                        findings.add(snake);
                        System.out.println("Added Finding: " + snake.getName());
                    }
                }
            }
        } else {
            System.err.println("No findings found in directory: " + findingsPath);
        }
        return findings;
    }

    /**
     * Initializes rare findings.
     *
     * @return a list of rare findings.
     */
    private static List<RareFinding> initializeRareFindings() {
        List<RareFinding> rareFindings = new ArrayList<>();
        String findingsPath = "project_assets/images/findings";
        File folder = new File(findingsPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".jpg")) {
                    String fileName = file.getName();
                    switch (fileName) {
                        case "diskos.jpg" -> {
                            RareFinding diskos = new RareFinding("Phaistos Disk", 35, "Phaistos");
                            rareFindings.add(diskos);
                            System.out.println("Added Rare Finding: " + diskos.getName());
                        }
                        case "ring.jpg" -> {
                            RareFinding ring = new RareFinding("Knossos Ring", 25, "Knossos");
                            rareFindings.add(ring);
                            System.out.println("Added Rare Finding: " + ring.getName());
                        }
                        case "kosmima.jpg" -> {
                            RareFinding kosmima = new RareFinding("Malia Jewel", 25, "Malia");
                            rareFindings.add(kosmima);
                            System.out.println("Added Rare Finding: " + kosmima.getName());
                        }
                        case "ruto.jpg" -> {
                            RareFinding ruto = new RareFinding("Zakros Rhyton", 25, "Zakros");
                            rareFindings.add(ruto);
                            System.out.println("Added Rare Finding: " + ruto.getName());
                        }
                    }
                }
            }
        } else {
            System.err.println("No findings found in directory: " + findingsPath);
        }
        return rareFindings;
    }

    // ---------------------------------------------
    // Card handling methods
    // ---------------------------------------------

    /**
     * Handles a card click by the player.
     *
     * @param players the list of players.
     * @param playerId the ID of the player who clicked the card.
     * @param cardIndex the index of the card clicked.
     */
    public void handleCardClick(List<Player> players, int playerId, int cardIndex) {

        Player currentPlayer = players.get(playerId - 1);

        if (cardIndex < 0 || cardIndex >= currentPlayer.getCardsOnHand().size()) {
            JOptionPane.showMessageDialog(view, "Invalid card selection!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Card selectedCard = currentPlayer.getCardsOnHand().get(cardIndex);

        String[] options = {"Play Card", "Discard Card"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Do you want to play or discard this card?",
                "Card Action",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 1) { // Discard Card
            discardCard(currentPlayer, cardIndex);
            return;
        }

        // handle the card based on its type
        if (selectedCard instanceof MinotaurCard minotaurCard) {

            handleMinotaurCard(currentPlayer, minotaurCard);

            JOptionPane.showMessageDialog(
                    view,
                    "Minotaur card played on path "+ selectedCard.getPalaceName(),
                    "Played Minotaur Card",
                    JOptionPane.ERROR_MESSAGE
            );
        } else if (selectedCard instanceof AriadneCard ariadneCard) {

            handleAriadneCard(currentPlayer, ariadneCard);

            JOptionPane.showMessageDialog(
                    view,
                    "Ariadne card played on path "+ selectedCard.getPalaceName(),
                    "Played Ariadne Card",
                    JOptionPane.ERROR_MESSAGE
            );
        } else if (selectedCard instanceof NumberCard numberCard) {

            handleNumberCard(currentPlayer, numberCard);

            JOptionPane.showMessageDialog(
                    view,
                    "Number card with value " + numberCard.getValue() + " played on path "+ selectedCard.getPalaceName(),
                    "Played Number Card",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    view,
                    "Unknown card type!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // remove the card from the player's hand
        currentPlayer.getCardsOnHand().remove(cardIndex);

        //draw new card from deck
        currentPlayer.drawCard(deck);

        System.out.println("Player hand after removal: " + currentPlayer.getCardsOnHand());


        currentPlayer.PlayedAnyCard = true;

        updateView(players);

        setActionCompleted(true);
    }

    /**
     * Handles a card discard by the player.
     *
     * @param player the player who discarded the card.
     * @param cardIndex the index of the card to discard.
     */
    private void discardCard(Player player, int cardIndex) {

        Card discardedCard = player.getCardsOnHand().remove(cardIndex);

        //draw new card
        player.drawCard(deck);

        // update the view
        view.updatePlayerHandPanel(player, player.getCardsOnHand());

        System.out.println("Player " + player.getName() + " discarded: " + discardedCard);

        setActionCompleted(true);
    }


    /**
     * Handles a minotaur card by the player.
     *
     * @param player the player who played the card.
     * @param card the minotaur card to handle.
     */
    private void handleMinotaurCard(Player player, MinotaurCard card) {
        String pathName = card.getPalaceName();
        Path selectedPath = board.getPathByName(pathName);

        if (selectedPath == null) {
            JOptionPane.showMessageDialog(view, "Invalid path for this card: " + pathName, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Pawn targetPawn = selectedPath.getOpponentPawn(player);
        if (targetPawn == null) {
            JOptionPane.showMessageDialog(view, "No opponent pawn found on path: " + pathName, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        card.attack(targetPawn, player);
        JOptionPane.showMessageDialog(view, "Attack successful on " + targetPawn.getType() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }


    /**
     * Handles an ariadne card by the player.
     *
     * @param player the player who played the card.
     * @param card the ariadne card to handle.
     */
    public void handleAriadneCard(Player player, AriadneCard card) {
        String pathName = card.getPalaceName();
        Path selectedPath = board.getPathByName(pathName);

        if (selectedPath == null) {
            JOptionPane.showMessageDialog(view, "Invalid path for this card!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Pawn playerPawn = selectedPath.getPlayerPawn(player);
        if (playerPawn == null) {
            JOptionPane.showMessageDialog(view, "No pawn found on this path for you!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = playerPawn.move(2);
        if (success) {
            JOptionPane.showMessageDialog(view, "Pawn moved two steps on path: " + selectedPath.getPathName());
        } else {
            JOptionPane.showMessageDialog(view, "Move failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        System.out.println("Ariadne card used on path: " + selectedPath.getPathName());
    }


    /**
     * Handles a number card by the player.
     *
     * @param player the player who played the card.
     * @param card the number card to handle.
     */
    private void handleNumberCard(Player player, NumberCard card) {
        String pathName = card.getPalaceName();
        Path selectedPath = board.getPathByName(pathName);

        if (selectedPath == null) {
            JOptionPane.showMessageDialog(view, "Invalid path for this card: " + pathName, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Pawn playerPawn = selectedPath.getPlayerPawn(player);
        if (playerPawn == null) {
            JOptionPane.showMessageDialog(view, "No valid pawn to move on path: " + pathName, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = card.isPlayable(card ,card.getPath());
        if (success) {
            JOptionPane.showMessageDialog(view, "Pawn moved one step on path: " + selectedPath.getPathName());
            playerPawn.move(1);
        } else {
            JOptionPane.showMessageDialog(view, "Move failed (probably because the card is not playable)! + " +
                    "Last played number card value: "+ card.getPath().getLastCardPlayedValue(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * After the card is played, we move to a new card (if available) and update the UI.
     *
     * @param player the player who played the card.
     */
    private void updatePlayerHand(Player player) {
        if (deck.hasCards()) {
            Card newCard = deck.topCard();
            player.hand.add(newCard);

            System.out.println("Adding new card to hand: " + newCard);
            System.out.println("Player hand after addition: " + player.getCardsOnHand());
        }
        view.updatePlayerHandPanel(player, player.getCardsOnHand());
    }

    /**
     * Updates the view when data changes (after a card is played).
     *
     * @param players the list of players.
     * */
    private void updateView(List<Player> players) {
        Player current = players.get(currentPlayerIndex);
        view.displayPlayerCards(players, players.get(0).getCardsOnHand(), players.get(1).getCardsOnHand());
        view.updateBoard(board);
        view.updateCurrentPlayer(current.getName());
    }

    // ---------------------------------------------
    // Player turn handling methods
    // ---------------------------------------------
    public void switchPlayerTurn() {
        if (players.isEmpty()) {
            throw new IllegalStateException("No players available!");
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        activePlayer = players.get(currentPlayerIndex);
        activePlayer.setPlayedAnyCard(false);
        System.out.println("Switching to player: " + activePlayer.getName());
        view.updateCurrentPlayer(activePlayer.getName());

        String musicPath = currentPlayerIndex == 0
                ? "project_assets/music/Player1.wav"
                : "project_assets/music/Player2.wav";
        musicPlayer.playMusic(musicPath);

        view.enablePlayerInteraction(players, activePlayer.getId(), this);
    }


    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        List<Pawn> allPawns = board.getAllPawns();
        boolean deckEmpty = !deck.hasCards();
        boolean checkpoint = checkCheckpointCondition(allPawns);
        System.out.println("Checking Game Over: deckEmpty=" + deckEmpty + ", checkpoint=" + checkpoint);
        return (deckEmpty || checkpoint);
    }


    /**
     * Checks if the checkpoint condition is met.
     *
     * @param allPawns the list of pawns on the board.
     * @return true if the checkpoint condition is met, false otherwise.
     */
    private boolean checkCheckpointCondition(List<Pawn> allPawns) {
        boolean condition = false;
        for (Pawn pawn : allPawns) {
            if (pawn.getPosition().getIndex() == 7) {
                condition = true;
                break;
            }
        }
        System.out.println("Checkpoint condition met: " + condition);
        return condition;
    }


    /**
     * Returns the winner (or null if there is a tie).
     *
     * @return the winner player, or null if there is a tie.
     */
    public Player getWinner() {
        if (players.isEmpty()) {
            throw new IllegalStateException("No players to check for winner!");
        }

        Player p1 = players.get(0);
        Player p2 = players.get(1);

        if (p1.getScore() == p2.getScore()) {
            return null;
        } else if (p1.getScore() > p2.getScore()) {
            return p1;
        } else {
            return p2;
        }
    }

    /**
     * Sets the actionCompleted flag.
     *
     * @param actionCompleted the new value of the flag.
     */
    public void setActionCompleted(boolean actionCompleted) {
        this.actionCompleted = actionCompleted;
    }
}