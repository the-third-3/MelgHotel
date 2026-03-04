package melg.hotel.accommodation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;

/**
 * Comprehensive Accommodation Booking View Panel
 * Includes guest details, room selection, and payment options
 */
public class AccommodationViewPanel extends JPanel {
    private JComboBox<String> genderCombo;
    private JSpinner checkInMonth;
    private JSpinner checkInDay;
    private JSpinner checkInYear;
    private JSpinner checkOutMonth;
    private JSpinner checkOutDay;
    private JSpinner checkOutYear;
    private JSpinner numPeopleSpinner;
    private JRadioButton vipRadio;
    private JRadioButton regularRadio;
    private PaymentPanel paymentPanel;
    private JLabel totalPriceLabel;
    private FamilyPicturesPanel familyPicturesPanel;
    private JTextField nameField;
    private JTextField contactField;

    public AccommodationViewPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(27, 54, 45)); // Dark green background
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Main scrollable panel
        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setBackground(new Color(27, 54, 45));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(27, 54, 45));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Book Your Accommodation");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(new Color(229, 218, 195)); // Gold text
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Room Status Panel - Shows availability
        RoomStatusPanel roomStatusPanel = new RoomStatusPanel();
        mainPanel.add(roomStatusPanel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Family Pictures Section
        familyPicturesPanel = new FamilyPicturesPanel();
        mainPanel.add(familyPicturesPanel);
        mainPanel.add(Box.createVerticalStrut(30));

        // White Form Panel
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Payment Section
        paymentPanel = new PaymentPanel();
        mainPanel.add(paymentPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Price and buttons
        JPanel priceAndButtonPanel = createPriceAndButtonPanel();
        mainPanel.add(priceAndButtonPanel);

        return mainPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                new EmptyBorder(30, 30, 30, 30)
        ));
        formPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Guest Name
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Guest Name:"), gbc);

        gbc.gridx = 1;
        nameField = createTextField(20);
        formPanel.add(nameField, gbc);
        row++;

        // Gender
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Gender:"), gbc);

        gbc.gridx = 1;
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setBackground(Color.WHITE);
        genderCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formPanel.add(genderCombo, gbc);
        row++;

        // Contact Number
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Contact Number:"), gbc);

        gbc.gridx = 1;
        contactField = createTextField(20);
        formPanel.add(contactField, gbc);
        row++;

        // Check-in Date
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Check-in Date (MM/DD/YYYY):"), gbc);

        gbc.gridx = 1;
        JPanel checkInPanel = createDatePanel(1);
        checkInMonth = (JSpinner) checkInPanel.getComponent(0);
        checkInDay = (JSpinner) checkInPanel.getComponent(2);
        checkInYear = (JSpinner) checkInPanel.getComponent(4);
        formPanel.add(checkInPanel, gbc);
        row++;

        // Check-out Date
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Check-out Date (MM/DD/YYYY):"), gbc);

        gbc.gridx = 1;
        JPanel checkOutPanel = createDatePanel(2);
        checkOutMonth = (JSpinner) checkOutPanel.getComponent(0);
        checkOutDay = (JSpinner) checkOutPanel.getComponent(2);
        checkOutYear = (JSpinner) checkOutPanel.getComponent(4);
        formPanel.add(checkOutPanel, gbc);
        row++;

        // Number of People
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Number of People:"), gbc);

        gbc.gridx = 1;
        numPeopleSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        numPeopleSpinner.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formPanel.add(numPeopleSpinner, gbc);
        row++;

        // Room Type (VIP or Regular)
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Room Type:"), gbc);

        gbc.gridx = 1;
        JPanel roomTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        roomTypePanel.setBackground(Color.WHITE);

        ButtonGroup roomGroup = new ButtonGroup();
        vipRadio = new JRadioButton("VIP (Ksh 15,000/night)", false);
        regularRadio = new JRadioButton("Regular (Ksh 8,000/night)", true);

        vipRadio.setBackground(Color.WHITE);
        regularRadio.setBackground(Color.WHITE);
        vipRadio.setFont(new Font("SansSerif", Font.PLAIN, 14));
        regularRadio.setFont(new Font("SansSerif", Font.PLAIN, 14));

        roomGroup.add(vipRadio);
        roomGroup.add(regularRadio);

        roomTypePanel.add(vipRadio);
        roomTypePanel.add(regularRadio);

        formPanel.add(roomTypePanel, gbc);

        return formPanel;
    }

    private JPanel createPriceAndButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(27, 54, 45));
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Total Price - Professional styling
        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setBackground(new Color(34, 72, 56)); // Panel green
        pricePanel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 218, 195), 2),
                new EmptyBorder(20, 30, 20, 30)
        ));

        JLabel priceTextLabel = new JLabel("Total Booking Cost");
        priceTextLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        priceTextLabel.setForeground(new Color(229, 218, 195)); // Gold text

        totalPriceLabel = new JLabel("Ksh 8,000.00");
        totalPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        totalPriceLabel.setForeground(new Color(229, 218, 195)); // Gold for price amount

        JPanel costContentPanel = new JPanel(new BorderLayout());
        costContentPanel.setBackground(new Color(34, 72, 56));
        costContentPanel.add(priceTextLabel, BorderLayout.WEST);
        costContentPanel.add(totalPriceLabel, BorderLayout.EAST);

        pricePanel.add(costContentPanel);

        panel.add(pricePanel);
        panel.add(Box.createVerticalStrut(30));

        // Buttons - Professional styling with larger size
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonPanel.setBackground(new Color(27, 54, 45));

        JButton bookButton = createStyledButton("🏨 BOOK NOW ✓", new Color(76, 175, 80), Color.WHITE);
        JButton cancelButton = createStyledButton("✕ CANCEL", new Color(244, 67, 54), Color.WHITE);

        bookButton.setPreferredSize(new Dimension(200, 60));
        cancelButton.setPreferredSize(new Dimension(200, 60));

        bookButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();
            int numPeople = (Integer) numPeopleSpinner.getValue();
            String roomType = vipRadio.isSelected() ? "VIP" : "Regular";
            String paymentMethod = paymentPanel.getSelectedPaymentMethod();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Please enter your name to proceed with booking.",
                        "Name Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String confirmationMessage = "🏨 ════════════════════════════════════ 🏨\n" +
                    "   MELG HOTEL BOOKING CONFIRMATION\n" +
                    "🏨 ════════════════════════════════════ 🏨\n\n" +
                    "✓ Guest Name: " + name + "\n" +
                    "✓ Gender: " + gender + "\n" +
                    "✓ Number of Guests: " + numPeople + "\n" +
                    "✓ Room Type: " + roomType + "\n" +
                    "✓ Payment Method: " + paymentMethod + "\n\n" +
                    "💰 Total Cost: " + totalPriceLabel.getText() + "\n\n" +
                    "🏨 ════════════════════════════════════ 🏨\n" +
                    "Thank you for choosing MELG HOTEL!\n" +
                    "Your booking reference will be sent via email.";

            JOptionPane.showMessageDialog(null,
                    confirmationMessage,
                    "✓ Booking Confirmed - MELG HOTEL",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        cancelButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to cancel this booking?",
                    "Cancel Booking",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Reset form
                nameField.setText("");
                genderCombo.setSelectedIndex(0);
                contactField.setText("");
                numPeopleSpinner.setValue(1);
                regularRadio.setSelected(true);
            }
        });

        buttonPanel.add(bookButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(new Color(50, 50, 50)); // Dark text for white form
        return label;
    }

    private JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor, 2),
                new EmptyBorder(8, 16, 8, 16)
        ));
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(adjustBrightness(bgColor, -30));
                button.setForeground(textColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setForeground(textColor);
            }
        });
        
        return button;
    }

    private Color adjustBrightness(Color color, int adjustment) {
        int r = Math.max(0, Math.min(255, color.getRed() + adjustment));
        int g = Math.max(0, Math.min(255, color.getGreen() + adjustment));
        int b = Math.max(0, Math.min(255, color.getBlue() + adjustment));
        return new Color(r, g, b);
    }

    private JPanel createDatePanel(int daysOffset) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(Color.WHITE);

        Calendar cal = Calendar.getInstance();
        if (daysOffset == 2) {
            cal.add(Calendar.DAY_OF_MONTH, 1); // Check-out is next day
        }

        // Month spinner (1-12)
        JSpinner monthSpinner = new JSpinner(new SpinnerNumberModel(cal.get(Calendar.MONTH) + 1, 1, 12, 1));
        monthSpinner.setPreferredSize(new Dimension(50, 25));
        monthSpinner.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(monthSpinner);

        panel.add(new JLabel("/"));

        // Day spinner (1-31)
        JSpinner daySpinner = new JSpinner(new SpinnerNumberModel(cal.get(Calendar.DAY_OF_MONTH), 1, 31, 1));
        daySpinner.setPreferredSize(new Dimension(50, 25));
        daySpinner.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(daySpinner);

        panel.add(new JLabel("/"));

        // Year spinner (current year to next 10 years)
        int currentYear = cal.get(Calendar.YEAR);
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, 2024, 2040, 1));
        yearSpinner.setPreferredSize(new Dimension(70, 25));
        yearSpinner.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(yearSpinner);

        return panel;
    }
}
