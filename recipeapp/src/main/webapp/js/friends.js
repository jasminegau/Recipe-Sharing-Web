document.getElementById("sf").style.visibility = "hidden";

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

document.addEventListener('DOMContentLoaded', () => {
    // Make an AJAX call to the FriendsServlet to get user and friend info
    fetch('FriendsServlet', {
        method: 'GET',
        credentials: 'include' // so that session is maintained
    })
    .then(response => response.json())
    .then(data => {
        // Update the logged-in user's username
        document.getElementById('loggedInUsername').textContent = data.username;

        // Populate friends list
        const friendsList = document.getElementById('friendsList');
        friendsList.innerHTML = ''; // Clear any existing content
        data.friends.forEach(friend => {
            const li = document.createElement('li');
            li.classList.add('friend');
            li.innerHTML = `
                <img src="assets/pfp.jpg" alt="${friend.username}" class="friend-profile-pic">
                <span>${friend.username}</span>
                <div class="friend-actions">
                    <button class="view-profile-btn" onclick="viewProfile('${friend.username}')">View Profile</button>
                    <button class="remove-friend-btn" onclick="removeFriend('${friend.username}')">Remove</button>
                </div>
            `;
            friendsList.appendChild(li);
        });

        // Populate suggested friends list
        const suggestedFriends = document.getElementById('suggestedFriends');
        suggestedFriends.innerHTML = ''; // Clear any existing content
        data.suggested.forEach(s => {
            const li = document.createElement('li');
            li.innerHTML = `
                <span>${s.username}</span>
                <button class="add-friend-btn" onclick="sendFriendRequest('${s.username}')">Request</button>
                <button class="remove-suggested-btn" onclick="removeSuggestedFriend('${s.username}')">Remove</button>
            `;
            suggestedFriends.appendChild(li);
        });

        // Populate pending friend requests list
        const pendingRequests = document.getElementById('pendingRequests');
        pendingRequests.innerHTML = ''; // Clear any existing content
        data.pending.forEach(p => {
            const li = document.createElement('li');
            li.innerHTML = `
                <span>${p.username}</span>
                <button class="cancel-request-btn" onclick="cancelFriendRequest('${p.username}')">Cancel</button>
            `;
            pendingRequests.appendChild(li);
        });

        // Populate mutual friends list
        const mutualFriendsList = document.getElementById('mutualFriendsList');
        mutualFriendsList.innerHTML = ''; // Clear any existing content
        data.mutual.forEach(m => {
            const li = document.createElement('li');
            li.textContent = m.username;
            mutualFriendsList.appendChild(li);
        });
    })
    .catch(error => console.error('Error fetching friends data:', error));
});

// Function to send a friend request
function sendFriendRequest(username) {
    fetch(`FriendsServlet?action=sendRequest&username=${encodeURIComponent(username)}`, {
        method: 'POST',
        credentials: 'include'
    })
    .then(response => {
        if (response.ok) {
            alert(`Friend request sent to ${username}`);
            location.reload();
        } else {
            alert('Failed to send friend request');
        }
    });
}

// Function to cancel a pending friend request
function cancelFriendRequest(username) {
    fetch(`FriendsServlet?action=cancelRequest&username=${encodeURIComponent(username)}`, {
        method: 'POST',
        credentials: 'include'
    })
    .then(response => {
        if (response.ok) {
            alert(`Friend request to ${username} canceled`);
            location.reload();
        } else {
            alert('Failed to cancel friend request');
        }
    });
}

// Function to remove a friend
function removeFriend(username) {
    fetch(`FriendsServlet?action=removeFriend&username=${encodeURIComponent(username)}`, {
        method: 'POST',
        credentials: 'include'
    })
    .then(response => {
        if (response.ok) {
            alert(`${username} removed from friends`);
            location.reload();
        } else {
            alert('Failed to remove friend');
        }
    });
}

// Function to view a friend's profile
function viewProfile(username) {
    alert(`Viewing profile of ${username}`); // You can navigate to a profile page if implemented
}

// Function to remove a suggested friend
function removeSuggestedFriend(username) {
    alert(`Removed ${username} from suggestions`); // Implement any backend logic if needed
}
