document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('registerform');
    const errorMessageDiv = document.getElementById('error-message');

    form.addEventListener('submit', handleFormSubmit);

    function handleFormSubmit(event) {
        event.preventDefault();

        const form = document.forms['registerform'];
        const formData = new URLSearchParams(new FormData(form)).toString();

        const xhttp = new XMLHttpRequest();
        xhttp.open('POST', 'Registration', true);
        xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

        xhttp.onload = function () {
            if (xhttp.status === 200) {
                errorMessageDiv.textContent = xhttp.responseText;

                if (xhttp.responseText === 'Success') {
                    errorMessageDiv.style.color = 'green';
                } else {
                    errorMessageDiv.style.color = 'red';
                }
            }

            //show the error message div if theres text
            if (errorMessageDiv.textContent.trim() !== '') {
                errorMessageDiv.style.display = 'block';
            } else {
                errorMessageDiv.style.display = 'none';
            }
        };
		
        xhttp.send(formData);
    }
});
