
fetch(`http://localhost:8080/recipeapp/ShowAllRecipe`)
		.then(response => response.json())
		.then(data => {
			document.getElementById("recipes").innerHTML = "";
			for (let recipe of data) {
				document.getElementById("recipes").appendChild(getRecipeItem(recipe.id, recipe.title, recipe.category, recipe.author));
			}
		})
		.catch(error => {
			console.log(error);
		})

function updateSearch(event) {
	event.preventDefault();
	console.log(document.getElementById("search").value);
	const query = document.getElementById("search").value;
	fetch(`http://localhost:8080/recipeapp/Search?query=${query}`)
		.then(response => response.json())
		.then(data => {
			document.getElementById("recipes").innerHTML = "";
			for (let recipe of data) {
				document.getElementById("recipes").appendChild(getRecipeItem(recipe.id, recipe.title, recipe.category, recipe.author));
			}
		})
		.catch(error => {
			console.log(error);
		})
}