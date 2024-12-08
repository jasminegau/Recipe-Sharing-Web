package recipe;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

/**
 * Servlet implementation class ShowAllRecipe
 */
@WebServlet("/ShowAllRecipe")
public class ShowAllRecipe extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowAllRecipe() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//establish settings and tools
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		ArrayList<Recipe> results = new ArrayList<Recipe>();
		
		//connecting to database and adding every recipe into a lucene document to index
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/RecipeApp", "root", "root");
			PreparedStatement stmt = con.prepareStatement("SELECT r.id, r.title, r.category, u.username FROM recipes r, users u WHERE r.author = u.id");
	        ResultSet rs = stmt.executeQuery();
	       while(rs.next()) {
	    	   	results.add(new Recipe(rs.getInt("id"), rs.getString("title"), rs.getString("category"), "", rs.getString("username")));
	       }
	       out.print(gson.toJson(results));
		} catch (SQLException error) {
			System.out.println(error);
			Error err = new Error(error.getMessage());
			out.print(gson.toJson(err));
		}
		catch (ClassNotFoundException error) {
			System.out.println(error);
			Error err = new Error(error.getMessage());
			out.print(gson.toJson(err));
		}
		finally {
			out.flush();
		}
	}
}
