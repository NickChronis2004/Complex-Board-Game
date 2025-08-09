package Model;

import javax.swing.ImageIcon;

public abstract class Card {
    private final String palaceName;
    private final String imageName;

    public Card(String palaceName, String imageName) {
        this.palaceName = palaceName;
        this.imageName = imageName;
    }

    /**
     * Returns the type of the card.
     * @return the type of the card.
     */
    public String getPalaceName() {
        return palaceName;
    }

    /**
     * Returns the full path of the image file.
     * @return the full path of the image file.
     */
    public String getImagePath() {
        return "project_assets/images/cards/" + imageName;
    }

    /**
     * Returns the ImageIcon of the card.
     * @return the ImageIcon of the card.
     */
    public ImageIcon getImageIcon() {
        String path = getImagePath();
        java.io.File file = new java.io.File(path);
        if (file.exists()) {
            return new ImageIcon(path);
        }
        // fallback if the image is not found
        return new ImageIcon("project_assets/images/cards/backcard.jpg");
    }


    /**
     * Returns the path associated with the card based on the palace name.
     *
     * @return the path associated with the card, or null if not found.
     */
    public String toString() {
        return "Palace: " + palaceName + ", ImageName: " + imageName;
    }
}
