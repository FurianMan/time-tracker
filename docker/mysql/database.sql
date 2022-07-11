CREATE DATABASE IF NOT EXISTS time_tracker;
CREATE TABLE IF NOT EXISTS time_tracker.users(
    user_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    surname VARCHAR(30) NOT NULL,
    patronymic VARCHAR(30),
    position VARCHAR(30) NOT NULL,
    birthday DATE NOT NULL
);
CREATE TABLE IF NOT EXISTS time_tracker.tasks(
	task_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	number INT NOT NULL,
	start_time DATETIME NOT NULL,
	end_time DATETIME,
	user_id INT NOT NULL,
	FOREIGN KEY (user_id) REFERENCES time_tracker.users (user_id) ON DELETE CASCADE
);
CREATE USER 'javauser'@'%' IDENTIFIED BY 'javapassword';
GRANT ALL PRIVILEGES ON time_tracker.* TO 'javauser'@'%';