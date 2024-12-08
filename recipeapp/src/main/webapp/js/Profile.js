/**
 * 
 */

init();


function init()
{
	document.getElementById("u").innerHTML = "i";
	
	const urlParams = new URLSearchParams(window.location.search);
	user = urlParams.get("user");
	
	
	
	if (!user)
	{
		user = sessionStorage.getItem("User");
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
				document.getElementById("u").innerHTML = sessionStorage.getItem("User");
			}
			else{
				document.getElementById("u").innerHTML = t.username;
				document.getElementById("f").innerHTML = t.firstName + " " + t.lastName;
				
				document.getElementById("sr").innerHTML = t.savedRecipes[0];
				
				
				

				
				
				
    		}
  	 }).catch(function (error) {
    		console.log('request failed', error)
			
  	 });
}
