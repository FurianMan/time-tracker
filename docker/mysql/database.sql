CREATE DATABASE IF NOT EXISTS time_tracker;
CREATE TABLE IF NOT EXISTS time_tracker.users(
    user_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    surname VARCHAR(30) NOT NULL,
    patronymic VARCHAR(30),
    position VARCHAR(30) NOT NULL,
    birthday DATE NOT NULL,
    date_creating DATETIME,
    UNIQUE (name,surname,position,birthday)
);
CREATE TABLE IF NOT EXISTS time_tracker.tasks(
	task_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	task_num INT NOT NULL,
	start_time DATETIME,
	end_time DATETIME,
	user_id INT NOT NULL,
    UNIQUE (task_num,start_time,user_id),
	FOREIGN KEY (user_id) REFERENCES time_tracker.users (user_id) ON DELETE CASCADE
);
USE time_tracker;
DELIMITER //
CREATE TRIGGER insert_date_into_user
BEFORE INSERT
ON users
FOR EACH ROW
SET NEW.date_creating=NOW();//
DELIMITER ;
CREATE USER 'javauser'@'%' IDENTIFIED BY 'javapassword';
GRANT ALL PRIVILEGES ON time_tracker.* TO 'javauser'@'%';