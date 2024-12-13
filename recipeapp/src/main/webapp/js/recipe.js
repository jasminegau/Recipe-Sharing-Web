const urlParams = new URLSearchParams(window.location.search);
const id = urlParams.get("id");

const userGreeting = document.getElementById("user-greeting");
const loginLink = document.getElementById("login-link");
const registerLink = document.getElementById("register-link");
const logoutLink = document.getElementById("logout-link");
const friendsLink = document.getElementById("friends-link");
const addRecipe = document.getElementById("add-recipe");
const profile = document.getElementById("profile-link");

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

if ((username = sessionStorage.getItem("User"))) {
	saveRecipeButton = document.getElementById("saveRecipe");
	saveRecipeButton.style.display = "block";
	saveRecipeButton.addEventListener('click', () => {
		fetch(`http://localhost:8080/recipeapp/UserRecipeCollection`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded'
			},
			body: `userId=${sessionStorage.getItem("UserId")}&recipeId=${id}`
		})
		.then(() => {
			document.getElementById("saveRecipeContainer").innerHTML = "Saved!"
		})
		.catch(err => console.log(err));
	});
	// If user is logged in, display username and logout link
   userGreeting.textContent = `Welcome, ${username}`;
   loginLink.style.display = 'none';
   registerLink.style.display = 'none';
   logoutLink.style.display = 'inline';
   friendsLink.style.display = 'inline';
   addRecipe.style.display = 'inline';
   profile.style.display = 'inline';

   // Add functionality to the logout link
   logoutLink.addEventListener('click', () => {
       sessionStorage.removeItem("User");
	   window.location.assign("http://localhost:8080/recipeapp/index.html");

   });
}
else {
	// If no user is logged in, show login and register links
    userGreeting.textContent = '';
    loginLink.style.display = 'inline';
    registerLink.style.display = 'inline';
    logoutLink.style.display = 'none';
    friendsLink.style.display = 'none';
    addRecipe.style.display = 'none';
	profile.style.display = 'none';
}
	
function showRecipe(title, category, instructions, author) {
	document.getElementById("title").innerHTML=title;
	document.getElementById("category").innerHTML=category;
	document.getElementById("author").innerHTML="by: " + author;
	document.getElementById("instructions").innerHTML=instructions;
};