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