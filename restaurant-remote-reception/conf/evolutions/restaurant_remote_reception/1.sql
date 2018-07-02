# User schema

# --- !Ups
create table USER (
  USER_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  USER_FULLNAME VARCHAR(50) NOT NULL,
  USER_EMAIL VARCHAR(100) NOT NULL
)

insert into (USER_ID, USER_FULLNAME, USER_EMAIL) VALUES ('0', 'admin', 'wingin.chan@bizreach.co.jp');

# --- !Downs
drop table USER