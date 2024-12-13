package recipe;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

/**
 * Servlet implementation class FriendsServlet
 */
@WebServlet("/FriendsServlet")
public class FriendsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials - consider moving these to a secure configuration
    private static final String DB_URL = "jdbc:mysql://localhost/RecipeApp";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private Gson gson = new Gson();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FriendsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Handles GET requests to fetch friends data
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set response type to JSON
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get the current session and user ID
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print(gson.toJson(Collections.singletonMap("error", "Unauthorized")));
            out.flush();
            return;
        }

        int userId = (int) session.getAttribute("userId");

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Fetch username
            String username = getUsername(con, userId);

            // Fetch friends
            List<String> friends = getFriends(con, userId);

            // Fetch suggested friends
            List<String> suggested = getSuggestedFriends(con, userId);

            // Fetch pending requests
            List<String> pending = getPendingRequests(con, userId);

            // Fetch mutual friends
            List<String> mutual = getMutualFriends(con, userId);

            // Create response map
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("username", username);
            responseMap.put("friends", friends);
            responseMap.put("suggested", suggested);
            responseMap.put("pending", pending);
            responseMap.put("mutual", mutual);

            // Convert to JSON and send
            String jsonResponse = gson.toJson(responseMap);
            out.print(jsonResponse);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Collections.singletonMap("error", e.getMessage())));
        } finally {
            out.flush();
        }
    }

    /**
     * Handles POST requests to perform friend-related actions
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set response type to JSON
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get the current session and user ID
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print(gson.toJson(Collections.singletonMap("error", "Unauthorized")));
            out.flush();
            return;
        }

        int userId = (int) session.getAttribute("userId");

        // Get the action parameter
        String action = request.getParameter("action");
        String targetUsername = request.getParameter("username");

        if (action == null || targetUsername == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Collections.singletonMap("error", "Missing parameters")));
            out.flush();
            return;
        }

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Get target user ID
            Integer targetUserId = getUserId(con, targetUsername);
            if (targetUserId == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(Collections.singletonMap("error", "User does not exist")));
                out.flush();
                return;
            }

            switch (action) {
                case "sendRequest":
                    sendFriendRequest(con, userId, targetUserId);
                    out.print(gson.toJson(Collections.singletonMap("message", "Friend request sent")));
                    break;

                case "cancelRequest":
                    cancelFriendRequest(con, userId, targetUserId);
                    out.print(gson.toJson(Collections.singletonMap("message", "Friend request canceled")));
                    break;

                case "acceptRequest":
                    acceptFriendRequest(con, userId, targetUserId);
                    out.print(gson.toJson(Collections.singletonMap("message", "Friend request accepted")));
                    break;

                case "removeFriend":
                    removeFriend(con, userId, targetUserId);
                    out.print(gson.toJson(Collections.singletonMap("message", "Friend removed")));
                    break;

                case "removeSuggested":
                    removeSuggestedFriend(con, userId, targetUserId);
                    out.print(gson.toJson(Collections.singletonMap("message", "Suggested friend removed")));
                    break;

                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gson.toJson(Collections.singletonMap("error", "Invalid action")));
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Collections.singletonMap("error", e.getMessage())));
        } finally {
            out.flush();
        }
    }
    
    private void acceptFriendRequest(Connection con, int userId, int targetUserId) throws SQLException {
        // Check if there is a pending friend request between the users
        String checkRequestSQL = "SELECT * FROM FriendRequests WHERE sender_id = ? AND receiver_id = ? AND status = 'pending'";
        try (PreparedStatement stmt = con.prepareStatement(checkRequestSQL)) {
            stmt.setInt(1, targetUserId); // target is the sender
            stmt.setInt(2, userId); // user is the receiver

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new SQLException("No pending friend request found between users.");
            }
        }

        // Update the status of the request to 'accepted'
        String updateRequestSQL = "UPDATE FriendRequests SET status = 'accepted' WHERE sender_id = ? AND receiver_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(updateRequestSQL)) {
            stmt.setInt(1, targetUserId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }

        // Add both users as friends (insert into a 'friends' table)
        String insertFriendSQL = "INSERT INTO Friends (user_id, friend_id) VALUES (?, ?), (?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(insertFriendSQL)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, targetUserId);
            stmt.setInt(3, targetUserId);
            stmt.setInt(4, userId);
            stmt.executeUpdate();
        }
    }


    /**
     * Retrieves the username for a given user ID
     */
    private String getUsername(Connection con, int userId) throws SQLException {
        String query = "SELECT username FROM users WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the user ID for a given username
     */
    private Integer getUserId(Connection con, String username) throws SQLException {
        String query = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the list of friends for the given user ID
     */
    private List<String> getFriends(Connection con, int userId) throws SQLException {
        String query = "SELECT u.username FROM friendships f JOIN users u ON (f.friend_id = u.id) " +
                       "WHERE f.user_id = ? AND f.status = 'accepted'";
        List<String> friends = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    friends.add(rs.getString("username"));
                }
            }
        }
        return friends;
    }

    /**
     * Retrieves the list of suggested friends for the given user ID
     * Suggestion logic can vary; here we suggest users who are not already friends and have no pending requests
     */
    private List<String> getSuggestedFriends(Connection con, int userId) throws SQLException {
        String query = "SELECT u.username FROM users u WHERE u.id != ? " +
                       "AND u.id NOT IN (SELECT friend_id FROM friendships WHERE user_id = ?) " +
                       "AND u.id NOT IN (SELECT user_id FROM friendships WHERE friend_id = ? AND status = 'pending') " +
                       "LIMIT 10"; // Limit to 10 suggestions
        List<String> suggested = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    suggested.add(rs.getString("username"));
                }
            }
        }
        return suggested;
    }

    /**
     * Retrieves the list of pending friend requests for the given user ID
     * These are incoming requests
     */
    private List<String> getPendingRequests(Connection con, int userId) throws SQLException {
        String query = "SELECT u.username FROM friendships f JOIN users u ON (f.user_id = u.id) " +
                       "WHERE f.friend_id = ? AND f.status = 'pending'";
        List<String> pending = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pending.add(rs.getString("username"));
                }
            }
        }
        return pending;
    }

    /**
     * Retrieves the list of mutual friends for the given user ID
     * Mutual friends are friends that the user shares with their friends
     * This is a simplified implementation
     */
    private List<String> getMutualFriends(Connection con, int userId) throws SQLException {
        String query = "SELECT u.username FROM friendships f1 " +
                       "JOIN friendships f2 ON f1.friend_id = f2.friend_id " +
                       "JOIN users u ON f1.friend_id = u.id " +
                       "WHERE f1.user_id = ? AND f2.user_id != ? AND f1.status = 'accepted' AND f2.status = 'accepted'";
        List<String> mutual = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mutual.add(rs.getString("username"));
                }
            }
        }
        return mutual;
    }

    /**
     * Sends a friend request from userId to targetUserId
     */
    private void sendFriendRequest(Connection con, int userId, int targetUserId) throws SQLException {
        // Check if a friendship already exists
        String checkQuery = "SELECT status FROM friendships WHERE user_id = ? AND friend_id = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, targetUserId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    if ("pending".equals(status)) {
                        throw new SQLException("Friend request already sent");
                    } else if ("accepted".equals(status)) {
                        throw new SQLException("Already friends");
                    } else if ("rejected".equals(status)) {
                        // Optionally allow re-sending after rejection
                        // For now, we'll allow it
                    }
                }
            }
        }

        // Insert a new friendship with status 'pending'
        String insertQuery = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, 'accepted')";
        try (PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, targetUserId);
            insertStmt.executeUpdate();
        }
    }

    /**
     * Cancels a friend request from userId to targetUserId
     */
    private void cancelFriendRequest(Connection con, int userId, int targetUserId) throws SQLException {
        String deleteQuery = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ? AND status = 'pending'";
        try (PreparedStatement deleteStmt = con.prepareStatement(deleteQuery)) {
            deleteStmt.setInt(1, userId);
            deleteStmt.setInt(2, targetUserId);
            int affectedRows = deleteStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No pending friend request to cancel");
            }
        }
    }

    /**
     * Removes a friend relationship between userId and targetUserId
     */
    private void removeFriend(Connection con, int userId, int targetUserId) throws SQLException {
        String deleteQuery = "DELETE FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        try (PreparedStatement deleteStmt = con.prepareStatement(deleteQuery)) {
            deleteStmt.setInt(1, userId);
            deleteStmt.setInt(2, targetUserId);
            deleteStmt.setInt(3, targetUserId);
            deleteStmt.setInt(4, userId);
            int affectedRows = deleteStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Friend relationship does not exist");
            }
        }
    }

    /**
     * Removes a suggested friend (implementation depends on how suggestions are managed)
     * For this example, we'll assume we add them to a 'blocked_suggestions' table to prevent future suggestions
     */
    private void removeSuggestedFriend(Connection con, int userId, int targetUserId) throws SQLException {
        // Example implementation: Insert into a blocked_suggestions table
        String insertQuery = "INSERT INTO blocked_suggestions (user_id, blocked_user_id) VALUES (?, ?)";
        try (PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, targetUserId);
            insertStmt.executeUpdate();
        }
    }

    /**
     * Initialize the servlet and ensure necessary tables exist
     */
    public void init() throws ServletException {
        super.init();
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = con.createStatement()) {

            // Create blocked_suggestions table if it doesn't exist
            String createBlockedSuggestions = "CREATE TABLE IF NOT EXISTS blocked_suggestions (" +
                                             "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                             "user_id INT NOT NULL, " +
                                             "blocked_user_id INT NOT NULL, " +
                                             "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                                             "FOREIGN KEY (blocked_user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                                             "UNIQUE KEY unique_user_block (user_id, blocked_user_id))";
            stmt.execute(createBlockedSuggestions);
        } catch (SQLException e) {
            throw new ServletException("Failed to initialize FriendsServlet", e);
        }
    }
}
