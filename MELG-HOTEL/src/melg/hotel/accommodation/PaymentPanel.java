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
        setBackground(new Color(27, 54, 45));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title
        JLabel titleLabel = new JLabel("Payment Method");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(229, 218, 195));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(15));

        ButtonGroup paymentGroup = new ButtonGroup();

        // ================= M-PESA =================
        mpesaRadio = new JRadioButton("mPesa");
        mpesaRadio.setSelected(true);
        styleRadioButton(mpesaRadio);
        mpesaRadio.addActionListener(e -> updatePaymentFields());
        add(mpesaRadio);

        mpesaPanel = createMpesaPanel();
        add(mpesaPanel);

        add(Box.createVerticalStrut(20));

        // ================= DEBIT CARD =================
        debitCardRadio = new JRadioButton("Debit Card");
        styleRadioButton(debitCardRadio);
        debitCardRadio.addActionListener(e -> updatePaymentFields());
        add(debitCardRadio);

        debitPanel = createCardPanel();
        debitPanel.setVisible(false);
        add(debitPanel);

        add(Box.createVerticalStrut(20));

        // ================= CREDIT CARD =================
        creditCardRadio = new JRadioButton("Credit Card");
        styleRadioButton(creditCardRadio);
        creditCardRadio.addActionListener(e -> updatePaymentFields());
        add(creditCardRadio);

        creditPanel = createCardPanel();
        creditPanel.setVisible(false);
        add(creditPanel);

        // Add radios to group
        paymentGroup.add(mpesaRadio);
        paymentGroup.add(debitCardRadio);
        paymentGroup.add(creditCardRadio);
    }

    // Style radio buttons
    private void styleRadioButton(JRadioButton radio) {
        radio.setFont(new Font("SansSerif", Font.BOLD, 14));
        radio.setForeground(Color.WHITE);
        radio.setBackground(new Color(27, 54, 45));
        radio.setFocusPainted(false);
        radio.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // ================= M-PESA PANEL =================
    private JPanel createMpesaPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(34, 72, 56));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel phoneLabel = new JLabel("Phone Number (for mPesa):");
        phoneLabel.setForeground(Color.WHITE);
        phoneLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(phoneLabel);

        JTextField phoneField = new JTextField();
        phoneField.setMaximumSize(new Dimension(300, 30));
        panel.add(phoneField);

        panel.add(Box.createVerticalStrut(10));

        JLabel infoLabel = new JLabel("You will receive an mPesa prompt on your phone.");
        infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(200, 200, 200));
        panel.add(infoLabel);

        return panel;
    }

    // ================= CARD PANEL =================
    private JPanel createCardPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(34, 72, 56));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        // Card Number
        JLabel cardNumLabel = new JLabel("Card Number:");
        cardNumLabel.setForeground(Color.WHITE);
        panel.add(cardNumLabel);

        JTextField cardNumField = new JTextField();
        cardNumField.setMaximumSize(new Dimension(300, 30));
        panel.add(cardNumField);

        panel.add(Box.createVerticalStrut(10));

        // Cardholder
        JLabel nameLabel = new JLabel("Cardholder Name:");
        nameLabel.setForeground(Color.WHITE);
        panel.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(300, 30));
        panel.add(nameField);

        panel.add(Box.createVerticalStrut(10));

        // Expiry + CVV
        JPanel expiryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        expiryPanel.setBackground(new Color(34, 72, 56));

        JLabel expiryLabel = new JLabel("Expiry (MM/YY):");
        expiryLabel.setForeground(Color.WHITE);

        JTextField expiryField = new JTextField(5);

        JLabel cvvLabel = new JLabel("CVV:");
        cvvLabel.setForeground(Color.WHITE);

        JTextField cvvField = new JTextField(3);

        expiryPanel.add(expiryLabel);
        expiryPanel.add(expiryField);
        expiryPanel.add(cvvLabel);
        expiryPanel.add(cvvField);

        panel.add(expiryPanel);

        return panel;
    }

    // ================= UPDATE PAYMENT PANELS =================
    private void updatePaymentFields() {

        mpesaPanel.setVisible(mpesaRadio.isSelected());
        debitPanel.setVisible(debitCardRadio.isSelected());
        creditPanel.setVisible(creditCardRadio.isSelected());

        revalidate();
        repaint();
    }

    // ================= GET SELECTED METHOD =================
    public String getSelectedPaymentMethod() {

        if (mpesaRadio.isSelected()) {
            return "mPesa";
        }

        if (debitCardRadio.isSelected()) {
            return "Debit Card";
        }

        return "Credit Card";
    }
}