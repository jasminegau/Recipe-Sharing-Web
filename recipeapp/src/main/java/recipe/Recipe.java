package recipe;

public class AddRecipe{
	private Integer id; 
	private String title; 
	private String category; 
	private String instructions; 
	// --------------------- Umer add ------------
	private String ingredients; 
	private String difficulty;
	private String time; 
	// -------------------------end------------------
	
	public AddRecipe() {}
	
	public AddRecipe(String title, String category, String instructions, String ingredients, String difficulty, String time) {
		this.title = title; 
		this.category = category; 
		this.instructions = instructions; 
		// ---------------------------
		this.ingredients = ingredients; 
		this.difficulty = difficulty; 
		this.time = time; 
		//------------end----------------
	}
	
	public Integer getId() {
		return this.id; 
	}
	
	public void setId(Integer id) {
		this.id = id; 
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title; 
	}
	
	public String getCategory() {
		return this.category; 
	}
	
	public void setCategory(String category) {
		this.category = category; 
	}
	
	
	public String getInstructions() {
		return instructions; 
	}
	
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	
	//----------------Umer Edits---------------------
	public String getIngredients() {
		return this.ingredients; 
	}
	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}
	
	public String getDifficulty() {
		return this.difficulty; 
	}
	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty; 
	}
	
	public String getTime() {
		return this.time;
	}
	public void setTime(String time) {
		this.time = time; 
	}
	
	
	public boolean validateRecipeFields() {
		if (title == null || title.trim().isEmpty()) return false; 
		if (category == null || category.trim().isEmpty()) return false; 
		if (instructions == null || instructions.trim().isEmpty()) return false; 
		if (ingredients == null || ingredients.trim().isEmpty()) return false;
		if (difficulty == null || difficulty.trim().isEmpty()) return false; 
		if (time == null || time.trim().isEmpty()) return false; 
		return true; 
	}
	
	//--------------------end------------------------
	
	
	
//	public boolean validateRecipeFields() {
//		if (title == null || title.trim().isEmpty()) return false; 
//		if (category == null || category.trim().isEmpty()) return false; 
//		if (instructions == null || instructions.trim().isEmpty()) return false; 
//		return true; 
//	}
	
}
