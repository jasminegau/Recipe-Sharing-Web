package recipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

import com.google.gson.Gson;

/**
 * Servlet implementation class HandleRecipe
 */
@WebServlet("/HandleRecipe")
public class HandleRecipe extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HandleRecipe() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    //get recipe
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//get id to find
		int recipeId = Integer.parseInt(request.getParameter("id"));
		
		//establish settings and tools
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		
		//find recipe
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/RecipeApp", "root", "root");
			PreparedStatement stmt = con.prepareStatement("SELECT r.title, r.category, r.instructions, u.username FROM recipes r, users u WHERE r.id = ? AND r.author = u.id");
			stmt.setInt(1, recipeId);
		    ResultSet rs = stmt.executeQuery();
	       if(rs.next()) {
	    	   String title = rs.getString("title");
	    	   String category = rs.getString("category");
	    	   String instructions = rs.getString("instructions");
	    	   String author = rs.getString("username");
	    	   Recipe r = new Recipe(recipeId, title, category, instructions, author);
	    	   out.print(gson.toJson(r));
	       }
	       else {
	    	   Error err = new Error("recipe does not exist.");
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	//upload recipe
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//establish settings and tools
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder builder = new StringBuilder();
		Gson gson = new Gson();
		
		//read in input
		String line;
		while((line = reader.readLine()) != null) {
			builder.append(line);
		}
		
		//convert to object
		RecipeUpload recipe = gson.fromJson(builder.toString(), RecipeUpload.class);
		
		//upload to database
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/RecipeApp", "root", "root");
			PreparedStatement stmt = con.prepareStatement("INSERT INTO recipes (title, category, instructions, author) VALUES (?, ?, ?, ?)");
			stmt.setString(1, recipe.title);
			stmt.setString(2, recipe.category);
			stmt.setString(3, recipe.instructions);
			stmt.setInt(4, recipe.author);
		    stmt.executeUpdate();
		    out.print("success");
		} catch (SQLException error) {
			System.out.println(error);
			out.print("failed");
		}
		catch (ClassNotFoundException error) {
			System.out.println(error);
			out.print("failed");
		}
		finally {
			out.flush();
		}
	}
	
	class RecipeUpload {
		String title;
		String category;
		String instructions;
		int author;
		
		RecipeUpload(String title, String category, String instructions, int author) {
			this.title = title;
			this.category = category;
			this.instructions = instructions;
			this.author = author;
		}
	}

}
