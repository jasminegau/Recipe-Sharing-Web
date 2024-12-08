package recipe;

//recipe object for each individual saved recipe
class Recipe {
	int id;
	String title;
	String category;
	String instructions;
	String author;
	
	Recipe(int id, String title, String category, String instructions, String author) {
		this.id = id;
		this.title = title;
		this.category = category;
		this.instructions = instructions;
		this.author = author;
	}
}

//error class to send error data such as invalid username or SQLException
class Error {
	String error;
	
	Error(String err) {
		this.error = err;
	}
}