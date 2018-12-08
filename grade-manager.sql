CREATE DATABASE IF NOT EXISTS grade-manager;
USE grade-manager;

CREATE TABLE donor (
  donor_id INTEGER PRIMARY KEY AUTO_INCREMENT,
  donor_name VARCHAR(500) NOT NULL,
  donor_email VARCHAR(200) NOT NULL,
  donor_address VARCHAR(200) NOT NULL,
  donor_city VARCHAR(100) NOT NULL,
  donor_state VARCHAR(20) NOT NULL,
  donor_zip VARCHAR(10) NOT NULL
);

CREATE TABLE fund (
  fund_id INTEGER PRIMARY KEY AUTO_INCREMENT,
  fund_name VARCHAR(50) NOT NULL
);

CREATE TABLE gift (
  gift_id INTEGER PRIMARY KEY AUTO_INCREMENT,
  donor_id INTEGER NOT NULL,
  gift_date DATE NOT NULL,

  FOREIGN KEY (donor_id) REFERENCES donor (donor_id),
  INDEX (donor_id)
);

CREATE TABLE gift_fund_allocation (
  gf_alloc_id INTEGER PRIMARY KEY AUTO_INCREMENT,
  gift_id INTEGER NOT NULL REFERENCES gift,
  fund_id INTEGER NOT NULL REFERENCES fund,
  amount DECIMAL NOT NULL,

  FOREIGN KEY (gift_id) REFERENCES gift (gift_id),
  FOREIGN KEY (fund_id) REFERENCES fund (fund_id),
  INDEX (gift_id),
  INDEX (fund_id)
);

-- BEGIN TABLE donor
INSERT INTO donor (donor_name, donor_email, donor_address, donor_city, donor_state, donor_zip) values ('George Coleman', 'gcoleman0@narod.ru', '80042 Manley Lane', 'Greenfield', 'MA', '48913-8689');
INSERT INTO donor (donor_name, donor_email, donor_address, donor_city, donor_state, donor_zip) values ('Todd Burton', 'tburton1@xing.com', '35476 Lawn Pass', 'Huntington', 'IN', '51288');
INSERT INTO donor (donor_name, donor_email, donor_address, donor_city, donor_state, donor_zip) values ('Melissa Gibson', 'mgibson2@dailymail.co.uk', '43 Spohn Terrace', 'Modesto', 'CA', '17566-6735');

-- BEGIN TABLE fund
INSERT INTO fund (fund_name) VALUES ('General / Operations');
INSERT INTO fund (fund_name) VALUES ('Cat Sheltering');
INSERT INTO fund (fund_name) VALUES ('Dog Sheltering');

-- BEGIN TABLE gift
INSERT INTO gift (gift_id, donor_id, gift_date) VALUES (935, 1, '2009-10-06');
INSERT INTO gift (gift_id, donor_id, gift_date) VALUES (936, 1, '2010-01-19');
INSERT INTO gift (gift_id, donor_id, gift_date) VALUES (937, 1, '2005-06-01');

-- BEGIN TABLE gift_fund_allocation
INSERT INTO gift_fund_allocation (gf_alloc_id, gift_id, fund_id, amount) VALUES (2045, 935, 1, 204);
INSERT INTO gift_fund_allocation (gf_alloc_id, gift_id, fund_id, amount) VALUES (2046, 936, 1, 845);
INSERT INTO gift_fund_allocation (gf_alloc_id, gift_id, fund_id, amount) VALUES (2047, 936, 2, 966);
