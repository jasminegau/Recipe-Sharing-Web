import java.io.IOException;

import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

@WebServlet("/RecipeValidation")
public class RecipeValidation extends HttpServlet {
    private static final long serialVersionUID = 1L;
    String recipeName = " "; 
    String cuisine = " "; 
    String ingredients = " "; 
    String instructions = " "; 
    String difficulty = " "; 
    String time = " ";

    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    
    	System.out.println("Got here");
        
        // Retrieve form parameters with null checks
        recipeName = request.getParameter("name");
        cuisine = request.getParameter("cuisine");
        ingredients = request.getParameter("ingredients");
        instructions = request.getParameter("instructions");
        difficulty = request.getParameter("difficulty");
        time = request.getParameter("time");
        
        System.out.println(recipeName + cuisine + ingredients + instructions + difficulty + time);
        	
        System.out.println("got here 1.5");
        
        // Basic validation
        if (recipeName == null || recipeName.trim().isEmpty()) {
        	System.out.println(recipeName);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Recipe name is required");
            return;
        }
        
        
        System.out.println("got here 2");
        //------------new addition -------------
        String dbUrl = "jdbc:mysql://localhost/RecipeApp";
        String dbUser = "root";
        //String dbPassword = "root";
        String dbPassword = "root";
        
        DatabaseManager db = new DatabaseManager(dbUrl, dbUser, dbPassword);
        
        System.out.println("got here 3");
        
        RecipeManagement rm = new RecipeManagement(db); 
        
         
        Map<String, String> recipe = new HashMap<>();
        recipe.put("title", recipeName);
        recipe.put("category", cuisine);
        recipe.put("instructions", instructions);
        recipe.put("ingredients", ingredients);
        recipe.put("difficulty", difficulty);
        recipe.put("time", time);
        
        System.out.println("got here 4");
        try {
			Recipe newRecipe = rm.createNewRecipe(recipe);
		} catch (IllegalArgumentException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("got here 5");
        
        
        //----------------end--------------------
        
        
        

        // Create JSON string (still manual, but with basic escaping)
        String jsonString = String.format(
            "{\"recipe name\": \"%s\"," +
            "\"cuisine\": \"%s\"," +
            "\"ingredients\": \"%s\"," +
            "\"instructions\": \"%s\"," +
            "\"difficulty\": \"%s\"," +
            "\"time\": \"%s\"}",
            escapeJson(recipeName),
            escapeJson(cuisine),
            escapeJson(ingredients),
            escapeJson(instructions),
            escapeJson(difficulty),
            escapeJson(time)
        );

        // Set response properties
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        System.out.println(jsonString);

        // Write response
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonString);
        }
    }

    // Simple JSON escaping method
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}
