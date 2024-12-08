package recipe;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Registration
 */
@WebServlet("/Registration")
public class Registration extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Registration() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//grab form data
		String username = request.getParameter("username");
		String fname = request.getParameter("fname");
		String lname = request.getParameter("lname");
		String password = request.getParameter("password");
		
		try {
			//connecting to database 
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/RecipeApp", "root", "jg112233");
			
			//check to see if username already exists
            String checkQuery = "SELECT id FROM users WHERE username = ?";
            	PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    // Username exists
                    response.getWriter().print("User already exists");
                    return;
                }
			
			//insert new user
	        String sql = "INSERT INTO users (username, firstName, lastName, password) VALUES (?, ?, ?, ?)";
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        
            stmt.setString(1, username);
            stmt.setString(2, fname);
            stmt.setString(3, lname);
            stmt.setString(4, password);
	        
            int rowsUpdated = stmt.executeUpdate();
            
            if(rowsUpdated > 0) {
            	response.getWriter().print("Success");
            }else {
            	response.getWriter().print("Error in Registration Servlet");
            }
            
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
