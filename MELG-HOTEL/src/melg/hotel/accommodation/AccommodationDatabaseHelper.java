package melg.hotel.accommodation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database helper class for accommodation bookings
 * Handles all database operations related to room bookings
 */
public class AccommodationDatabaseHelper {

    /**
     * Save a new booking to the database
     *
     * @param guestName Guest's full name
     * @param guestContact Guest's contact number
     * @param gender Guest's gender
     * @param peopleCount Number of people
     * @param checkInDate Check-in date (yyyy-MM-dd format)
     * @param checkOutDate Check-out date (yyyy-MM-dd format)
     * @param roomType Room type (VIP or Regular)
     * @param paymentMethod Payment method (mPesa, Debit Card, Credit Card)
     * @param totalAmount Total booking amount
     * @return true if booking saved successfully, false otherwise
     */
    public static boolean saveBooking(String guestName, String guestContact, String gender,
            int peopleCount, String checkInDate, String checkOutDate, String roomType,
            String paymentMethod, double totalAmount) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();

            // First, get or create room_id based on room type
            int roomId = getRoomIdByType(conn, roomType);
            if (roomId == -1) {
                // Create a new room if it doesn't exist
                roomId = createRoom(conn, roomType);
            }

            // Insert booking
            String sql = "INSERT INTO hotel_bookings " +
                    "(guest_name, guest_contact, room_id, check_in_date, check_out_date, total_amount, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, guestName);
            pstmt.setString(2, guestContact);
            pstmt.setInt(3, roomId);
            pstmt.setDate(4, Date.valueOf(checkInDate));
            pstmt.setDate(5, Date.valueOf(checkOutDate));
            pstmt.setDouble(6, totalAmount);
            pstmt.setString(7, "Booked");

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get room ID by room type
     */
    private static int getRoomIdByType(Connection conn, String roomType) {
        String sql = "SELECT id FROM hotel_rooms WHERE room_type = ? LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomType);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Create a new room in the database
     */
    private static int createRoom(Connection conn, String roomType) {
        String sql = "INSERT INTO hotel_rooms (room_number, room_type, price, status) VALUES (?, ?, ?, ?)";
        double price = roomType.equals("VIP") ? 15000 : 8000;

        try (PreparedStatement pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, "ROOM-" + System.currentTimeMillis());
            pstmt.setString(2, roomType);
            pstmt.setDouble(3, price);
            pstmt.setString(4, "Available");

            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get all bookings for a guest
     */
    public static List<Map<String, String>> getGuestBookings(String guestName) {
        List<Map<String, String>> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM hotel_bookings WHERE guest_name = ? ORDER BY check_in_date DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, guestName);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, String> booking = new HashMap<>();
                booking.put("id", String.valueOf(rs.getInt("id")));
                booking.put("guest_name", rs.getString("guest_name"));
                booking.put("check_in_date", rs.getDate("check_in_date").toString());
                booking.put("check_out_date", rs.getDate("check_out_date").toString());
                booking.put("total_amount", String.valueOf(rs.getDouble("total_amount")));
                booking.put("status", rs.getString("status"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bookings;
    }

    /**
     * Cancel a booking
     */
    public static boolean cancelBooking(int bookingId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "UPDATE hotel_bookings SET status = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "Cancelled");
            pstmt.setInt(2, bookingId);

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get database connection
     * Uses the same connection settings as DatabaseHelper in MELGHOTEL.java
     */
    private static Connection getConnection() throws SQLException {
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
     * Check room availability for a date range
     */
    public static boolean isRoomAvailable(String roomType, String checkInDate, String checkOutDate) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) as count FROM hotel_bookings hb " +
                    "INNER JOIN hotel_rooms hr ON hb.room_id = hr.id " +
                    "WHERE hr.room_type = ? AND hb.status = 'Booked' " +
                    "AND ((hb.check_in_date <= ? AND hb.check_out_date > ?) " +
                    "OR (hb.check_in_date < ? AND hb.check_out_date >= ?) " +
                    "OR (hb.check_in_date >= ? AND hb.check_out_date <= ?))";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, roomType);
            pstmt.setDate(2, Date.valueOf(checkOutDate));
            pstmt.setDate(3, Date.valueOf(checkInDate));
            pstmt.setDate(4, Date.valueOf(checkOutDate));
            pstmt.setDate(5, Date.valueOf(checkInDate));
            pstmt.setDate(6, Date.valueOf(checkInDate));
            pstmt.setDate(7, Date.valueOf(checkOutDate));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") == 0; // Available if no conflicting bookings
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
