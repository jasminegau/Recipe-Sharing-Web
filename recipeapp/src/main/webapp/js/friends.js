// Search Friends Functionality
function searchFriends() {
    const searchBar = document.getElementById('searchBar').value.toLowerCase();
    const friendsList = document.getElementById('friendsList').getElementsByClassName('friend');

    for (let i = 0; i < friendsList.length; i++) {
        const friendName = friendsList[i].querySelector('span').textContent.toLowerCase();
        if (friendName.includes(searchBar)) {
            friendsList[i].style.display = 'flex';
        } else {
            friendsList[i].style.display = 'none';
        }
    }
}

// Add to Pending Requests
function addToPending(friendName) {
    const suggestedFriendsList = document.getElementById('suggestedFriends');
    const pendingRequestsList = document.getElementById('pendingRequests');

    // Find the friend in Suggested Friends
    const friends = suggestedFriendsList.getElementsByTagName('li');
    for (let i = 0; i < friends.length; i++) {
        if (friends[i].textContent.includes(friendName)) {
            // Move the friend to Pending Requests
            const newFriend = friends[i].cloneNode(true);
            const addButton = newFriend.querySelector('button');
            addButton.remove(); // Remove the Request button
            
            // Create a Cancel button for the Pending Request
            const cancelButton = document.createElement('button');
            cancelButton.textContent = "Cancel";
            cancelButton.classList.add('cancel-request-btn');
            cancelButton.onclick = function() {
                cancelRequest(newFriend);
            };
            newFriend.appendChild(cancelButton);

            pendingRequestsList.appendChild(newFriend);
            // Remove the friend from Suggested Friends
            suggestedFriendsList.removeChild(friends[i]);
            break;
        }
    }
}

// Cancel Pending Request
function cancelRequest(friendElement) {
    const pendingRequestsList = document.getElementById('pendingRequests');
    pendingRequestsList.removeChild(friendElement);
}

// Remove Suggested Friend
function removeSuggestedFriend(friendName) {
    const suggestedFriendsList = document.getElementById('suggestedFriends');
    const friends = suggestedFriendsList.getElementsByTagName('li');
    for (let i = 0; i < friends.length; i++) {
        if (friends[i].textContent.includes(friendName)) {
            // Remove the friend from the list
            suggestedFriendsList.removeChild(friends[i]);
            break;
        }
    }
}

// Remove Friend from "Your Friends" List
function removeFriend(event) {
    const friendItem = event.target.closest('.friend'); // Find the friend item
    const friendsList = document.getElementById('friendsList');
    friendsList.removeChild(friendItem); // Remove the friend from the list
}

// Attach remove functionality to all "Remove" buttons on page load
window.onload = function() {
    const removeButtons = document.querySelectorAll('.remove-friend-btn');
    removeButtons.forEach(button => {
        button.addEventListener('click', removeFriend); // Attach event listener
    });

    const removeSuggestedButtons = document.querySelectorAll('.remove-suggested-btn');
    removeSuggestedButtons.forEach(button => {
        button.addEventListener('click', function() {
            const friendName = button.closest('li').textContent.trim();
            removeSuggestedFriend(friendName); // Call removeSuggestedFriend when Remove button is clicked
        });
    });
}
