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
     * Save a new booking
     */
    public static boolean saveBooking(String guestName, String guestContact, String gender,
                                      int peopleCount, String checkInDate, String checkOutDate,
                                      String roomType, String paymentMethod, double totalAmount) {

        Connection conn = null;

        try {

            conn = getConnection();

            // Check room availability first
            if (!isRoomAvailable(roomType, checkInDate, checkOutDate)) {
                System.out.println("Room not available for selected dates.");
                return false;
            }

            // Get room ID
            int roomId = getRoomIdByType(conn, roomType);

            if (roomId == -1) {
                roomId = createRoom(conn, roomType);
            }

            String sql = "INSERT INTO hotel_bookings "
                    + "(guest_name, guest_contact, gender, people_count, room_id, "
                    + "check_in_date, check_out_date, payment_method, total_amount, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, guestName);
            pstmt.setString(2, guestContact);
            pstmt.setString(3, gender);
            pstmt.setInt(4, peopleCount);
            pstmt.setInt(5, roomId);
            pstmt.setDate(6, Date.valueOf(checkInDate));
            pstmt.setDate(7, Date.valueOf(checkOutDate));
            pstmt.setString(8, paymentMethod);
            pstmt.setDouble(9, totalAmount);
            pstmt.setString(10, "Booked");

            int result = pstmt.executeUpdate();

            // Update room status
            if (result > 0) {

                String updateRoom = "UPDATE hotel_rooms SET status='Occupied' WHERE id=?";
                PreparedStatement updateStmt = conn.prepareStatement(updateRoom);

                updateStmt.setInt(1, roomId);
                updateStmt.executeUpdate();

                updateStmt.close();
            }

            pstmt.close();

            return result > 0;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        } finally {

            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Get room ID by type
     */
    private static int getRoomIdByType(Connection conn, String roomType) {

        String sql = "SELECT id FROM hotel_rooms WHERE room_type = ? LIMIT 1";

        try {

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, roomType);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                int id = rs.getInt("id");

                rs.close();
                pstmt.close();

                return id;
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return -1;

    }

    /**
     * Create new room automatically
     */
    private static int createRoom(Connection conn, String roomType) {

        String sql = "INSERT INTO hotel_rooms (room_number, room_type, price, status) VALUES (?, ?, ?, ?)";

        double price;

        if (roomType.equalsIgnoreCase("VIP")) {
            price = 15000;
        } else {
            price = 8000;
        }

        try {

            PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            String roomNumber = "RM-" + (int) (Math.random() * 1000);

            pstmt.setString(1, roomNumber);
            pstmt.setString(2, roomType);
            pstmt.setDouble(3, price);
            pstmt.setString(4, "Available");

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();

            if (rs.next()) {

                int id = rs.getInt(1);

                rs.close();
                pstmt.close();

                return id;
            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return -1;

    }

    /**
     * Get bookings for a guest
     */
    public static List<Map<String, String>> getGuestBookings(String guestName) {

        List<Map<String, String>> bookings = new ArrayList<>();

        try {

            Connection conn = getConnection();

            String sql = "SELECT * FROM hotel_bookings WHERE guest_name=? ORDER BY check_in_date DESC";

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, guestName);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                Map<String, String> booking = new HashMap<>();

                booking.put("id", String.valueOf(rs.getInt("id")));
                booking.put("guest_name", rs.getString("guest_name"));
                booking.put("check_in_date", rs.getDate("check_in_date").toString());
                booking.put("check_out_date", rs.getDate("check_out_date").toString());
                booking.put("payment_method", rs.getString("payment_method"));
                booking.put("total_amount", String.valueOf(rs.getDouble("total_amount")));
                booking.put("status", rs.getString("status"));

                bookings.add(booking);

            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return bookings;

    }

    /**
     * Cancel booking
     */
    public static boolean cancelBooking(int bookingId) {

        try {

            Connection conn = getConnection();

            String sql = "UPDATE hotel_bookings SET status='Cancelled' WHERE id=?";

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, bookingId);

            int result = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            return result > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;

        }

    }

    /**
     * Check room availability
     */
    public static boolean isRoomAvailable(String roomType, String checkInDate, String checkOutDate) {

        try {

            Connection conn = getConnection();

            String sql = "SELECT COUNT(*) as count "
                    + "FROM hotel_bookings hb "
                    + "JOIN hotel_rooms hr ON hb.room_id = hr.id "
                    + "WHERE hr.room_type=? AND hb.status='Booked' "
                    + "AND ((hb.check_in_date <= ? AND hb.check_out_date > ?) "
                    + "OR (hb.check_in_date < ? AND hb.check_out_date >= ?) "
                    + "OR (hb.check_in_date >= ? AND hb.check_out_date <= ?))";

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, roomType);

            pstmt.setDate(2, Date.valueOf(checkOutDate));
            pstmt.setDate(3, Date.valueOf(checkInDate));

            pstmt.setDate(4, Date.valueOf(checkOutDate));
            pstmt.setDate(5, Date.valueOf(checkInDate));

            pstmt.setDate(6, Date.valueOf(checkInDate));
            pstmt.setDate(7, Date.valueOf(checkOutDate));

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                int count = rs.getInt("count");

                rs.close();
                pstmt.close();
                conn.close();

                return count == 0;
            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    /**
     * Database connection
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
                "jdbc:mysql://localhost:3306/melgHotel",
                "root",
                "");

    }

}