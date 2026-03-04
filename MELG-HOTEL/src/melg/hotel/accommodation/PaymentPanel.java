package melg.hotel.accommodation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Payment Panel for accommodation booking
 * Supports mPesa, Debit Card, and Credit Card
 */
public class PaymentPanel extends JPanel {
    private JRadioButton mpesaRadio;
    private JRadioButton debitCardRadio;
    private JRadioButton creditCardRadio;
    private JPanel mpesaPanel;
    private JPanel debitPanel;
    private JPanel creditPanel;

    public PaymentPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(27, 54, 45)); // Dark green
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title
        JLabel titleLabel = new JLabel("Payment Method");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(229, 218, 195)); // Gold text
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(15));

        // Payment options
        ButtonGroup paymentGroup = new ButtonGroup();

        // mPesa Option
        mpesaRadio = new JRadioButton("mPesa", true);
        mpesaRadio.setFont(new Font("SansSerif", Font.BOLD, 14));
        mpesaRadio.setForeground(Color.WHITE);
        mpesaRadio.setBackground(new Color(27, 54, 45));
        mpesaRadio.setFocusPainted(false);
        mpesaRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
        mpesaRadio.addActionListener(e -> updatePaymentFields());
        add(mpesaRadio);

        mpesaPanel = createMpesaPanel();
        mpesaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(mpesaPanel);
        add(Box.createVerticalStrut(20));

        // Debit Card Option
        debitCardRadio = new JRadioButton("Debit Card", false);
        debitCardRadio.setFont(new Font("SansSerif", Font.BOLD, 14));
        debitCardRadio.setForeground(Color.WHITE);
        debitCardRadio.setBackground(new Color(27, 54, 45));
        debitCardRadio.setFocusPainted(false);
        debitCardRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
        debitCardRadio.addActionListener(e -> updatePaymentFields());
        add(debitCardRadio);

        debitPanel = createCardPanel("Debit Card");
        debitPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        debitPanel.setVisible(false);
        add(debitPanel);
        add(Box.createVerticalStrut(20));

        // Credit Card Option
        creditCardRadio = new JRadioButton("Credit Card", false);
        creditCardRadio.setFont(new Font("SansSerif", Font.BOLD, 14));
        creditCardRadio.setForeground(Color.WHITE);
        creditCardRadio.setBackground(new Color(27, 54, 45));
        creditCardRadio.setFocusPainted(false);
        creditCardRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
        creditCardRadio.addActionListener(e -> updatePaymentFields());
        add(creditCardRadio);

        creditPanel = createCardPanel("Credit Card");
        creditPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        creditPanel.setVisible(false);
        add(creditPanel);

        // Add group
        paymentGroup.add(mpesaRadio);
        paymentGroup.add(debitCardRadio);
        paymentGroup.add(creditCardRadio);
    }

    private JPanel createMpesaPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(34, 72, 56)); // Panel green
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel label = new JLabel("Phone Number (for mPesa):");
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);

        JTextField phoneField = new JTextField(20);
        phoneField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        phoneField.setMaximumSize(new Dimension(300, 30));
        phoneField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(phoneField);

        panel.add(Box.createVerticalStrut(10));

        JLabel infoLabel = new JLabel("You will receive an mPesa prompt on your phone.");
        infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(200, 200, 200));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(infoLabel);

        return panel;
    }

    private JPanel createCardPanel(String cardType) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(34, 72, 56)); // Panel green
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Card Number
        JLabel cardNumLabel = new JLabel("Card Number:");
        cardNumLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cardNumLabel.setForeground(Color.WHITE);
        cardNumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cardNumLabel);

        JTextField cardNumField = new JTextField(20);
        cardNumField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cardNumField.setMaximumSize(new Dimension(300, 30));
        cardNumField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cardNumField);
        panel.add(Box.createVerticalStrut(10));

        // Cardholder Name
        JLabel nameLabel = new JLabel("Cardholder Name:");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);

        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nameField.setMaximumSize(new Dimension(300, 30));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));

        // Expiry and CVV
        JPanel expiryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        expiryPanel.setBackground(new Color(34, 72, 56));
        expiryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        expiryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel expiryLabel = new JLabel("Expiry (MM/YY):");
        expiryLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        expiryLabel.setForeground(Color.WHITE);

        JTextField expiryField = new JTextField(6);
        expiryField.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel cvvLabel = new JLabel("CVV:");
        cvvLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cvvLabel.setForeground(Color.WHITE);

        JTextField cvvField = new JTextField(4);
        cvvField.setFont(new Font("SansSerif", Font.PLAIN, 12));

        expiryPanel.add(expiryLabel);
        expiryPanel.add(expiryField);
        expiryPanel.add(cvvLabel);
        expiryPanel.add(cvvField);

        panel.add(expiryPanel);

        return panel;
    }

    private void updatePaymentFields() {
        mpesaPanel.setVisible(mpesaRadio.isSelected());
        debitPanel.setVisible(debitCardRadio.isSelected());
        creditPanel.setVisible(creditCardRadio.isSelected());
        revalidate();
        repaint();
    }

    public String getSelectedPaymentMethod() {
        if (mpesaRadio.isSelected()) {
            return "mPesa";
        } else if (debitCardRadio.isSelected()) {
            return "Debit Card";
        } else {
            return "Credit Card";
        }
    }
}
