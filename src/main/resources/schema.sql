DROP TABLE IF EXISTS T_TWEETS;

CREATE TABLE T_TWEETS (
  id IDENTITY AUTO_INCREMENT  PRIMARY KEY,
  user_name VARCHAR(250) NOT NULL,
  text VARCHAR(100000) NOT NULL,
  location VARCHAR(250) NOT NULL,
  has_been_validate BOOLEAN DEFAULT NULL
);