package recipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
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
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class UserRecipeCollection
 */
@WebServlet("/UserRecipeCollection")
public class UserRecipeCollection extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserRecipeCollection() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    //retrieve saved recipes
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	//add recipe user saved list
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		
		//getting info
		int userId = Integer.parseInt(request.getParameter("userId"));
		int recipeId = Integer.parseInt(request.getParameter("recipeId"));
		System.out.println(userId);
		
		try {
			Connection con = DBConnection.getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT savedRecipes FROM users WHERE id = ?");
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
		    	  String s = rs.getString("savedRecipes");
		    	  ArrayList<Integer> arr = gson.fromJson(s, new TypeToken<ArrayList<Integer>>(){}.getType());
		    	  arr.add(0, recipeId);
		    	  s = gson.toJson(arr);
		    	  stmt = con.prepareStatement("UPDATE users SET savedRecipes = ? WHERE id = ?");
		    	  stmt.setString(1, s);
		    	  stmt.setInt(2, userId);
		    	  stmt.executeUpdate();
		       }
	       else {
	    	   Error err = new Error("user does not exist.");
	    	   out.print(gson.toJson(err));
	       }
		} catch (SQLException error) {
			System.out.println(error);
			out.print("failed");
		}
		finally {
			out.flush();
		}
	}

}
