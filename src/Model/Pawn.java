package Model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Represents a Pawn in the game.
 */
public abstract class Pawn {
    private final String type;
    private final Player player;
    private Position position;
    private Path path;
    private boolean checkpointReached;
    private boolean isRevealed;
    private ImageIcon imageIcon;
    private static final String IMAGE_PATH = "project_assets/images/pionia/";

    /**
     * Constructs a Pawn with a specified type, player, initial position, path, and isRevealed flag.
     *
     * @param type the type of the pawn (e.g., "Archaeologist", "Theseus").
     * @param player the player who owns the pawn.
     * @param initialPosition the initial position of the pawn.
     * @param path the path associated with the pawn.
     * @param isRevealed the initial revealed status of the pawn.
     */
    public Pawn(String type, Player player, Position initialPosition, Path path, boolean isRevealed) {
        this.type = type;
        this.player = player;
        this.path = path;
        this.position = null;
        this.checkpointReached = false;
        this.isRevealed = isRevealed;
        this.imageIcon = loadImageIcon();

        if (path == null) {
            System.out.println("Error: Path is null for " + type + " of player " + player.getName());
        } else {
            System.out.println(type + " of player " + player.getName() + " initialized on path " + path.getPathName());
        }

        if (initialPosition != null) {
            setCurrentPosition(initialPosition);
        }
    }

    /**
     * Επιστρέφει το είδος του πιόνιου (π.χ. "Archaeologist", "Theseus").
     */
    public String getType() {
        return type;
    }

    /**
     * Επιστρέφει τον παίκτη που κατέχει το πιόνι.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Θέτει το πιόνι να βρίσκεται σε μια νέα Position στο ταμπλό.
     * <p>
     * 1. Αφαιρεί το πιόνι από την παλιά θέση (αν υπάρχει),
     * 2. Το προσθέτει στη νέα θέση,
     * 3. Ενημερώνει το πεδίο {@code position}.
     *
     * @param newPosition η νέα θέση όπου θα τοποθετηθεί το πιόνι.
     */
    public void setCurrentPosition(Position newPosition) {
        // remove the pawn from the previous position
        if (this.position != null) {
            this.position.removePawn(this);
        }

        // update the position
        this.position = newPosition;

        // update the path with the new position
        if (newPosition != null) {
            this.path = newPosition.getPath();
            boolean success = newPosition.addPawn(this);
            if (success) {
                System.out.println(this.type + " of " + this.player.getName() + " successfully placed at position " + newPosition.getIndex() + " on path " + newPosition.getPath().getPathName());
            } else {
                System.out.println("Could not place pawn " + this.type + " at position " + newPosition.getIndex() + " on path " + newPosition.getPath().getPathName());
                this.position = null;
                this.path = null;
            }
        }
    }

    /**
     * Returns the current position of the pawn.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Returns the path associated with the pawn.
     */
    public Path getPath() {
        return path;
    }

    /**
     * Returns true if the pawn has reached the checkpoint, false otherwise.
     */
    public boolean isCheckpointReached() {
        return this.checkpointReached;
    }

    /**
     * Returns true if the pawn is revealed, false otherwise.
     */
    public boolean isRevealed(){
        return isRevealed;
    }

    /**
     * Returns true if the pawn is hidden, false if revealed.
     * @return the hidden state of the pawn.
     */
    public boolean isHidden() {
        return !isRevealed();
    }

    /**
     * Reveals the pawn.
     */
    public void revealPawn() {
        if (!isRevealed) {
            isRevealed = true;
            updateImageIcon();
            System.out.println(this.type + " of " + this.player.getName() + " has been revealed.");
        }
    }

    /**
     * Updates the imageIcon based on the pawn type.
     */
    private void updateImageIcon() {
        this.imageIcon = new ImageIcon(IMAGE_PATH + getImageName());
    }

    /**
     * Returns the name of the image file based on the pawn type.
     *
     * @return the name of the image file.
     */
    private String getImageName() {
        if (this instanceof Archaeologist) {
            return "archaeologist.jpg";
        } else if (this instanceof Theseus) {
            return "theseus.jpg";
        } else {
            return "question.jpg";
        }
    }

    /**
     * Loads the ImageIcon based on the pawn type and revealed status.
     * @return the ImageIcon of the pawn.
     */
    private ImageIcon loadImageIcon() {
        String imagePath = isRevealed ? getImageName() : "question.jpg";
        String fullPath = IMAGE_PATH + imagePath;

        try {
            BufferedImage img = ImageIO.read(new File(fullPath));
            Image scaledImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (IOException e) {
            System.out.println("Error loading image for " + type + ": " + fullPath);
            // return a placeholder image if the image fails to load
            BufferedImage placeholder = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = placeholder.createGraphics();
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, 50, 50);
            g2d.setColor(Color.BLACK);
            g2d.drawString("?", 20, 25);
            g2d.dispose();
            return new ImageIcon(placeholder);
        }
    }


    /**
     * Returns the ImageIcon for the pawn, taking into account the revealed status.
     * @return the ImageIcon of the pawn.
     */
    public ImageIcon getCurrentImageIcon() {
        String imageName = isRevealed ? getImageName() : "question.jpg";
        String fullPath = IMAGE_PATH + imageName;

        try {
            BufferedImage img = ImageIO.read(new File(fullPath));
            Image scaledImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (IOException e) {
            System.err.println("Error loading image for pawn: " + fullPath);
            // create a placeholder image in case of loading failure
            BufferedImage placeholder = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = placeholder.createGraphics();
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, 40, 40);
            g2d.setColor(Color.BLACK);
            g2d.drawString("?", 15, 25);
            g2d.dispose();
            return new ImageIcon(placeholder);
        }
    }



    /**
     * Moves the pawn by the specified number of steps.
     *
     * @param steps the number of steps to move (positive or negative).
     * @return true if the movement was successful, false otherwise.
     */
    public boolean move(int steps) {
        if (position == null) {
            System.out.println("Pawn is not placed on a valid position.");
            return false;
        }

        System.out.println("Attempting to move " + this.type + " of " + player.getName() + " from position " +
                position.getIndex() + " by " + steps + " steps.");

        int currentIndex = position.getIndex();
        int newIndex = currentIndex + steps;

        // check if the movement is valid
        if (newIndex < 1) {
            System.out.println("Pawn cannot move before the first position. Forced to position 1.");
            newIndex = 1;
        } else if (newIndex > path.getPositions().size()) {
            System.out.println("Pawn cannot move beyond the end of the path.");
            return false;
        }

        Position oldPos = position;
        oldPos.removePawn(this);
        System.out.println(this.type + " removed from position " + oldPos.getIndex());

        Position newPos = path.getPositionByIndex(newIndex);
        if (newPos == null) {
            System.out.println("Invalid new position: index " + newIndex);
            oldPos.addPawn(this);
            System.out.println(this.type + " reverted to position " + oldPos.getIndex());
            return false;
        }

        boolean success = newPos.addPawn(this);
        if (!success) {
            System.out.println("Could not place pawn at new position " + newIndex + ". Move failed.");
            oldPos.addPawn(this);
            System.out.println(this.type + " reverted to position " + oldPos.getIndex());
            return false;
        }

        this.position = newPos;
        System.out.println(this.type + " moved to position " + newPos.getIndex());
        // check if we have reached the checkpoint
        if (newIndex == 7) {
            this.checkpointReached = true;
            System.out.println("Pawn " + this.type + " has reached the checkpoint at index " + newIndex);
        } else {
            this.checkpointReached = false;
        }

        return true;
    }
}
