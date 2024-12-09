/**
 * 
 */
document.getElementById("e").style.visibility ="hidden";


function validate() {
	
    let baseURL = window.location.origin + "/recipeapp/";
    var url = new URL("Login", baseURL);
    var params = {
  	 username: document.form.username.value, password: document.form.password.value}
    url.search = new URLSearchParams(params).toString();
	fetch(url, {
	    method: 'POST',
	    credentials: 'include' // This ensures cookies are sent and received
	})
	.then(data => data.text())
	.then((text) => {
	    const t = JSON.parse(text);
	    
	    if (t.error) {
	        document.getElementById("error").innerHTML = t.error;
	        document.getElementById("e").style.visibility = "visible";
	    } else {
	        sessionStorage.setItem("User", t.username);
	        sessionStorage.setItem("UserId", t.id); // Store userId
	        window.location.assign("index.html");
	    }
	})

	.catch(function (error) {
	    console.log('request failed', error);
	});


   
}