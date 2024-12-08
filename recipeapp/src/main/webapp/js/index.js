
for (let i = 0; i < 10; i++) {
	document.getElementById("recipes").appendChild(getRecipeItem("test", "yummy", "test user"));
}

function updateSearch(event) {
	event.preventDefault();
	console.log(document.getElementById("search").value);
	const query = document.getElementById("search").value;
	fetch(`http://localhost:8080/recipeapp/Search?query=${query}`)
		.then(response => response.json())
		.then(data => {
			console.log(data);
		})
		.catch(error => {
			console.log(error);
		})
}