set character_set_server="utf8";
SET NAMES utf8mb4;

create schema if not exists restaurant_remote_reception_test;
use restaurant_remote_reception_test;

create table USERS (
  USER_ID INT NOT NULL AUTO_INCREMENT,
  USER_FULLNAME VARCHAR(50) NOT NULL,
  EMAIL VARCHAR(100) NOT NULL,
 PRIMARY KEY(USER_ID),
 UNIQUE INDEX email_UNIQUE (EMAIL ASC)
) DEFAULT CHARSET=utf8;

create table USER_SECRET (
  USER_ID INT NOT NULL,
  PASSWORD VARCHAR(300) NOT NULL,
 PRIMARY KEY(USER_ID),
FOREIGN KEY(USER_ID) REFERENCES USERS(USER_ID)
) DEFAULT CHARSET=utf8;

create table SHOPPING_CENTERS (
  SHOPPING_CENTER_ID INT NOT NULL AUTO_INCREMENT,
  SHOPPING_CENTER_NAME VARCHAR(100) NOT NULL,
  BRANCH_NAME VARCHAR(100) DEFAULT "",
  PRIMARY KEY(SHOPPING_CENTER_ID),
  UNIQUE shopping_center_branch_UNIQUE (SHOPPING_CENTER_NAME, BRANCH_NAME)
) DEFAULT CHARSET=utf8;

INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("ルミネ", "エスト");
INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("渋谷ヒカリエ", "");
INSERT INTO SHOPPING_CENTERS (SHOPPING_CENTER_NAME, BRANCH_NAME) VALUES ("渋谷マークシティ", "");
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
("味の牛たん喜助", 4, "8F", 57, "牛たん", "03-6915-2611", "11:00-22:30");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("おぼん de ごはん", 4, "8F", 100, "定食&カフェ", "03-5928-0396", "11:00-22:30");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("串揚げ 串亭／炉端 金平", 4, "8F", 69, "串揚げ", "03-5954-8136", "11:00-22:30");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("GOOD MORNING CAFE", 4, "8F", 70, "欧風料理と窯焼ピッツァ&スイーツ", "03-5954-8155", "11:00-22:30");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("こなな", 4, "8F", 68, "和パスタ&カフェ", "03-5944-8803", "11:00-22:30");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("THE KITCHEN 銀座ライオン", 4, "8F", 60, "洋食", "03-5954-8282", "11:00-22:30");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("eat more SOUP&BREAD", 1, "7F", 0, "スープ&ベーカリーカフェ", "03-3356-3606", "11:00～23:00");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("一風堂", 1, "7F", 0, "博多ラーメン", "03-6380-4473", "11:00～23:00");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("ORIENTAL COLORS", 1, "7F", 0, "アジアン・エスニック", "03-6380-1677", "11:00～23:00");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("The Original Pancake House", 1, "7F", 0, "パンケーキ", "03-6457-8768", "11:00～23:00");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("こめらく 贅沢な、お茶漬け日和", 1, "7F", 0, "和食/創作だし茶漬け", "03-5312-8852", "11:00～23:00");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("THE STATION GRIL", 1, "7F", 0, "ハンバーグ・オムライス", "03-5368-2228", "11:00～23:00");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("SESTOSENSO 『Ｈ』", 2, "6F", 72, "インターナショナルイタリアン", "03-6434-1461", "11:00～23:00");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("旨酒・料理 酢重ダイニング", 2, "6F", 120, "和食・酒肴飯", "03-6434-1555", "11:00～23:00");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("横浜中華街 招福門", 2, "6F", 72, "中華ダイニング(広東・香港)", "03-6434-1475", "11:00～23:00");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("CAPRICCI", 2, "6F", 52, "南イタリア料理", "03-6434-1471", "11:00～23:00");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("こめらく　たっぷり野菜とお茶漬けと。", 2, "6F", 36, "創作茶漬け", "03-6433-5666", "11:00～23:00");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("和幸", 3, "4F", 37, "とんかつ", "03-5457-1199", "11:00～23:00");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("ルサンパーム", 3, "ウエストモール 1階", 27, "ファストフレンチ", "03-6416-0417", "10：00～21：00");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("美々卯", 3, "4F", 72, "そば・うどん・うどんすき･和食", "03-5459-2620", "11：00～15：30 17：00～22：30");
INSERT INTO RESTAURANTS
(RESTAURANT_NAME, SHOPPING_CENTER_ID, FLOOR, SEAT_NO, CUISINE, PHONE_NO, OPENING_HOUR)
VALUES
("茶鍋カフェkagurazakasaryo", 3, "ウエストモール 2階", 42, "和カフェ", "03-3496-0990", "10：00～21：00");

create table TICKETS (
  TICKET_ID INT NOT NULL AUTO_INCREMENT,
  TICKET_NO INT NOT NULL,
  RESTAURANT_ID INT NOT NULL,
  CREATED_AT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CREATED_BY_ID INT NOT NULL,
  TICKET_SEAT_NO INT NOT NULL,
  TICKET_TYPE VARCHAR(20) NOT NULL,
  TICKET_STATUS VARCHAR(20) DEFAULT "Active",
  PRIMARY KEY(TICKET_ID),
  FOREIGN KEY(RESTAURANT_ID) REFERENCES RESTAURANTS(RESTAURANT_ID),
  FOREIGN KEY(CREATED_BY_ID) REFERENCES USERS(USER_ID),
  UNIQUE restaurant_user_ticket_status_seat_UNIQUE (RESTAURANT_ID, CREATED_BY_ID, TICKET_STATUS, TICKET_SEAT_NO)
) DEFAULT CHARSET=utf8;
