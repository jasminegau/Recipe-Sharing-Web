CREATE DATABASE RecipeApp;
USE RecipeApp;
CREATE table users (
	id INT AUTO_INCREMENT PRIMARY KEY,
   username VARCHAR(50) UNIQUE,
   firstName VARCHAR(50),
   lastName VARCHAR(50),
   savedRecipes JSON,
   password VARCHAR(50)
);
CREATE table recipes (
	id INT AUTO_INCREMENT PRIMARY KEY,
   title VARCHAR(100),
   category VARCHAR(100),
   instructions VARCHAR(1000),
   author INT,
   FOREIGN KEY (author) REFERENCES users(id)
);
CREATE TABLE friendships (
   id INT AUTO_INCREMENT PRIMARY KEY,
   user_id INT NOT NULL,
   friend_id INT NOT NULL,
   status ENUM('pending','accepted','rejected') DEFAULT 'pending',
   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
   FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE,
   UNIQUE KEY unique_user_friend (user_id, friend_id)
);
