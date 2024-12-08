package recipe;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;

import recipe.Login.User;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Profile")
public class Profile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Profile() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("A");
		//establish settings and tools
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
	
		//get username and password
		
		String username = request.getParameter("username");
		
		
		
		//handle login request
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/RecipeApp", "root", "root");
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM users WHERE username = ?");
			stmt.setString(1, username);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	        	out.print(getUserInfo(username, con, rs));
	        }
	        else {
	        	Error err = new Error("invalid user");
				out.print(gson.toJson(err));
	        }
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
	
	//user class to hold user information upon successful login
	class User {
		String username;
		String firstName;
		String lastName;
		ArrayList<Recipe> savedRecipes;
		
		User(String username, String firstName, String lastName, ArrayList<Recipe> savedRecipes) {
			this.username = username;
			this.firstName = firstName;
			this.lastName = lastName;
			this.savedRecipes = savedRecipes;
		}
	}
	
	String getUserInfo(String username, Connection con, ResultSet rs) throws SQLException{
		
		//defining variables
		Gson gson = new Gson();
		ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		ArrayList<Recipe> recipes2 = new ArrayList<Recipe>();
		
		//parsing recipe id list into recipe objects
		try {
			JsonArray recipeIds = gson.fromJson(rs.getString("savedRecipes"), JsonArray.class);
			if (recipeIds != null)
			{
				for (int i = 0; i < recipeIds.size(); i++) {
					PreparedStatement stmt = con.prepareStatement("SELECT r.id, r.title, r.category, u.username FROM recipes r, users u WHERE r.id = ? AND r.author = u.id");
					stmt.setInt(1, recipeIds.get(i).getAsInt());
					ResultSet result = stmt.executeQuery();
					if (result.next()) {
						int id = result.getInt("id");
						String title = result.getString("title");
						String category = result.getString("category");
						String author = result.getString("username");
						recipes.add(new Recipe(id, title, category, "", author));
					}
				}
			}
			
			PreparedStatement stmt = con.prepareStatement("SELECT r.id, r.title, r.category, r.instructions, r.author, u.username FROM recipes r, users u WHERE r.author = ? AND r.author = u.id");

			stmt.setInt(1, rs.getInt("id"));
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				int id = result.getInt("id");
				String title = result.getString("title");
				String category = result.getString("category");
				String author = result.getString("username");
				String instr = result.getString("instructions");
				recipes2.add(new Recipe(id, title, category, instr, author));
			}
			
		} catch (JsonSyntaxException err) {
			System.out.println(err);
		}
		
		//creating user object
		
		ProfileUser user = new ProfileUser(rs.getString("username"), rs.getString("firstName"), rs.getString("lastName"), recipes, recipes2);
		
		//converting object to json and return string
		return gson.toJson(user);
	}


}

class ProfileUser {
	String username;
	String firstName;
	String lastName;
	ArrayList<Recipe> savedRecipes;
	ArrayList<Recipe> uploadedRecipes;
	
	ProfileUser(String username, String firstName, String lastName, ArrayList<Recipe> savedRecipes, ArrayList<Recipe> uploadedRecipes) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.savedRecipes = savedRecipes;
		this.uploadedRecipes = uploadedRecipes;
	}
}