package melg.hotel.accommodation;

import java.awt.*;
import java.util.Calendar;
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
        setBackground(new Color(27, 54, 45));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createMainContent() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(27, 54, 45));

        JLabel titleLabel = new JLabel("Book Your Accommodation");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(new Color(229, 218, 195));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        RoomStatusPanel roomStatusPanel = new RoomStatusPanel();
        mainPanel.add(roomStatusPanel);

        mainPanel.add(Box.createVerticalStrut(30));

        familyPicturesPanel = new FamilyPicturesPanel();
        mainPanel.add(familyPicturesPanel);

        mainPanel.add(Box.createVerticalStrut(30));

        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel);

        mainPanel.add(Box.createVerticalStrut(20));

        paymentPanel = new PaymentPanel();
        mainPanel.add(paymentPanel);

        mainPanel.add(Box.createVerticalStrut(20));

        JPanel pricePanel = createPriceAndButtonPanel();
        mainPanel.add(pricePanel);

        return mainPanel;
    }

    private JPanel createFormPanel() {

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                new EmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Guest Name:"), gbc);

        gbc.gridx = 1;
        nameField = createTextField(20);
        formPanel.add(nameField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Gender:"), gbc);

        gbc.gridx = 1;
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        formPanel.add(genderCombo, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Contact Number:"), gbc);

        gbc.gridx = 1;
        contactField = createTextField(20);
        formPanel.add(contactField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Check-in Date:"), gbc);

        gbc.gridx = 1;
        JPanel checkInPanel = createDatePanel();
        checkInMonth = (JSpinner) checkInPanel.getComponent(0);
        checkInDay = (JSpinner) checkInPanel.getComponent(2);
        checkInYear = (JSpinner) checkInPanel.getComponent(4);

        formPanel.add(checkInPanel, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Check-out Date:"), gbc);

        gbc.gridx = 1;
        JPanel checkOutPanel = createDatePanel();
        checkOutMonth = (JSpinner) checkOutPanel.getComponent(0);
        checkOutDay = (JSpinner) checkOutPanel.getComponent(2);
        checkOutYear = (JSpinner) checkOutPanel.getComponent(4);

        formPanel.add(checkOutPanel, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Number of People:"), gbc);

        gbc.gridx = 1;
        numPeopleSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        formPanel.add(numPeopleSpinner, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Room Type:"), gbc);

        gbc.gridx = 1;

        JPanel roomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        roomPanel.setBackground(Color.WHITE);

        vipRadio = new JRadioButton("VIP (Ksh 15,000/night)");
        regularRadio = new JRadioButton("Regular (Ksh 8,000/night)", true);

        ButtonGroup group = new ButtonGroup();
        group.add(vipRadio);
        group.add(regularRadio);

        roomPanel.add(vipRadio);
        roomPanel.add(regularRadio);

        formPanel.add(roomPanel, gbc);

        return formPanel;
    }

    private JPanel createPriceAndButtonPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(27, 54, 45));

        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setBackground(new Color(34, 72, 56));

        pricePanel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 218, 195), 2),
                new EmptyBorder(20, 30, 20, 30)
        ));

        JLabel label = new JLabel("Total Booking Cost");
        label.setForeground(new Color(229, 218, 195));
        label.setFont(new Font("SansSerif", Font.BOLD, 18));

        totalPriceLabel = new JLabel("Ksh 8,000.00");
        totalPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        totalPriceLabel.setForeground(new Color(229, 218, 195));

        pricePanel.add(label, BorderLayout.WEST);
        pricePanel.add(totalPriceLabel, BorderLayout.EAST);

        panel.add(pricePanel);

        panel.add(Box.createVerticalStrut(30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(27, 54, 45));

        JButton bookButton = createStyledButton("BOOK NOW");
        JButton cancelButton = createStyledButton("CANCEL");

        bookButton.addActionListener(e -> bookRoom());

        cancelButton.addActionListener(e -> clearForm());

        buttonPanel.add(bookButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel);

        return panel;
    }

    private void bookRoom() {

        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your name.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Booking Confirmed for " + name + "\nTotal: " + totalPriceLabel.getText(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearForm() {

        nameField.setText("");
        contactField.setText("");
        genderCombo.setSelectedIndex(0);
        numPeopleSpinner.setValue(1);
        regularRadio.setSelected(true);
    }

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));

        return label;
    }

    private JTextField createTextField(int size) {

        JTextField field = new JTextField(size);
        field.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        return field;
    }

    private JButton createStyledButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(76, 175, 80));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);

        return button;
    }

    private JPanel createDatePanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(Color.WHITE);

        Calendar cal = Calendar.getInstance();

        JSpinner month = new JSpinner(new SpinnerNumberModel(cal.get(Calendar.MONTH) + 1, 1, 12, 1));
        JSpinner day = new JSpinner(new SpinnerNumberModel(cal.get(Calendar.DAY_OF_MONTH), 1, 31, 1));
        JSpinner year = new JSpinner(new SpinnerNumberModel(cal.get(Calendar.YEAR), 2024, 2040, 1));

        panel.add(month);
        panel.add(new JLabel("/"));
        panel.add(day);
        panel.add(new JLabel("/"));
        panel.add(year);

        return panel;
    }

}