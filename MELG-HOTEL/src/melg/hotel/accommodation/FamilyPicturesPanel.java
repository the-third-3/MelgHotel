package melg.hotel.accommodation;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Panel displaying family and hotel pictures in a grid layout
 */
public class FamilyPicturesPanel extends JPanel {

    private static final int GRID_COLS = 3;
    private static final int IMAGE_WIDTH = 250;
    private static final int IMAGE_HEIGHT = 200;

    public FamilyPicturesPanel() {
        initComponents();
    }

    private void initComponents() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(27, 54, 45));
        setBorder(new EmptyBorder(10, 0, 10, 0));

        // Title
        JLabel titleLabel = new JLabel("Gallery - Hotel & Family Pictures");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(229, 218, 195));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(titleLabel);
        add(Box.createVerticalStrut(15));

        // Image grid panel
        JPanel imagesPanel = new JPanel(new GridLayout(2, GRID_COLS, 15, 15));
        imagesPanel.setBackground(new Color(27, 54, 45));
        imagesPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Image resource paths
        String[] imagePaths = {
                "/melg/hotel/images/room1.jpg",
                "/melg/hotel/images/room2.jpg",
                "/melg/hotel/images/room3.jpg",
                "/melg/hotel/images/family1.jpg",
                "/melg/hotel/images/family2.jpg",
                "/melg/hotel/images/family3.jpg"
        };

        for (String path : imagePaths) {
            imagesPanel.add(createImagePanel(path));
        }

        add(imagesPanel);
    }

    private JPanel createImagePanel(String imagePath) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(34, 72, 56));
        panel.setBorder(BorderFactory.createLineBorder(new Color(229, 218, 195), 2));
        panel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));

        try {

            java.net.URL imgUrl = getClass().getResource(imagePath);

            if (imgUrl != null) {

                ImageIcon icon = new ImageIcon(imgUrl);

                Image scaledImage = icon.getImage().getScaledInstance(
                        IMAGE_WIDTH,
                        IMAGE_HEIGHT,
                        Image.SCALE_SMOOTH
                );

                JLabel imgLabel = new JLabel(new ImageIcon(scaledImage));
                imgLabel.setHorizontalAlignment(SwingConstants.CENTER);

                panel.add(imgLabel, BorderLayout.CENTER);

            } else {

                panel.add(createPlaceholderLabel(imagePath), BorderLayout.CENTER);

            }

        } catch (Exception e) {

            panel.add(createPlaceholderLabel(imagePath), BorderLayout.CENTER);

        }

        return panel;
    }

    private JLabel createPlaceholderLabel(String imagePath) {

        JLabel placeholder = new JLabel("[Image: " + new File(imagePath).getName() + "]");

        placeholder.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        placeholder.setHorizontalAlignment(SwingConstants.CENTER);
        placeholder.setVerticalAlignment(SwingConstants.CENTER);

        placeholder.setForeground(new Color(229, 218, 195));
        placeholder.setFont(new Font("SansSerif", Font.ITALIC, 12));

        placeholder.setBackground(new Color(50, 50, 50));
        placeholder.setOpaque(true);

        return placeholder;
    }
}