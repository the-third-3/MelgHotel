package melg.hotel;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * MELG Hotel Management System
 * Main Entry Point and UI Controller
 */
public class MELGHOTEL extends JFrame {

    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private SidebarPanel sidebarPanel;

    public MELGHOTEL() {
        initComponents();
    }

    private void initComponents() {
        // --- Frame Settings ---
        setTitle("MELG Hotel Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Main Content Area (Center Cards) ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Theme.BG_DARK_GREEN);

        // --- Sidebar (Left Navigation) ---
        sidebarPanel = new SidebarPanel(new NavigationListener() {
            @Override
            public void navigateTo(String viewName) {
                cardLayout.show(mainContentPanel, viewName);
            }
        });
        add(sidebarPanel, BorderLayout.WEST);

        // Add actual cards
        mainContentPanel.add(new WelcomeView(viewName -> cardLayout.show(mainContentPanel, viewName)), "WelcomeView");
        mainContentPanel.add(new DashboardView(), "DashboardView");
        mainContentPanel.add(new AccommodationView(), "AccommodationView");
        mainContentPanel.add(new ServicesView(), "ServicesView");
        mainContentPanel.add(new MenuView(), "MenuView");
        mainContentPanel.add(new TermsView(), "TermsView");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Initialize Database connections/tables
        DatabaseHelper.initDatabase();

        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                MELGHOTEL frame = new MELGHOTEL();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ========================================================= //
    // THEME & INTERFACES
    // ========================================================= //

    public static class Theme {
        public static final Color BG_DARK_GREEN = new Color(27, 54, 45);
        public static final Color PANEL_GREEN = new Color(34, 72, 56);
        public static final Color TEXT_GOLD = new Color(229, 218, 195);
        public static final Color TEXT_WHITE = new Color(245, 245, 245);
        public static final Color HOVER_GREEN = new Color(45, 90, 70);
        public static final Color BUTTON_GOLD = new Color(229, 218, 195);
        public static final Color BUTTON_TEXT_DARK = new Color(27, 54, 45);

        public static final Font FONT_TITLE = new Font("Serif", Font.BOLD, 28);
        public static final Font FONT_HEADER = new Font("Serif", Font.BOLD, 20);
        public static final Font FONT_REGULAR = new Font("SansSerif", Font.PLAIN, 14);
        public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 14);
    }

    public interface NavigationListener {
        void navigateTo(String viewName);
    }

    // ========================================================= //
    // CUSTOM COMPONENTS
    // ========================================================= //

    public static class SidebarButton extends JButton {
        private boolean isHovered = false;
        private boolean isActive = false;

        public SidebarButton(String text) {
            super(text);
            setForeground(Theme.TEXT_GOLD);
            setBackground(Theme.PANEL_GREEN);
            setFont(Theme.FONT_BUTTON);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(true);
            setBorder(new EmptyBorder(10, 20, 10, 20));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setHorizontalAlignment(SwingConstants.LEFT);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        public void setActive(boolean active) {
            this.isActive = active;
            setForeground(active ? Theme.BUTTON_TEXT_DARK : Theme.TEXT_GOLD);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(isActive || isHovered ? Theme.BUTTON_GOLD : getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }
    }

    public static class SidebarPanel extends JPanel {
        private NavigationListener navigationCallback;
        private List<SidebarButton> buttons = new ArrayList<>();

        public SidebarPanel(NavigationListener navigationCallback) {
            this.navigationCallback = navigationCallback;
            setBackground(Theme.PANEL_GREEN);
            setPreferredSize(new Dimension(250, 0));
            setLayout(new BorderLayout());

            JPanel logoPanel = new JPanel(new BorderLayout());
            logoPanel.setBackground(Theme.PANEL_GREEN);
            logoPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
            JLabel logoLabel = new JLabel("MELG", SwingConstants.CENTER);
            logoLabel.setFont(Theme.FONT_TITLE);
            logoLabel.setForeground(Theme.TEXT_GOLD);
            logoPanel.add(logoLabel, BorderLayout.CENTER);
            add(logoPanel, BorderLayout.NORTH);

            JPanel linksPanel = new JPanel(new GridLayout(10, 1, 0, 5));
            linksPanel.setBackground(Theme.PANEL_GREEN);

            addButton(linksPanel, "Welcome / Home", "WelcomeView");
            addButton(linksPanel, "Dashboard", "DashboardView");
            addButton(linksPanel, "Accommodation", "AccommodationView");
            addButton(linksPanel, "Services", "ServicesView");
            addButton(linksPanel, "Menu", "MenuView");
            addButton(linksPanel, "Terms", "TermsView");

            add(linksPanel, BorderLayout.CENTER);
            if (!buttons.isEmpty()) buttons.get(0).setActive(true);
        }

        private void addButton(JPanel panel, String text, String viewName) {
            SidebarButton btn = new SidebarButton("  " + text);
            buttons.add(btn);
            btn.addActionListener(e -> {
                buttons.forEach(b -> b.setActive(false));
                btn.setActive(true);
                if (navigationCallback != null) navigationCallback.navigateTo(viewName);
            });
            panel.add(btn);
        }
    }

    // ========================================================= //
    // DATABASE HELPER
    // ========================================================= //

    public static class DatabaseHelper {
        private static final String URL = "jdbc:mysql://localhost:3306/melgHotel";
        private static final String USER = "root";
        private static final String PASSWORD = "";

        public static Connection getConnection() throws SQLException {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                try { Class.forName("com.mysql.jdbc.Driver"); } catch (Exception ex) { throw new SQLException("Driver missing"); }
            }
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }

        public static void initDatabase() {
            try (Connection con = getConnection(); Statement st = con.createStatement()) {
                st.execute("CREATE TABLE IF NOT EXISTS hotel_rooms (id INT AUTO_INCREMENT PRIMARY KEY, room_number VARCHAR(10) UNIQUE, room_type VARCHAR(50), price DOUBLE, status VARCHAR(20) DEFAULT 'Available')");
                System.out.println("Database Ready.");
            } catch (SQLException e) {
                System.err.println("Database not connected. Ensure XAMPP/MySQL is running.");
            }
        }
    }

    // ========================================================= //
    // VIEWS (Including placeholders for missing classes)
    // ========================================================= //

    public static class AccommodationView extends JPanel {
        public AccommodationView() {
            setLayout(new BorderLayout());
            setBackground(Theme.BG_DARK_GREEN);
            // Replaced missing AccommodationViewPanel with a simple placeholder
            JLabel label = new JLabel("Room Management Content Here", SwingConstants.CENTER);
            label.setForeground(Theme.TEXT_GOLD);
            add(label, BorderLayout.CENTER);
        }
    }

    public static class WelcomeView extends JPanel {
        private NavigationListener navigationCallback;
        private Image backgroundImage;

        public WelcomeView(NavigationListener navigationCallback) {
            this.navigationCallback = navigationCallback;
            setLayout(new BorderLayout());
            setBackground(Theme.BG_DARK_GREEN);
            setBorder(new EmptyBorder(50, 50, 50, 50));

            JLabel headerLabel = new JLabel("Welcome to MELG", SwingConstants.CENTER);
            headerLabel.setFont(new Font("Serif", Font.BOLD, 48));
            headerLabel.setForeground(Theme.TEXT_WHITE);
            add(headerLabel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            buttonPanel.setOpaque(false);
            
            JButton btn = new JButton("Get Started");
            btn.setPreferredSize(new Dimension(200, 50));
            btn.setBackground(Theme.TEXT_GOLD);
            btn.addActionListener(e -> navigationCallback.navigateTo("AccommodationView"));
            buttonPanel.add(btn);

            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    // Simple placeholders for other views
    public static class DashboardView extends JPanel { public DashboardView() { setBackground(Theme.BG_DARK_GREEN); add(new JLabel("Dashboard")).setForeground(Theme.TEXT_GOLD); }}
    public static class ServicesView extends JPanel { public ServicesView() { setBackground(Theme.BG_DARK_GREEN); add(new JLabel("Services")).setForeground(Theme.TEXT_GOLD); }}
    public static class MenuView extends JPanel { public MenuView() { setBackground(Theme.BG_DARK_GREEN); add(new JLabel("Menu")).setForeground(Theme.TEXT_GOLD); }}
    public static class TermsView extends JPanel { public TermsView() { setBackground(Theme.BG_DARK_GREEN); add(new JLabel("Terms")).setForeground(Theme.TEXT_GOLD); }}
}