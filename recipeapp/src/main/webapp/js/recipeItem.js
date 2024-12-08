//function to generate a recipe card that can be displayed in grid, need to include recipeItem.css in html file

const recipeItem = `
	<div class="recipeItemContainer">
		<div class="upperSection">
			<p1 id="title"></p1>
			<p2 id="category"></p2>
		</div>
		<div class="lowerSection">
			<div class="line"></div>
			<p3 id="user"></p3>
		</div>
	</div>
`;

function getRecipeItem(id, title, category, creator) {
	//creating recipe element
	const container = document.createElement("div");
	container.innerHTML = recipeItem;
	
	//adding info
	container.querySelector("#title").textContent = title;
	container.querySelector("#category").textContent = category;
	container.querySelector("#user").textContent = "By: " + creator;
	
	//adding click event listener
	container.addEventListener("click", () => {
		window.location.href = `recipe.html?&id=${id}`;
	})
	
	return container;
}
