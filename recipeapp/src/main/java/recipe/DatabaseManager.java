import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    public DatabaseManager(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    // Create a new recipe
    public boolean saveRecipe(Recipe recipe) throws ClassNotFoundException {
        System.out.println("issue 1");
        String sql = "INSERT INTO recipes (title, category, instructions, ingredients, difficulty, time) VALUES (?, ?, ?, ?, ?, ?)";
        Class.forName("com.mysql.cj.jdbc.Driver");  // Load MySQL JDBC driver
        System.out.println("issue #2");
        
        try (Connection conn = getConnection()) {
            System.out.println("Able to get connection");

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                System.out.println("issue #3");
                
                // Set parameters
                stmt.setString(1, recipe.getTitle());
                stmt.setString(2, recipe.getCategory());
                stmt.setString(3, recipe.getInstructions());
                System.out.println("issue #4");
                stmt.setString(4, recipe.getIngredients());
                stmt.setString(5, recipe.getDifficulty());
                System.out.println("issue #5");
                stmt.setString(6, recipe.getTime());  // Ensure 'time' is correctly set
                
                // Now execute the statement to insert the data
                int affectedRows = stmt.executeUpdate();  // <--- Missing part
                if (affectedRows > 0) {
                    // Retrieve the generated keys if necessary (e.g., auto-increment ID)
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            recipe.setId(rs.getInt(1));  // Set the generated ID to the recipe
                        }
                    }
                    return true;  // Indicate that the insert was successful
                }

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed at prepareStatement or parameter setting");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed at getConnection()");
        }
        
        return false;  // Return false if the insert was not successful
    }


    // Retrieve a recipe by id
    public Recipe getRecipeById(int id) throws ClassNotFoundException {
        String sql = "SELECT * FROM recipes WHERE id = ?";
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Recipe recipe = new Recipe();
                    recipe.setId(rs.getInt("id"));
                    recipe.setTitle(rs.getString("title"));
                    recipe.setCategory(rs.getString("category"));
                    recipe.setInstructions(rs.getString("instructions"));
                    //|||||||||||||||||||||||Umer Edit ||||||||||||||||||
                    recipe.setIngredients(rs.getString("ingredients"));
                    recipe.setDifficulty(rs.getString("difficulty"));
                    recipe.setTime(rs.getString("time"));
                    
                    
                    //|||||||||||||||||||||||||||||||||||||||||||||||||||
                    return recipe;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update a recipe
    public boolean updateRecipe(Recipe recipe) throws ClassNotFoundException {
        if (recipe.getId() == null) return false;
        //String sql = "UPDATE recipes SET title = ?, category = ?, instructions = ? WHERE id = ?";
        String sql = "UPDATE recipes SET title = ?, category = ?, instructions = ?, ingredients = ?, difficulty = ?, time = ? WHERE id = ?";
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recipe.getTitle());
            stmt.setString(2, recipe.getCategory());
            stmt.setString(3, recipe.getInstructions());
            stmt.setInt(4, recipe.getId());
            //---------------------------UMER EDITS----------------
            stmt.setString(5, recipe.getIngredients());
            stmt.setString(6, recipe.getDifficulty());
            stmt.setString(7, recipe.getTime());
            
            
            
            //-----------------------------------------------------
            
            
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a recipe
    public boolean deleteRecipe(int id) throws ClassNotFoundException {
        String sql = "DELETE FROM recipes WHERE id = ?";
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Retrieve all recipes
    public List<Recipe> getAllRecipes() throws ClassNotFoundException {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT * FROM recipes";
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("id"));
                recipe.setTitle(rs.getString("title"));
                recipe.setCategory(rs.getString("category"));
                recipe.setInstructions(rs.getString("instructions"));
                
                //---------------------------------UMER EDITS----------------------
                
                
                recipe.setIngredients(rs.getString("ingredients"));
                recipe.setDifficulty(rs.getString("difficulty"));
                recipe.setTime(rs.getString("time"));
                       
                
                //------------------------------------------------------------------
                
                
                recipes.add(recipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipes;
    }
}
