/**
 * 
 */

init();

function friend()
{
	document.getElementById("add").style.visibility = "hidden";	
	let baseURL = window.location.origin + "/recipeapp/";
	const urlParams = new URLSearchParams(window.location.search);
	user = urlParams.get("user");
	var url = new URL("Profile", baseURL);
	var params = {
	username: user}
	url.search = new URLSearchParams(params).toString();
		
	fetch(url, {
		   method: 'POST'
	})
		
				
	     .then(data => data.text())
	   	.then((text) => {
		const t = JSON.parse(text);
		}).catch(function (error) {
		    		console.log('request failed', error)
					
		  	 });
	
}
function rc(n)
{
	let baseURL = window.location.origin + "/recipeapp/";
	var url = new URL("recipe.html?&id=" + n, baseURL); 
	window.open(url);
}

function init()
{
	document.getElementById("add").style.visibility = "hidden";
	document.getElementById("add").addEventListener("click", friend);

	document.getElementById("u").innerHTML = "i";
	
	const urlParams = new URLSearchParams(window.location.search);
	user = urlParams.get("user");
	
	
	
	if (!user && sessionStorage.getItem("User"))
	{
		user = sessionStorage.getItem("User");
		document.getElementById("add").style.visibility = "hidden";
	}
	
	else if (user && sessionStorage.getItem("User") && user !== sessionStorage.getItem("User"))
	{
		document.getElementById("add").style.visibility = "visible";
		
	}
	else if (!user)
	{
		document.getElementById("bg").innerHTML = "not logged in";
	}
	getUser(user);
}



function getUser(u) {
	
    let baseURL = window.location.origin + "/recipeapp/";
    var url = new URL("Profile", baseURL);
    var params = {
  	 username: u}
    url.search = new URLSearchParams(params).toString();
	fetch(url, {
	        method: 'GET'
	    })
	
			
       .then(data => data.text())
    	 .then((text) => {
			const t = JSON.parse(text);
			
			if (t.error)
			{
				document.getElementById("u").innerHTML = "User not found";
				document.getElementById("add").style.visibility = "hidden";
			}
			else{
				document.getElementById("u").innerHTML = t.username;
				document.getElementById("f").innerHTML = t.firstName + " " + t.lastName;
				
				var table = document.getElementById("sr");
				for (let i = 0; i < t.savedRecipes.length; i++)
				{
					var row = table.insertRow(0);
					var cell1 = row.insertCell(0);
					var cell2 = row.insertCell(1);
					var cell3 = row.insertCell(2);
					cell1.innerHTML = t.savedRecipes[i].title;
					cell2.innerHTML = t.savedRecipes[i].author;
					cell3.innerHTML = t.savedRecipes[i].category;
					row.addEventListener('click', function() {
						 rc(t.savedRecipes[i].id);
					}); 
				}
				var row = table.insertRow(0);
				var cell1 = row.insertCell(0);
				var cell2 = row.insertCell(1);
				var cell3 = row.insertCell(2);
				cell1.innerHTML = "Name";
				cell2.innerHTML = "Author";
				cell3.innerHTML = "Category";
				
				var table2 = document.getElementById("yr");
				for (let i = 0; i < t.uploadedRecipes.length; i++)
				{
					var row = table2.insertRow(0);
					var cell1 = row.insertCell(0);
					var cell2 = row.insertCell(1);
					cell1.innerHTML = t.uploadedRecipes[i].title;
					cell2.innerHTML = t.uploadedRecipes[i].category;
					row.addEventListener('click', function() {
					   rc(t.uploadedRecipes[i].id);
					}); 
				}
				
				var row2 = table2.insertRow(0);
				var cell3 = row2.insertCell(0);
				var cell4 = row2.insertCell(1);
				cell3.innerHTML = "Name";
				cell4.innerHTML = "Category";
				
				if(sessionStorage.getItem("User") && (t.friends.includes(sessionStorage.getItem("User")) || t.friends.includes(t.username)))
				{
					
					document.getElementById("add").style.visibility = "hidden";	
				}
				
				
				
    		}
  	 }).catch(function (error) {
    		console.log('request failed', error)
			
  	 });
}
