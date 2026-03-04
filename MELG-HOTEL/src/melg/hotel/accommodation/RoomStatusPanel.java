package melg.hotel.accommodation;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Room Status Panel displays availability and booking status of all rooms
 * Shows room type, number, current status, and price per night
 */
public class RoomStatusPanel extends JPanel {
    private List<Map<String, Object>> rooms;

    public RoomStatusPanel() {
        initComponents();
        loadRoomStatus();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(27, 54, 45)); // Dark green
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title
        JLabel titleLabel = new JLabel("Room Status & Availability");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(229, 218, 195)); // Gold text
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(15));

        // Legend
        JPanel legendPanel = createLegendPanel();
        legendPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(legendPanel);
        add(Box.createVerticalStrut(15));

        // Rooms will be added here after loading from database
    }

    private void loadRoomStatus() {
        rooms = new ArrayList<>();
        loadRoomsFromDatabase();
        displayRooms();
    }

    private void loadRoomsFromDatabase() {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();

            // Create sample rooms if none exist
            ensureSampleRooms(conn);

            // Get all rooms with their booking status
            String sql = "SELECT r.id, r.room_number, r.room_type, r.price, r.status, " +
                    "CASE WHEN EXISTS (SELECT 1 FROM hotel_bookings b WHERE b.room_id = r.id AND b.status = 'Booked') " +
                    "THEN 'Booked' ELSE r.status END as current_status " +
                    "FROM hotel_rooms r ORDER BY r.room_type, r.room_number";

            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> room = new HashMap<>();
                room.put("id", rs.getInt("id"));
                room.put("room_number", rs.getString("room_number"));
                room.put("room_type", rs.getString("room_type"));
                room.put("price", rs.getDouble("price"));
                room.put("status", rs.getString("current_status"));
                rooms.add(room);
            }

            // If no rooms in database, create default ones
            if (rooms.isEmpty()) {
                createDefaultRooms();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error loading room status: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void ensureSampleRooms(Connection conn) {
        try {
            String checkSql = "SELECT COUNT(*) as count FROM hotel_rooms";
            PreparedStatement checkPstmt = conn.prepareStatement(checkSql);
            ResultSet rs = checkPstmt.executeQuery();
            rs.next();
            int count = rs.getInt("count");

            if (count == 0) {
                // Insert sample VIP rooms
                String insertSql = "INSERT INTO hotel_rooms (room_number, room_type, price, status) VALUES (?, ?, ?, ?)";
                PreparedStatement insertPstmt = conn.prepareStatement(insertSql);

                for (int i = 1; i <= 3; i++) {
                    insertPstmt.setString(1, "VIP-10" + i);
                    insertPstmt.setString(2, "VIP");
                    insertPstmt.setDouble(3, 15000.0);
                    insertPstmt.setString(4, "Available");
                    insertPstmt.executeUpdate();
                }

                // Insert sample Regular rooms
                for (int i = 1; i <= 4; i++) {
                    insertPstmt.setString(1, "REG-20" + i);
                    insertPstmt.setString(2, "Regular");
                    insertPstmt.setDouble(3, 8000.0);
                    insertPstmt.setString(4, "Available");
                    insertPstmt.executeUpdate();
                }

                insertPstmt.close();
            }
            checkPstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createDefaultRooms() {
        // Create mock data if database is empty
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
        // Group rooms by type
        Map<String, List<Map<String, Object>>> roomsByType = new HashMap<>();
        for (Map<String, Object> room : rooms) {
            String type = (String) room.get("room_type");
            roomsByType.computeIfAbsent(type, k -> new ArrayList<>()).add(room);
        }

        // Add VIP rooms
        if (roomsByType.containsKey("VIP")) {
            JLabel vipLabel = new JLabel("VIP Rooms (₦15,000/night)");
            vipLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            vipLabel.setForeground(Color.WHITE);
            vipLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(vipLabel);
            add(Box.createVerticalStrut(10));

            JPanel vipPanel = createRoomGrid(roomsByType.get("VIP"));
            vipPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(vipPanel);
            add(Box.createVerticalStrut(15));
        }

        // Add Regular rooms
        if (roomsByType.containsKey("Regular")) {
            JLabel regLabel = new JLabel("Regular Rooms (₦8,000/night)");
            regLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            regLabel.setForeground(Color.WHITE);
            regLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(regLabel);
            add(Box.createVerticalStrut(10));

            JPanel regPanel = createRoomGrid(roomsByType.get("Regular"));
            regPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(regPanel);
        }

        add(Box.createVerticalGlue());
    }

    private JPanel createRoomGrid(List<Map<String, Object>> roomList) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 4, 10, 10));
        panel.setBackground(new Color(27, 54, 45));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        for (Map<String, Object> room : roomList) {
            JPanel roomCard = createRoomCard(room);
            panel.add(roomCard);
        }

        return panel;
    }

    private JPanel createRoomCard(Map<String, Object> room) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(40, 85, 70));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 218, 195), 3),
                new EmptyBorder(12, 12, 12, 12)
        ));
        card.setMaximumSize(new Dimension(280, 420));

        String roomNumber = (String) room.get("room_number");
        String status = (String) room.get("status");
        double price = (Double) room.get("price");
        String roomType = (String) room.get("room_type");

        // Professional room image (larger)
        JLabel imgLabel = createRoomImageLabel(roomNumber, roomType);
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imgLabel.setMaximumSize(new Dimension(256, 160));
        card.add(imgLabel);
        card.add(Box.createVerticalStrut(10));

        // Room Number - larger & bold
        JLabel roomNumLabel = new JLabel(roomNumber);
        roomNumLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        roomNumLabel.setForeground(new Color(229, 218, 195)); // Gold accent
        roomNumLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(roomNumLabel);
        card.add(Box.createVerticalStrut(8));

        // Amenities
        JLabel amenitiesLabel = createAmenitiesLabel(roomType);
        amenitiesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(amenitiesLabel);
        card.add(Box.createVerticalStrut(8));

        // Status Badge - enhanced
        JLabel statusLabel = new JLabel("  " + status + "  ");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if ("Available".equals(status)) {
            statusLabel.setForeground(Color.WHITE);
            statusLabel.setOpaque(true);
            statusLabel.setBackground(new Color(76, 175, 80)); // Material Green
        } else if ("Booked".equals(status)) {
            statusLabel.setForeground(Color.WHITE);
            statusLabel.setOpaque(true);
            statusLabel.setBackground(new Color(244, 67, 54)); // Material Red
        } else {
            statusLabel.setForeground(Color.WHITE);
            statusLabel.setOpaque(true);
            statusLabel.setBackground(new Color(255, 152, 0)); // Material Orange
        }
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(10));

        // Price - emphasized
        JLabel priceLabel = new JLabel("Ksh " + String.format("%,.0f", price));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        priceLabel.setForeground(new Color(229, 218, 195));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(priceLabel);

        JLabel nightLabel = new JLabel("per night");
        nightLabel.setFont(new Font("SansSerif", Font.ITALIC, 10));
        nightLabel.setForeground(Color.WHITE);
        nightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nightLabel);

        return card;
    }

    private JLabel createAmenitiesLabel(String roomType) {
        String amenities = "VIP".equals(roomType) 
            ? "🛏️ King Bed • 🚿 Luxury Bath • 🍷 Mini Bar"
            : "🛏️ Queen Bed • 🚿 Modern Bath • 📺 Smart TV";
        JLabel label = new JLabel(amenities);
        label.setFont(new Font("SansSerif", Font.PLAIN, 9));
        label.setForeground(new Color(200, 200, 200));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 5));
        panel.setBackground(new Color(34, 72, 56));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Available
        JPanel availableItem = createLegendItem("Available", new Color(100, 200, 100));
        panel.add(availableItem);

        // Booked
        JPanel bookedItem = createLegendItem("Booked", new Color(255, 100, 100));
        panel.add(bookedItem);

        // Not Reserved
        JPanel notReservedItem = createLegendItem("Not Reserved", new Color(200, 200, 100));
        panel.add(notReservedItem);

        return panel;
    }

    private JPanel createLegendItem(String label, Color statusColor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setBackground(new Color(34, 72, 56));

        JLabel colorBox = new JLabel("   ");
        colorBox.setOpaque(true);
        colorBox.setBackground(statusColor);
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        panel.add(colorBox);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        labelText.setForeground(Color.WHITE);
        panel.add(labelText);

        return panel;
    }

    /**
     * Get database connection
     */
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                throw new SQLException("MySQL Driver not found!");
            }
        }
        return java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/melgHotel", "root", "");
    }

    /**
     * Refresh room status (useful for real-time updates)
     */
    public void refreshRoomStatus() {
        removeAll();
        initComponents();
        loadRoomStatus();
        revalidate();
        repaint();
    }

    /**
     * Get list of available rooms
     */
    public List<Map<String, Object>> getAvailableRooms() {
        List<Map<String, Object>> available = new ArrayList<>();
        for (Map<String, Object> room : rooms) {
            if ("Available".equals(room.get("status"))) {
                available.add(room);
            }
        }
        return available;
    }

    /**
     * Get list of booked rooms
     */
    public List<Map<String, Object>> getBookedRooms() {
        List<Map<String, Object>> booked = new ArrayList<>();
        for (Map<String, Object> room : rooms) {
            if ("Booked".equals(room.get("status"))) {
                booked.add(room);
            }
        }
        return booked;
    }

    /**
     * Attempt to load an image for the given room number. If not found, returns
     * a placeholder label with room-specific images based on type.
     */
    private JLabel createRoomImageLabel(String roomNumber, String roomType) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(256, 160));
        label.setOpaque(true);
        label.setBackground(new Color(50, 50, 50));
        try {
            java.net.URL imgUrl = getClass().getResource("/melg/hotel/images/" + roomNumber + ".jpg");
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image scaled = icon.getImage().getScaledInstance(256, 160, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaled));
            } else {
                // Professional placeholder based on room type
                String placeholder = "VIP".equals(roomType)
                    ? "https://images.unsplash.com/photo-1631049307038-da0ec89d4d0a?w=256&h=160&fit=crop"
                    : "https://images.unsplash.com/photo-1578502494516-52d7e1e62842?w=256&h=160&fit=crop";
                
                try {
                    ImageIcon icon = new ImageIcon(new java.net.URL(placeholder));
                    Image scaled = icon.getImage().getScaledInstance(256, 160, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(scaled));
                } catch (Exception e) {
                    label.setText("Room " + roomNumber);
                    label.setForeground(Color.WHITE);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setVerticalAlignment(SwingConstants.CENTER);
                }
            }
        } catch (Exception e) {
            label.setText("Room " + roomNumber);
            label.setForeground(Color.WHITE);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
        }
        return label;
    }
}
