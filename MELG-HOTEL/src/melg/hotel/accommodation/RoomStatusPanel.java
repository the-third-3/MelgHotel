package melg.hotel.accommodation;

import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Room Status Panel displays availability and booking status of all rooms
 */
public class RoomStatusPanel extends JPanel {

    private List<Map<String, Object>> rooms = new ArrayList<>();

    public RoomStatusPanel() {
        initComponents();
        loadRoomStatus();
    }

    private void initComponents() {

        removeAll();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(27, 54, 45));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Room Status & Availability");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(229, 218, 195));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titleLabel);

        add(Box.createVerticalStrut(15));

        JPanel legendPanel = createLegendPanel();
        legendPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(legendPanel);

        add(Box.createVerticalStrut(15));
    }

    private void loadRoomStatus() {
        rooms.clear();
        loadRoomsFromDatabase();
        displayRooms();
    }

    private void loadRoomsFromDatabase() {

        try (Connection conn = getConnection()) {

            ensureSampleRooms(conn);

            String sql =
                    "SELECT r.id, r.room_number, r.room_type, r.price, r.status, " +
                    "CASE WHEN EXISTS (" +
                    "SELECT 1 FROM hotel_bookings b WHERE b.room_id=r.id AND b.status='Booked')" +
                    " THEN 'Booked' ELSE r.status END AS current_status " +
                    "FROM hotel_rooms r ORDER BY r.room_type,r.room_number";

            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    Map<String, Object> room = new HashMap<>();

                    room.put("id", rs.getInt("id"));
                    room.put("room_number", rs.getString("room_number"));
                    room.put("room_type", rs.getString("room_type"));
                    room.put("price", rs.getDouble("price"));
                    room.put("status", rs.getString("current_status"));

                    rooms.add(room);
                }
            }

            if (rooms.isEmpty()) {
                createDefaultRooms();
            }

        } catch (SQLException e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading room status: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void ensureSampleRooms(Connection conn) {

        try {

            String checkSql = "SELECT COUNT(*) FROM hotel_rooms";

            try (PreparedStatement check = conn.prepareStatement(checkSql);
                 ResultSet rs = check.executeQuery()) {

                rs.next();

                int count = rs.getInt(1);

                if (count == 0) {

                    String insert =
                            "INSERT INTO hotel_rooms(room_number,room_type,price,status) VALUES(?,?,?,?)";

                    try (PreparedStatement insertStmt = conn.prepareStatement(insert)) {

                        for (int i = 1; i <= 3; i++) {
                            insertStmt.setString(1, "VIP-10" + i);
                            insertStmt.setString(2, "VIP");
                            insertStmt.setDouble(3, 15000);
                            insertStmt.setString(4, "Available");
                            insertStmt.executeUpdate();
                        }

                        for (int i = 1; i <= 4; i++) {
                            insertStmt.setString(1, "REG-20" + i);
                            insertStmt.setString(2, "Regular");
                            insertStmt.setDouble(3, 8000);
                            insertStmt.setString(4, "Available");
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createDefaultRooms() {

        for (int i = 1; i <= 3; i++) {

            Map<String, Object> room = new HashMap<>();

            room.put("id", i);
            room.put("room_number", "VIP-10" + i);
            room.put("room_type", "VIP");
            room.put("price", 15000.0);
            room.put("status", i == 1 ? "Booked" : "Available");

            rooms.add(room);
        }

        for (int i = 1; i <= 4; i++) {

            Map<String, Object> room = new HashMap<>();

            room.put("id", 3 + i);
            room.put("room_number", "REG-20" + i);
            room.put("room_type", "Regular");
            room.put("price", 8000.0);
            room.put("status", i == 2 ? "Not Reserved" : "Available");

            rooms.add(room);
        }
    }

    private void displayRooms() {

        Map<String, List<Map<String, Object>>> roomsByType = new HashMap<>();

        for (Map<String, Object> room : rooms) {

            String type = (String) room.get("room_type");

            roomsByType.computeIfAbsent(type, k -> new ArrayList<>()).add(room);
        }

        if (roomsByType.containsKey("VIP")) {

            JLabel vipLabel = new JLabel("VIP Rooms (Ksh 15,000/night)");
            vipLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            vipLabel.setForeground(Color.WHITE);
            vipLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            add(vipLabel);
            add(Box.createVerticalStrut(10));

            add(createRoomGrid(roomsByType.get("VIP")));

            add(Box.createVerticalStrut(15));
        }

        if (roomsByType.containsKey("Regular")) {

            JLabel regLabel = new JLabel("Regular Rooms (Ksh 8,000/night)");
            regLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            regLabel.setForeground(Color.WHITE);
            regLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            add(regLabel);
            add(Box.createVerticalStrut(10));

            add(createRoomGrid(roomsByType.get("Regular")));
        }

        add(Box.createVerticalGlue());
    }

    private JPanel createRoomGrid(List<Map<String, Object>> roomList) {

        JPanel panel = new JPanel(new GridLayout(0, 4, 10, 10));
        panel.setBackground(new Color(27, 54, 45));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        for (Map<String, Object> room : roomList) {

            panel.add(createRoomCard(room));
        }

        return panel;
    }

    private JPanel createRoomCard(Map<String, Object> room) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(40, 85, 70));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 218, 195), 3),
                new EmptyBorder(12, 12, 12, 12)));

        String number = (String) room.get("room_number");
        String type = (String) room.get("room_type");
        String status = (String) room.get("status");
        double price = (Double) room.get("price");

        JLabel img = createRoomImageLabel(number, type);
        img.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(img);
        card.add(Box.createVerticalStrut(10));

        JLabel num = new JLabel(number);
        num.setFont(new Font("SansSerif", Font.BOLD, 16));
        num.setForeground(new Color(229, 218, 195));
        num.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(num);
        card.add(Box.createVerticalStrut(8));

        JLabel amenities = createAmenitiesLabel(type);
        amenities.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(amenities);
        card.add(Box.createVerticalStrut(8));

        JLabel statusLabel = new JLabel(" " + status + " ");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        statusLabel.setOpaque(true);

        if ("Available".equals(status)) {
            statusLabel.setBackground(new Color(76, 175, 80));
        } else if ("Booked".equals(status)) {
            statusLabel.setBackground(new Color(244, 67, 54));
        } else {
            statusLabel.setBackground(new Color(255, 152, 0));
        }

        statusLabel.setForeground(Color.WHITE);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(statusLabel);
        card.add(Box.createVerticalStrut(10));

        JLabel priceLabel = new JLabel("Ksh " + String.format("%,.0f", price));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        priceLabel.setForeground(new Color(229, 218, 195));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(priceLabel);

        return card;
    }

    private JLabel createAmenitiesLabel(String type) {

        String text =
                "VIP".equals(type)
                        ? "🛏️ King Bed • 🚿 Luxury Bath • 🍷 Mini Bar"
                        : "🛏️ Queen Bed • 🚿 Modern Bath • 📺 Smart TV";

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 9));
        label.setForeground(new Color(200, 200, 200));

        return label;
    }

    private JPanel createLegendPanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 5));
        panel.setBackground(new Color(34, 72, 56));

        panel.add(createLegendItem("Available", new Color(76, 175, 80)));
        panel.add(createLegendItem("Booked", new Color(244, 67, 54)));
        panel.add(createLegendItem("Not Reserved", new Color(255, 152, 0)));

        return panel;
    }

    private JPanel createLegendItem(String text, Color color) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setBackground(new Color(34, 72, 56));

        JLabel box = new JLabel();
        box.setOpaque(true);
        box.setBackground(color);
        box.setPreferredSize(new Dimension(20, 20));

        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);

        panel.add(box);
        panel.add(label);

        return panel;
    }

    private Connection getConnection() throws SQLException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found");
        }

        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/melgHotel",
                "root",
                ""
        );
    }

    public void refreshRoomStatus() {

        initComponents();
        loadRoomStatus();
        revalidate();
        repaint();
    }

    public List<Map<String, Object>> getAvailableRooms() {

        List<Map<String, Object>> list = new ArrayList<>();

        for (Map<String, Object> room : rooms) {

            if ("Available".equals(room.get("status"))) {
                list.add(room);
            }
        }

        return list;
    }

    public List<Map<String, Object>> getBookedRooms() {

        List<Map<String, Object>> list = new ArrayList<>();

        for (Map<String, Object> room : rooms) {

            if ("Booked".equals(room.get("status"))) {
                list.add(room);
            }
        }

        return list;
    }

    private JLabel createRoomImageLabel(String roomNumber, String type) {

        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(256, 160));
        label.setOpaque(true);
        label.setBackground(new Color(50, 50, 50));

        try {

            java.net.URL img =
                    getClass().getResource("/melg/hotel/images/" + roomNumber + ".jpg");

            if (img != null) {

                ImageIcon icon = new ImageIcon(img);
                Image scaled =
                        icon.getImage().getScaledInstance(256, 160, Image.SCALE_SMOOTH);

                label.setIcon(new ImageIcon(scaled));

            } else {

                label.setText("Room " + roomNumber);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setForeground(Color.WHITE);
            }

        } catch (Exception e) {

            label.setText("Room " + roomNumber);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
        }

        return label;
    }
}