import java.util.Map;

public class RecipeManagement {
    private DatabaseManager dbManager;

    public RecipeManagement(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public Recipe createNewRecipe(Map<String, String> fields) throws IllegalArgumentException, ClassNotFoundException {
        Recipe recipe = new Recipe(
            fields.get("title"),
            fields.get("category"),
            fields.get("instructions"),
            //------------------------
            fields.get("ingredients"),
            fields.get("difficulty"),
            fields.get("time")       
            //---------end-----------
        );
        
        System.out.println("BOOYAKASHA");
        
        if (!recipe.validateRecipeFields()) {
            throw new IllegalArgumentException("Invalid recipe fields");
        }
        
        System.out.println("BOOYAKASHA");

        boolean saved = dbManager.saveRecipe(recipe);
        
        System.out.println("BOOYAKASHA  3");
        if (!saved) {
            throw new RuntimeException("Failed to save recipe");
        }
        return recipe;
    }

    public Recipe editRecipe(int recipeId, Map<String, String> updatedFields) throws ClassNotFoundException {
        Recipe existing = dbManager.getRecipeById(recipeId);
        if (existing == null) throw new IllegalArgumentException("Recipe does not exist");

        if (updatedFields.containsKey("title")) existing.setTitle(updatedFields.get("title"));
        if (updatedFields.containsKey("category")) existing.setCategory(updatedFields.get("category"));
        if (updatedFields.containsKey("instructions")) existing.setInstructions(updatedFields.get("instructions"));

        if (!existing.validateRecipeFields()) {
            throw new IllegalArgumentException("Invalid updated fields");
        }

        boolean updated = dbManager.updateRecipe(existing);
        if (!updated) {
            throw new RuntimeException("Failed to update recipe");
        }
        return existing;
    }

    public boolean deleteRecipe(int recipeId) throws ClassNotFoundException {
        Recipe existing = dbManager.getRecipeById(recipeId);
        if (existing == null) throw new IllegalArgumentException("Recipe does not exist");
        return dbManager.deleteRecipe(recipeId);
    }
}
