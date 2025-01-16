package src.View;

import src.Controller.GameController;
import src.Model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The GameView class is responsible for displaying the game board, player cards, and paths.
 */
public class GameView extends JFrame {
    private JPanel player1Panel;
    private JPanel player2Panel;
    private JPanel pathsPanel;
    private JLabel currentPlayerLabel;

    private Map<String, JLabel> pathLabelMap;
    private GameController controller;

    /**
     * Constructs a GameView object.
     */
    public GameView() {
        setTitle("Minoan Board Game");
        setSize(1600, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(null);

        pathLabelMap = new HashMap<>();
        // load and set the background image
        try {
            ImageIcon backgroundIcon = loadAndResizeImage("background.jpg", 1600, 1000);
            JLabel backgroundLabel = new JLabel(backgroundIcon);
            backgroundLabel.setBounds(0, 0, 1600, 1000);
            backgroundLabel.setLayout(null);
            setContentPane(backgroundLabel);
            System.out.println("Background image loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error loading background image.");
            e.printStackTrace();
            // create a placeholder image in case of failure
            JLabel backgroundLabel = new JLabel(createPlaceholderImage(1600, 1000));
            backgroundLabel.setBounds(0, 0, 1600, 1000);
            backgroundLabel.setLayout(null);
            setContentPane(backgroundLabel);
        }

        // Label for the current player
        currentPlayerLabel = new JLabel("Current Player: Player 1");
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        currentPlayerLabel.setBounds(20, 20, 300, 30);
        getContentPane().add(currentPlayerLabel);

        // Panel for the paths
        pathsPanel = new JPanel(null);
        pathsPanel.setOpaque(false);
        pathsPanel.setBounds(100, 200, 1400, 600);
        getContentPane().add(pathsPanel);

        // Προσθήκη μονοπατιών (π.χ., "knossos", "phaistos", "malia", "zakros")
        String[] paths = {"knossos", "phaistos", "malia", "zakros"};
        for (int i = 0; i < paths.length; i++) {
            addPath(paths[i], i);
        }

        // Panels for the player cards
        player1Panel = createCardPanel(100, 50, "Player 1 Cards");
        player2Panel = createCardPanel(100, 800, "Player 2 Cards");
        getContentPane().add(player1Panel);
        getContentPane().add(player2Panel);

        setVisible(true);
    }

    /**
     * A helper method for loading and resizing an image.
     *
     * @param filename the name of the image file.
     * @param width the new width.
     * @param height the new height.
     * @return an ImageIcon with the desired size.
     * @throws IOException if the image cannot be found or loaded.
     */
    private ImageIcon loadAndResizeImage(String filename, int width, int height) throws IOException {
        BufferedImage originalImage = loadImage(filename);
        BufferedImage resizedImage = resizeImage(originalImage, width, height);
        return new ImageIcon(resizedImage);
    }


    /**
     * Loads an image from resources using the ClassLoader.
     *
     * @param filename the name of the image file.
     * @return a BufferedImage of the image.
     * @throws IOException if the image cannot be found or loaded.
     */
    private BufferedImage loadImage(String filename) throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/images/" + filename)) {
            if (is == null) {
                throw new IOException("Resource not found: /images/" + filename);
            }
            BufferedImage img = ImageIO.read(is);
            System.out.println("Image loaded from: /images/" + filename);
            return img;
        } catch (Exception e) {
            throw new IOException("Resource not found: /images/" + filename, e);
        }
    }

    /**
     * A helper method for resizing a BufferedImage using Graphics2D for better performance.
     *
     * @param originalImage the original image.
     * @param targetWidth the new width.
     * @param targetHeight the new height.
     * @return the resized image.
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        // settings for better performance
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return resizedImage;
    }

    /**
     * Creates a panel for the player cards.
     *
     * @param x the x-coordinate of the panel.
     * @param y the y-coordinate of the panel.
     * @param title the title of the panel.
     * @return the created JPanel.
     */
    private JPanel createCardPanel(int x, int y, String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBounds(x, y, 1400, 120);
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        // create 8 buttons for the player cards with resized images
        for (int i = 0; i < 8; i++) {
            try {
                ImageIcon cardIcon = loadAndResizeImage("cards/backcard.jpg", 80, 120);
                JButton cardButton = new JButton(cardIcon);
                cardButton.setPreferredSize(new Dimension(80, 120));
                panel.add(cardButton);
            } catch (IOException e) {
                System.err.println("Error loading card image: cards/backcard.jpg");
                e.printStackTrace();
                // Δημιουργία placeholder κουμπιού αν χρειάζεται
                JButton placeholderButton = new JButton(createPlaceholderImage(80, 120));
                placeholderButton.setPreferredSize(new Dimension(80, 120));
                panel.add(placeholderButton);
            }
        }
        return panel;
    }

    /**
     * Adds paths with resized images.
     *
     * @param pathName the name of the path.
     * @param index the index of the path.
     */
    private void addPath(String pathName, int index) {
        int x = 50;
        int y = index * 150;

        JPanel pathPanel = new JPanel(null);
        pathPanel.setOpaque(false);
        pathPanel.setBounds(x, y, 1400, 150);

        for (int i = 1; i <= 9; i++) {
            String imageName;
            if (i == 9) {
                imageName = pathName.toLowerCase() + "Palace.jpg";
            } else {
                imageName = (i % 2 == 0) ? pathName.toLowerCase() + "2.jpg" : pathName.toLowerCase() + ".jpg";
            }
            // change the size of the image
            ImageIcon icon;
            try {
                icon = loadAndResizeImage("paths/" + imageName, 140, 150);
            } catch (IOException e) {
                System.err.println("Failed to load image: paths/" + imageName);
                e.printStackTrace();
                icon = new ImageIcon(new BufferedImage(140, 150, BufferedImage.TYPE_INT_ARGB));
            }

            JLabel positionLabel = new JLabel(icon);
            positionLabel.setBounds((i - 1) * 140, 0, 140, 150);
            pathPanel.add(positionLabel);

            String key = pathName.toLowerCase() + "-" + i;

            if (pathLabelMap.containsKey(key)) {
                System.out.println("Duplicate key detected: " + key);
            }
            pathLabelMap.put(key, positionLabel);
            System.out.println("Added JLabel for key: " + key); // debug print
        }

        pathsPanel.add(pathPanel);
    }

    /**
     * Sets the controller and sets listeners for the card buttons.
     *
     * @param controller the controller of the game.
     * @param players the list of players.
     */
    public void setController(GameController controller, List<Player> players) {
        this.controller = controller;

        // Player 1
        for (int i = 0; i < player1Panel.getComponentCount(); i++) {
            if (player1Panel.getComponent(i) instanceof JButton button) {
                int cardIndex = i;
                // clear existing listeners
                for (ActionListener listener : button.getActionListeners()) {
                    button.removeActionListener(listener);
                    System.out.println("Removed existing listener from Player 1 card " + cardIndex);
                }
                // add new listener
                button.addActionListener(e -> {
                    System.out.println("Player 1 card " + cardIndex + " clicked.");
                    controller.handleCardClick(players, 1, cardIndex);
                });
            }
        }

        // Player 2
        for (int i = 0; i < player2Panel.getComponentCount(); i++) {
            if (player2Panel.getComponent(i) instanceof JButton button) {
                int cardIndex = i;
                // clear existing listeners
                for (ActionListener listener : button.getActionListeners()) {
                    button.removeActionListener(listener);
                    System.out.println("Removed existing listener from Player 2 card " + cardIndex);
                }
                // add new listeners
                button.addActionListener(e -> {
                    System.out.println("Player 2 card " + cardIndex + " clicked.");
                    controller.handleCardClick(players, 2, cardIndex);
                });
            }
        }
    }

    /**
     * Updates the game board with the current state.
     *
     * @param board the current state of the board.
     */
    public void updateBoard(Board board) {
        for (Path path : board.getPaths()) {
            for (Position position : path.getPositions()) {
                String key = path.getPathName().toLowerCase() + "-" + position.getIndex();
                JLabel positionLabel = pathLabelMap.get(key);
                if (positionLabel == null) {
                    continue;
                }

                // load the base image
                String imageName;
                if (position.getIndex() == 9) {
                    imageName = path.getPathName().toLowerCase() + "Palace.jpg";
                } else {
                    imageName = (position.getIndex() % 2 == 0) ? path.getPathName().toLowerCase() + "2.jpg" : path.getPathName().toLowerCase() + ".jpg";
                }

                BufferedImage baseImage;
                try {
                    baseImage = loadImage("paths/" + imageName);
                    baseImage = resizeImage(baseImage, 140, 150);
                } catch (IOException e) {
                    System.err.println("Failed to load base path image: paths/" + imageName);
                    e.printStackTrace();
                    baseImage = new BufferedImage(140, 150, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = baseImage.createGraphics();
                    g2d.setColor(Color.GRAY);
                    g2d.fillRect(0, 0, 140, 150);
                    g2d.dispose();
                }

                Graphics2D g2d = baseImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                // add pawns if they exist
                List<Pawn> pawnsAtPos = position.getPawns();
                if (!pawnsAtPos.isEmpty()) {
                    ImageIcon combinedIcon = createCombinedPawnIcon(pawnsAtPos);
                    g2d.drawImage(((ImageIcon) combinedIcon).getImage(), 0, 0, null);
                }
                // add the finding if it exists
                else if (position.isFindingPosition() && position.getFinding() != null) {
                    String findingImagePath = "findings/" + position.getFinding().getName() + ".jpg";
                    try {
                        ImageIcon findingIcon = loadAndResizeImage(findingImagePath, 140, 150);
                        g2d.drawImage(findingIcon.getImage(), 0, 0, null);
                    } catch (IOException e) {
                        System.err.println("Error loading finding image: " + findingImagePath);
                        e.printStackTrace();
                        // add placeholder image
                        g2d.setColor(Color.LIGHT_GRAY);
                        g2d.fillRect(0, 0, 140, 150);
                        g2d.setColor(Color.BLACK);
                        g2d.drawString("Finding Not Found", 20, 75);
                    }
                }

                g2d.dispose();

                positionLabel.setIcon(new ImageIcon(baseImage));
            }
        }
    }


    /**
     * Updates the current player label.
     *
     * @param playerName the name of the current player.
     */
    public void updateCurrentPlayer(String playerName) {
        currentPlayerLabel.setText("Current Player: " + playerName);
    }

    /**
     * Displays the "Game Over" message.
     */
    public void displayGameOverMessage() {
        JOptionPane.showMessageDialog(
                this,
                "Game Over!",
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE
        );
        dispose();
    }

    /**
     * Updates the panel that displays the player cards.
     *
     * @param player the player.
     * @param updatedCards the updated list of cards.
     */
    public void updatePlayerHandPanel(Player player, List<Card> updatedCards) {
        SwingUtilities.invokeLater(() -> {
            // clear focus owner before changing
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();

            JPanel playerPanel = (player.getId() == 1) ? player1Panel : player2Panel;

            playerPanel.removeAll();

            for (int i = 0; i < updatedCards.size(); i++) {
                Card card = updatedCards.get(i);
                JButton cardButton = new JButton();
                cardButton.setPreferredSize(new Dimension(80, 120));

                // use getImageIcon() if the player is active
                ImageIcon icon;
                if (player.equals(controller.getActivePlayer())) {
                    icon = card.getImageIcon();
                } else {
                    try {
                        icon = loadAndResizeImage("cards/backcard.jpg", 80, 120);
                    } catch (IOException e) {
                        System.err.println("Error loading card image: cards/backcard.jpg");
                        e.printStackTrace();
                        icon = createPlaceholderImage(80, 120);
                    }
                }
                cardButton.setIcon(icon);

                // tooltip only for the active player
                cardButton.setToolTipText(player.equals(controller.getActivePlayer()) ? card.getPalaceName() : null);

                // the buttons should be enabled only for the active player
                cardButton.setEnabled(player.equals(controller.getActivePlayer()));

                // add listener for the card click
                final int cardIndex = i;
                cardButton.addActionListener(e -> controller.handleCardClick(controller.getPlayers(), player.getId(), cardIndex));

                playerPanel.add(cardButton);
            }

            playerPanel.revalidate();
            playerPanel.repaint();
        });
    }

    /**
     * Displays the cards and the two players.
     *
     * @param players the list of players.
     * @param player1Cards the list of cards for player 1.
     * @param player2Cards the list of cards for player 2.
     */
    public void displayPlayerCards(List<Player> players, List<Card> player1Cards, List<Card> player2Cards) {
        updatePlayerHandPanel(players.get(0), player1Cards);
        updatePlayerHandPanel(players.get(1), player2Cards);
    }

    /**
     * Allows interaction only with the active player.
     *
     * @param players the list of players.
     * @param playerId the ID of the active player.
     * @param controller the GameController object.
     */
    public void enablePlayerInteraction(List<Player> players, int playerId, GameController controller) {
        JPanel activePanel = (playerId == 1) ? player1Panel : player2Panel;
        JPanel opponentPanel = (playerId == 1) ? player2Panel : player1Panel;

        // enable cards for the active player
        for (int i = 0; i < activePanel.getComponentCount(); i++) {
            if (activePanel.getComponent(i) instanceof JButton button) {
                button.setEnabled(true);
            }
        }
        // disable cards for the opponent
        for (int i = 0; i < opponentPanel.getComponentCount(); i++) {
            if (opponentPanel.getComponent(i) instanceof JButton button) {
                button.setEnabled(false);
            }
        }
    }

    /**
     * Disables interaction for all players.
     */
    public void disablePlayerInteraction() {
        for (Component c : player1Panel.getComponents()) {
            if (c instanceof JButton button) {
                button.setEnabled(false);
            }
        }
        for (Component c : player2Panel.getComponents()) {
            if (c instanceof JButton button) {
                button.setEnabled(false);
            }
        }
    }

    /**
     * Creates a placeholder image in case of loading failure.
     *
     * @param width the width of the image.
     * @param height the height of the image.
     * @return an ImageIcon for the placeholder image.
     */
    private ImageIcon createPlaceholderImage(int width, int height) {
        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = placeholder.createGraphics();
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Image Not Found", width / 2 - 50, height / 2);
        g2d.dispose();
        return new ImageIcon(placeholder);
    }

    /**
     * Combines the images of multiple pawns into one (if there are at most 2 pawns, they will be side-by-side).
     *
     * @param pawns the list of pawns on a path.
     * @return an ImageIcon that combines all the pawns.
     */
    private ImageIcon createCombinedPawnIcon(List<Pawn> pawns) {
        List<BufferedImage> images = new ArrayList<>();
        for (Pawn p : pawns) {
            // use the current image icon, taking into account if it is revealed
            ImageIcon pawnIcon = p.getCurrentImageIcon();
            if (pawnIcon != null && pawnIcon.getImage() != null) {
                BufferedImage pawnImage = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
                Graphics g = pawnImage.getGraphics();
                g.drawImage(pawnIcon.getImage(), 0, 0, null);
                g.dispose();
                images.add(pawnImage);
            }
        }

        // if there are no images, return the placeholder image
        if (images.isEmpty()) {
            try {
                return loadAndResizeImage("pionia/question.jpg", 40, 40);
            } catch (IOException e) {
                System.err.println("Error loading default pawn image.");
                e.printStackTrace();
                // Placeholder
                BufferedImage placeholder = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = placeholder.createGraphics();
                g2d.setColor(new Color(0, 0, 0, 0));
                g2d.fillRect(0, 0, 40, 40);
                g2d.dispose();
                return new ImageIcon(placeholder);
            }
        }

        // if there is only one image, return it
        if (images.size() == 1) {
            return new ImageIcon(images.get(0));
        }

        // combine multiple images
        int totalWidth = 40 * images.size();
        int totalHeight = 40;

        BufferedImage combined = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = combined.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        int xOffset = 0;
        for (BufferedImage img : images) {
            g2d.drawImage(img, xOffset, 0, null);
            xOffset += 40;
        }
        g2d.dispose();

        return new ImageIcon(combined);
    }


    /**
     * The main method for running the GameView.
     *
     * @param args the command-line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameView::new);
    }
}