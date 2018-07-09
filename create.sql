set character_set_server="utf8";
SET NAMES utf8mb4;

create schema if not exists restaurant_remote_reception;
use restaurant_remote_reception;

create table USERS (
  USER_ID INT NOT NULL AUTO_INCREMENT,
  USER_FULLNAME VARCHAR(50) NOT NULL,
  EMAIL VARCHAR(100) NOT NULL,
 PRIMARY KEY(USER_ID),
 UNIQUE INDEX email_UNIQUE (EMAIL ASC)
) DEFAULT CHARSET=utf8;

INSERT INTO USERS (USER_FULLNAME,EMAIL) VALUES ("Yuri Tanaka", "yuritanaka@rainbow.com");
INSERT INTO USERS (USER_FULLNAME,EMAIL) VALUES ("Ni Tanaka", "nitanaka@rainbow.com");
INSERT INTO USERS (USER_FULLNAME,EMAIL) VALUES ("San Tanaka", "santanaka@rainbow.com");
INSERT INTO USERS (USER_FULLNAME,EMAIL) VALUES ("Yon Tanaka", "yontanaka@rainbow.com");
INSERT INTO USERS (USER_FULLNAME,EMAIL) VALUES ("Go Tanaka", "gotanaka@rainbow.com");
INSERT INTO USERS (USER_FULLNAME,EMAIL) VALUES ("Roku Tanaka", "rokutanaka@rainbow.com");
INSERT INTO USERS (USER_FULLNAME,EMAIL) VALUES ("Nana Tanaka", "nanatanaka@rainbow.com");
INSERT INTO USERS (USER_FULLNAME,EMAIL) VALUES ("Yako Tanaka", "yakotanaka@rainbow.com");

create table USER_SECRET (
  USER_ID INT NOT NULL,
  PASSWORD VARCHAR(300) NOT NULL,
 PRIMARY KEY(USER_ID),
FOREIGN KEY(USER_ID) REFERENCES USERS(USER_ID)
) DEFAULT CHARSET=utf8;

INSERT INTO USER_SECRET VALUES (1, "abcde");

create table SHOPPING_CENTERS (
  SHOPPING_CENTER_ID INT NOT NULL AUTO_INCREMENT,
  SHOPPING_CENTER_NAME VARCHAR(100) NOT NULL,
  BRANCH_NAME VARCHAR(100) DEFAULT "",
  PRIMARY KEY(SHOPPING_CENTER_ID),
  UNIQUE shopping_center_branch_UNIQUE (SHOPPING_CENTER_NAME, BRANCH_NAME)
) DEFAULT CHARSET=utf8;

INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("ルミネ1", "新宿");
INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("ルミネ2", "新宿");
INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("ルミネ", "エスト");
INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("マルイ", "新宿本館");
INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("渋谷ヒカリエ", "");
INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("渋谷マークシティ", "");
INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("東急百貨店", "渋谷駅・東横店");
INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("伊勢丹", "新宿店");
INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("小田急百貨店", "新宿店");
INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("髙島屋", "新宿店");
INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("マルイ", "渋谷");
INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("マルイ", "池袋");
INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("ルミネ", "池袋");

create table RESTAURANTS (
  RESTAURANT_ID INT NOT NULL AUTO_INCREMENT,
  RESTAURANT_NAME VARCHAR(100) NOT NULL,
  SHOPPING_CENTER_ID INT NOT NULL,
  RESTAURANT_STATUS VARCHAR(20) NOT NULL DEFAULT "Closed",
  FLOOR VARCHAR(10) NOT NULL,
  SEAT_NO INT NOT NULL,
  CUISINE VARCHAR(100) NOT NULL,
  PHONE_NO VARCHAR(20) NOT NULL,
  OPENING_HOUR VARCHAR(100) NOT NULL,
  IMAGE_URL VARCHAR(300),
  PRIMARY KEY(RESTAURANT_ID),
  FOREIGN KEY(SHOPPING_CENTER_ID) REFERENCES SHOPPING_CENTERS(SHOPPING_CENTER_ID),
  UNIQUE (PHONE_NO),
  UNIQUE restaurant_shopping_center_UNIQUE (RESTAURANT_NAME, SHOPPING_CENTER_ID)
) DEFAULT CHARSET=utf8;

INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("味の牛たん喜助", 13, "8F", 57, "牛たん", "03-6915-2611", "11:00-22:30");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("おぼん de ごはん", 13, "8F", 100, "定食&カフェ", "03-5928-0396", "11:00-22:30");

create table TICKETS (
  TICKET_ID INT NOT NULL AUTO_INCREMENT,
  TICKET_NO INT NOT NULL,
  RESTAURANT_ID INT NOT NULL,
  CREATED_AT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CREATED_BY_ID INT NOT NULL,
  TICKET_SEAT_NO INT NOT NULL,
  TICKET_TYPE VARCHAR(20) NOT NULL,
  TICKET_STATUS VARCHAR(20) NOT NULL DEFAULT "Active",
  PRIMARY KEY(TICKET_ID),
  FOREIGN KEY(RESTAURANT_ID) REFERENCES RESTAURANTS(RESTAURANT_ID),
  FOREIGN KEY(CREATED_BY_ID) REFERENCES USERS(USER_ID),
  UNIQUE restaurant_user_ticket_status_UNIQUE (RESTAURANT_ID, CREATED_BY_ID, TICKET_STATUS)
) DEFAULT CHARSET=utf8;

INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(1, 1, 1, 1, 'A');
INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(2, 1, 2, 1, 'A');
INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(3, 1, 3, 1, 'A');
INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(1, 1, 6, 3, 'B');
INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(2, 1, 7, 3, 'B');
INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(3, 1, 8, 3, 'B');
INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(4, 1, 10, 3, 'B');
INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(1, 1, 11, 7, 'C');

INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(1, 2, 1, 1, 'A');
INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(2, 2, 2, 1, 'A');
INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(1, 2, 3, 1, 'B');
INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(2, 2, 6, 3, 'B');
INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(3, 2, 7, 3, 'B');
INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(4, 2, 8, 3, 'B');
INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(1, 2, 10, 6, 'C');
INSERT INTO TICKETS
(TICKET_NO, RESTAURANT_ID, CREATED_BY_ID, TICKET_SEAT_NO, TICKET_TYPE)
VALUES
(2, 2, 11, 7, 'C');
