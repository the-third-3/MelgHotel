package melg.hotel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class MELGHOTEL extends JFrame {

    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public MELGHOTEL() {
        initComponents();
    }

    private void initComponents() {
        // --- Frame Settings ---
        setTitle("MELG Hotel Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800); // Standard starting size
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BG_DARK_GREEN);

        // --- Main Content Area (Center Cards) ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Theme.BG_DARK_GREEN);

        // --- Sidebar (Left Navigation) ---
        SidebarPanel sidebarPanel = new SidebarPanel(new NavigationListener() {
            @Override
            public void navigateTo(String viewName) {
                cardLayout.show(mainContentPanel, viewName);
            }
        });
        add(sidebarPanel, BorderLayout.WEST);

        // Add actual cards
        mainContentPanel.add(new WelcomeView(new NavigationListener() {
            @Override
            public void navigateTo(String viewName) {
                cardLayout.show(mainContentPanel, viewName);
            }
        }), "WelcomeView");

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

        // Run UI in the Event Dispatch Thread
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set system look and feel for better font rendering initially
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                    MELGHOTEL frame = new MELGHOTEL();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // ========================================================= //
    // NESTED CLASSES (Consolidated from packages)
    // ========================================================= //

    /**
     * Centralized theme colors and fonts for MELG-HOTEL UI.
     */
    public static class Theme {
        // --- Colors ---
        public static final Color BG_DARK_GREEN = new Color(27, 54, 45); // #1B362D
        public static final Color PANEL_GREEN = new Color(34, 72, 56); // approx #224838
        public static final Color TEXT_GOLD = new Color(229, 218, 195); // #E5DAC3
        public static final Color TEXT_WHITE = new Color(245, 245, 245);
        public static final Color HOVER_GREEN = new Color(45, 90, 70);
        public static final Color BUTTON_GOLD = TEXT_GOLD;
        public static final Color BUTTON_TEXT_DARK = BG_DARK_GREEN;

        // --- Fonts ---
        public static final Font FONT_TITLE = new Font("Serif", Font.BOLD, 28);
        public static final Font FONT_HEADER = new Font("Serif", Font.BOLD, 20);
        public static final Font FONT_REGULAR = new Font("SansSerif", Font.PLAIN, 14);
        public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
        public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 14);
    }

    /**
     * Interface to handle navigation events from the sidebar to the main content
     * area.
     */
    public interface NavigationListener {
        void navigateTo(String viewName);
    }

    /**
     * A custom styled text field for input forms.
     */
    public static class CustomTextField extends JTextField {
        public CustomTextField(int columns) {
            super(columns);
            setBackground(Theme.BG_DARK_GREEN);
            setForeground(Theme.TEXT_GOLD);
            setCaretColor(Theme.TEXT_GOLD);
            setFont(Theme.FONT_REGULAR);
            setBorder(new EmptyBorder(8, 12, 8, 12));
            setOpaque(false); // We will draw the rounded background manually
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

            g2.setColor(Theme.TEXT_GOLD);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

            super.paintComponent(g);
            g2.dispose();
        }
    }

    /**
     * A custom styled button used primarily in the sidebar navigation.
     */
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
            setBorder(new EmptyBorder(10, 20, 10, 20)); // Padding
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setHorizontalAlignment(SwingConstants.LEFT);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        public void setActive(boolean active) {
            this.isActive = active;
            if (active) {
                setForeground(Theme.BUTTON_TEXT_DARK);
            } else {
                setForeground(Theme.TEXT_GOLD);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (isActive || isHovered) {
                g.setColor(Theme.BUTTON_GOLD);
            } else {
                g.setColor(getBackground());
            }
            g.fillRect(0, 0, getWidth(), getHeight());

            super.paintComponent(g);
        }
    }

    /**
     * The left sidebar navigation panel.
     */
    public static class SidebarPanel extends JPanel {
        private NavigationListener navigationCallback;
        private List<SidebarButton> buttons;

        public SidebarPanel(NavigationListener navigationCallback) {
            this.navigationCallback = navigationCallback;
            this.buttons = new ArrayList<>();
            initComponents();
        }

        private void initComponents() {
            setBackground(Theme.PANEL_GREEN);
            setPreferredSize(new Dimension(250, getHeight()));
            setLayout(new BorderLayout());

            JPanel logoPanel = new JPanel(new BorderLayout());
            logoPanel.setBackground(Theme.PANEL_GREEN);
            logoPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

            JLabel logoLabel = new JLabel("MELG", SwingConstants.CENTER);
            logoLabel.setFont(Theme.FONT_TITLE);
            logoLabel.setForeground(Theme.TEXT_GOLD);
            logoPanel.add(logoLabel, BorderLayout.CENTER);

            add(logoPanel, BorderLayout.NORTH);

            JPanel linksPanel = new JPanel(new GridLayout(10, 1, 0, 10)); // 10 rows, 1 col, 10px vgap
            linksPanel.setBackground(Theme.PANEL_GREEN);
            linksPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // No outer padding

            addButton(linksPanel, "Welcome / Home", "WelcomeView");
            addButton(linksPanel, "Dashboard", "DashboardView");
            addButton(linksPanel, "Accommodation", "AccommodationView");
            addButton(linksPanel, "Services", "ServicesView");
            addButton(linksPanel, "Menu", "MenuView");
            addButton(linksPanel, "Terms", "TermsView");

            add(linksPanel, BorderLayout.CENTER);

            if (!buttons.isEmpty()) {
                buttons.get(0).setActive(true);
            }
        }

        private void addButton(JPanel panel, String text, final String viewName) {
            final SidebarButton btn = new SidebarButton("  " + text); // Add a little indent
            buttons.add(btn);

            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (SidebarButton b : buttons) {
                        b.setActive(false);
                    }
                    btn.setActive(true);

                    if (navigationCallback != null) {
                        navigationCallback.navigateTo(viewName);
                    }
                }
            });

            panel.add(btn);
        }
    }

    public static class DatabaseHelper {
        private static final String URL = "jdbc:mysql://localhost:3306/melgHotel";
        private static final String USER = "root";
        private static final String PASSWORD = "";

        public static Connection getConnection() throws SQLException {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.err.println("Old driver not found, trying fallback...");
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                    throw new SQLException(
                            "MySQL Driver not found! Please add the mysql-connector-java.jar to your libraries.");
                }
            }
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }

        public static void initDatabase() {
            Connection con = null;
            Statement st = null;

            try {
                con = getConnection();
                st = con.createStatement();

                st.execute("CREATE TABLE IF NOT EXISTS hotel_users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(50) UNIQUE NOT NULL, " +
                        "password VARCHAR(100) NOT NULL, " +
                        "role VARCHAR(20) NOT NULL)");

                try {
                    st.execute(
                            "INSERT IGNORE INTO hotel_users (username, password, role) VALUES ('admin', 'admin123', 'Admin')");
                } catch (SQLException ignored) {
                }

                st.execute("CREATE TABLE IF NOT EXISTS hotel_rooms (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "room_number VARCHAR(10) UNIQUE NOT NULL, " +
                        "room_type VARCHAR(50) NOT NULL, " +
                        "price DOUBLE NOT NULL, " +
                        "status VARCHAR(20) DEFAULT 'Available')");

                st.execute("CREATE TABLE IF NOT EXISTS hotel_bookings (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "guest_name VARCHAR(100) NOT NULL, " +
                        "guest_contact VARCHAR(50), " +
                        "room_id INT, " +
                        "check_in_date DATE NOT NULL, " +
                        "check_out_date DATE NOT NULL, " +
                        "total_amount DOUBLE, " +
                        "status VARCHAR(20) DEFAULT 'Booked', " +
                        "FOREIGN KEY (room_id) REFERENCES hotel_rooms(id))");

                st.execute("CREATE TABLE IF NOT EXISTS hotel_services (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "item_name VARCHAR(100) NOT NULL, " +
                        "category VARCHAR(50) NOT NULL, " +
                        "price DOUBLE NOT NULL, " +
                        "description TEXT)");

                System.out.println("Hotel database schema initialized successfully!");

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Database Connection Error!\n\n" +
                                "Make sure your local MySQL/XAMPP server is running and the 'ongili' database exists.\n\n"
                                +
                                "Error: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    if (st != null)
                        st.close();
                    if (con != null)
                        con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static class AccommodationView extends JPanel {
        public AccommodationView() {
            setLayout(new BorderLayout());
            setBackground(Theme.BG_DARK_GREEN);

            JLabel label = new JLabel("Accommodation / Booking View (Under Construction)", SwingConstants.CENTER);
            label.setFont(Theme.FONT_HEADER);
            label.setForeground(Theme.TEXT_GOLD);
            add(label, BorderLayout.CENTER);
        }
    }

    public static class DashboardView extends JPanel {
        public DashboardView() {
            setLayout(new BorderLayout());
            setBackground(Theme.BG_DARK_GREEN);

            JLabel label = new JLabel("Dashboard View (Under Construction)", SwingConstants.CENTER);
            label.setFont(Theme.FONT_HEADER);
            label.setForeground(Theme.TEXT_GOLD);
            add(label, BorderLayout.CENTER);
        }
    }

    public static class MenuView extends JPanel {
        public MenuView() {
            setLayout(new BorderLayout());
            setBackground(Theme.BG_DARK_GREEN);

           JLabel label = new JLabel("FOOD $ DRINKS", SwingConstants.CENTER);
            label.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.WHITE));
            label.setFont(Theme.FONT_HEADER);
            label.setForeground(Theme.TEXT_GOLD);
            label.setBounds(10,2,900,40);
            add(label,BorderLayout.CENTER);
            JLabel label1=new JLabel("Breakfast");
            add(label1);
            label1.setBounds(10,50,100,50);
            label1.setFont(Theme.FONT_HEADER);
            label1.setForeground(Theme.TEXT_GOLD);
             JLabel label2 = new JLabel("Full Breakfast");
            label2.setFont(Theme.FONT_REGULAR);
            label2.setForeground(Theme.TEXT_GOLD);
            label2.setBounds(10,100,200,50);
            add(label2);
            JLabel Break1=new JLabel("Contimential Breakfast");
            add(Break1);
            Break1.setBounds(10,140,480,50);
            Break1.setFont(Theme.FONT_REGULAR);
            Break1.setForeground(Theme.TEXT_GOLD);
            Break1.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.WHITE));
             JLabel label3 = new JLabel("Lunch");
            label3.setFont(Theme.FONT_HEADER);
            label3.setForeground(Theme.TEXT_GOLD);
            label3.setBounds(10,200,200,50);
            add(label3);
            JLabel Break2=new JLabel("Pane Dishes");
            add(Break2);
            Break2.setBounds(10,240,100,50);
            Break2.setFont(Theme.FONT_REGULAR);
            Break2.setForeground(Theme.TEXT_GOLD);
             JLabel label4 = new JLabel("Ugali Dishes");
            label4.setFont(Theme.FONT_REGULAR);
            label4.setForeground(Theme.TEXT_GOLD);
            label4.setBounds(10,280,200,50);
            add(label4);
            JLabel Break3=new JLabel("Raw Prawns");
            add(Break3);
            Break3.setBounds(10,320,480,50);
            Break3.setFont(Theme.FONT_REGULAR);
            Break3.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.WHITE));
            Break3.setForeground(Theme.TEXT_GOLD);
             JLabel label5 = new JLabel("Snacks");
            label5.setFont(Theme.FONT_HEADER);
            label5.setForeground(Theme.TEXT_GOLD);
            label5.setBounds(10,380,200,50);
            add(label5);
            JLabel Break4=new JLabel("Mandazi");
            add(Break4);
            Break4.setBounds(10,420,100,50);
            Break4.setFont(Theme.FONT_REGULAR);
            Break4.setForeground(Theme.TEXT_GOLD);
             JLabel label6 = new JLabel("Samosa");
            label6.setFont(Theme.FONT_REGULAR);
            label6.setForeground(Theme.TEXT_GOLD);
            label6.setBounds(10,460,200,50);
            add(label6);
            JLabel Break5=new JLabel("Chicken wings");
            add(Break5);
            Break5.setBounds(10,500,480,50);
            Break5.setFont(Theme.FONT_REGULAR);
            Break5.setForeground(Theme.TEXT_GOLD);
            Break5.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.WHITE));
             JLabel label7 = new JLabel("Desserts");
            label7.setFont(Theme.FONT_HEADER);
            label7.setForeground(Theme.TEXT_GOLD);
            label7.setBounds(10,560,200,50);
            add(label7);
            JLabel Break6=new JLabel("Chocolate Cake");
            add(Break6);
            Break6.setBounds(10,600,200,50);
            Break6.setFont(Theme.FONT_REGULAR);
            Break6.setForeground(Theme.TEXT_GOLD);
             JLabel label8 = new JLabel("Fruit Salad");
            label8.setFont(Theme.FONT_REGULAR);
            label8.setForeground(Theme.TEXT_GOLD);
            label8.setBounds(10,640,480,50);
            add(label8);
            label8.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.WHITE));
            
            JLabel breakfast =new JLabel("KSH 400");
            breakfast.setBounds(390,80,100,40);
            breakfast.setFont(Theme.FONT_HEADER);
            breakfast.setForeground(Theme.TEXT_GOLD);
            add(breakfast);
             JLabel breakfast1 =new JLabel("KSH 500");
            breakfast1.setBounds(390,230,100,40);
            breakfast1.setFont(Theme.FONT_HEADER);
            breakfast1.setForeground(Theme.TEXT_GOLD);
            add(breakfast1);
             JLabel breakfast2 =new JLabel("KSH 200");
            breakfast2.setBounds(390,410,100,40);
            breakfast2.setFont(Theme.FONT_HEADER);
            breakfast2.setForeground(Theme.TEXT_GOLD);
            add(breakfast2);
             JLabel breakfast3 =new JLabel("KSH 250");
            breakfast3.setBounds(390,590,100,40);
            breakfast3.setFont(Theme.FONT_HEADER);
            breakfast3.setForeground(Theme.TEXT_GOLD);
            add(breakfast3);
              JButton order1 =new JButton("ORDER");
            order1.setBounds(390,140,80,30);
            order1.setFont(Theme.FONT_SMALL);
            order1.setForeground(Theme.BG_DARK_GREEN);
            add(order1);
             JButton order2=new JButton("ORDER");
             order2.setBounds(390,290,80,30);
             order2.setFont(Theme.FONT_SMALL);
             order2.setForeground(Theme.BG_DARK_GREEN);
            add( order2);
             JButton  order3=new JButton("ORDER");
            order3.setBounds(390,470,80,30);
            order3.setFont(Theme.FONT_SMALL);
            order3.setForeground(Theme.BG_DARK_GREEN);
            add(order3);
             JButton order4 =new JButton("ORDER");
            order4.setBounds(390,650,80,30);
            order4.setFont(Theme.FONT_SMALL);
            order4.setForeground(Theme.BG_DARK_GREEN);
            add(order4);
            JSeparator vertical=new JSeparator(SwingConstants.VERTICAL);
            vertical.setBounds(500,40,2,800);
            add(vertical);
            
             JLabel label66 = new JLabel("Standard Room");
            label66.setFont(Theme.FONT_HEADER);
            label66.setForeground(Theme.TEXT_GOLD);
            label66.setBounds(510,50,200,50);
            add(label66);
            JLabel Break10=new JLabel("Comfortable Rooms with Premium Amenities");
            add(Break10);
            Break10.setBounds(510,220,400,50);
            Break10.setFont(Theme.FONT_REGULAR);
            Break10.setForeground(Theme.TEXT_GOLD);
             JLabel Price1 = new JLabel("KSH 3,500");
            Price1.setFont(Theme.FONT_HEADER);
            Price1.setForeground(Theme.TEXT_GOLD);
            Price1.setBounds(800,100,200,40);
            add(Price1);
            JButton  order5=new JButton("ORDER");
            order5.setBounds(820,150,80,30);
            order5.setFont(Theme.FONT_SMALL);
            order5.setForeground(Theme.BG_DARK_GREEN);
            add(order5);
            Break10.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.WHITE));
             JLabel label10 = new JLabel("Deluxe Room");
            label10.setFont(Theme.FONT_HEADER);
            label10.setForeground(Theme.TEXT_GOLD);
            label10.setBounds(510,260,200,50);
            add(label10);
            JLabel Break11=new JLabel("Spacious Room with Premium amenities");
            add(Break11);
            Break11.setBounds(510,430,400,50);
            Break11.setFont(Theme.FONT_REGULAR);
            Break11.setForeground(Theme.TEXT_GOLD);
             JLabel Price2 = new JLabel("KSH 5,000");
            Price2.setFont(Theme.FONT_HEADER);
            Price2.setForeground(Theme.TEXT_GOLD);
            Price2.setBounds(800,310,200,40);
            add(Price2);
            JButton  order6=new JButton("ORDER");
            order6.setBounds(820,360,80,30);
            order6.setFont(Theme.FONT_SMALL);
            order6.setForeground(Theme.BG_DARK_GREEN);
            add(order6);
            Break11.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.WHITE));
             JLabel label12 = new JLabel("VIP Suite");
            label12.setFont(Theme.FONT_HEADER);
            label12.setForeground(Theme.TEXT_GOLD);
            label12.setBounds(510,470,200,50);
            add(label12);
            
             JLabel label13 = new JLabel("Luxury Suites with Premium Amenities");
            label13.setFont(Theme.FONT_REGULAR);
            label13.setForeground(Theme.TEXT_GOLD);
            label13.setBounds(510,650,250,50);
            add(label13);
             JLabel Price3 = new JLabel("KSH 8,500");
            Price3.setFont(Theme.FONT_HEADER);
            Price3.setForeground(Theme.TEXT_GOLD);
            Price3.setBounds(800,540,200,40);
            add(Price3);
            JButton  order7=new JButton("ORDER");
            order7.setBounds(820,590,80,30);
            order7.setFont(Theme.FONT_SMALL);
            order7.setForeground(Theme.BG_DARK_GREEN);
            add(order7);
            
           
            
        
        }
    }

    public static class ServicesView extends JPanel {
        public ServicesView() {
            setLayout(new BorderLayout());
            setBackground(Theme.BG_DARK_GREEN);

            JLabel label = new JLabel("Services View (Under Construction)", SwingConstants.CENTER);
            label.setFont(Theme.FONT_HEADER);
            label.setForeground(Theme.TEXT_GOLD);
            add(label, BorderLayout.CENTER);
        }
    }

    public static class TermsView extends JPanel {
        public TermsView() {
            setLayout(new BorderLayout());
            setBackground(Theme.BG_DARK_GREEN);

            JLabel label = new JLabel("Terms & Conditions View (Under Construction)", SwingConstants.CENTER);
            label.setFont(Theme.FONT_HEADER);
            label.setForeground(Theme.TEXT_GOLD);
            add(label, BorderLayout.CENTER);
        }
    }

    /**
     * The landing view of the application.
     */
    public static class WelcomeView extends JPanel {
        private NavigationListener navigationCallback;
        private java.awt.Image backgroundImage;

        public WelcomeView(NavigationListener navigationCallback) {
            this.navigationCallback = navigationCallback;
            try {
                // Load the image from the classpath
                java.net.URL imgUrl = getClass().getResource("/melg/hotel/images/background.jpeg");
                if (imgUrl != null) {
                    backgroundImage = new javax.swing.ImageIcon(imgUrl).getImage();
                } else {
                    System.err.println("Background image not found at /melg/hotel/images/background.jpg");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            initComponents();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // Draw the image scaled to fit the entire panel width and height
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

                // Optional: overlay a semi-transparent dark tint so the text is still readable
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 0, 0, 150)); // Black with ~60% transparency
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        }

        private void initComponents() {
            setLayout(new BorderLayout());
            setBackground(Theme.BG_DARK_GREEN);
            setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setOpaque(false);

            JLabel headerLabel = new JLabel("Welcome to MELG", SwingConstants.CENTER);
            headerLabel.setFont(new Font(Theme.FONT_TITLE.getName(), Font.BOLD, 48));
            headerLabel.setForeground(Theme.TEXT_WHITE);

            JLabel subheadLabel = new JLabel("Experience Unparalleled Comfort", SwingConstants.CENTER);
            subheadLabel.setFont(new Font(Theme.FONT_HEADER.getName(), Font.ITALIC, 24));
            subheadLabel.setForeground(Theme.TEXT_GOLD);

            headerPanel.add(headerLabel, BorderLayout.CENTER);
            headerPanel.add(subheadLabel, BorderLayout.SOUTH);

            add(headerPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            buttonPanel.setOpaque(false);

            buttonPanel.add(createActionButton("Accommodation", "AccommodationView"));
            buttonPanel.add(createActionButton("View Services", "ServicesView"));
            buttonPanel.add(createActionButton("Contact Admin", "DashboardView"));

            add(buttonPanel, BorderLayout.SOUTH);
        }

        private JButton createActionButton(String text, final String targetView) {
            JButton btn = new JButton(text);
            btn.setFont(Theme.FONT_BUTTON);
            btn.setForeground(Theme.BG_DARK_GREEN);
            btn.setBackground(Theme.TEXT_GOLD);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(200, 45));

            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (navigationCallback != null) {
                        navigationCallback.navigateTo(targetView);
                    }
                }
            });

            return btn;
        }
    }
}
