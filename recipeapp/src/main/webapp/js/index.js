loggedIn();
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

function loggedIn()
{
	if (!sessionStorage.getItem("User"))
	{
		document.getElementById("login").innerHTML = "Login";
		document.getElementById("register").innerHTML = "Register";
		document.getElementById("register").href = "registration.html";
		document.getElementById("login").href = "Login.html";
		
	}
	else
	{
		document.getElementById("login").innerHTML = "Profile";
		document.getElementById("register").innerHTML = "Sign Out";
		document.getElementById("login").href = "Profile.html";
		
		row.addEventListener('click', function() {
			sessionStorage.setItem("User", null);
			loggedIn();
		}); 
		document.getElementById("register").href = "javascript:void(0)";
	}
}
