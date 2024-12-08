const urlParams = new URLSearchParams(window.location.search);

const id = urlParams.get("id");

fetch(`http://localhost:8080/recipeapp/HandleRecipe?&id=${id}`)
	.then(response => response.json())
	.then(data => {
		if (data.error) {
			console.log(data.error);
		}
		else {
			showRecipe(data.title, data.category, data.instructions, data.author);
		}
	})
	.catch(error => console.log(error));
	
	
function showRecipe(title, category, instructions, author) {
	document.getElementById("title").innerHTML=title;
	document.getElementById("category").innerHTML=category;
	document.getElementById("author").innerHTML="by: " + author;
	document.getElementById("instructions").innerHTML=instructions;
};