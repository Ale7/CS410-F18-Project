CREATE DATABASE IF NOT EXISTS grade-manager;
USE grade-manager;

CREATE TABLE class (
  class_id INTEGER PRIMARY KEY AUTO_INCREMENT,
  class_course_num INTEGER(10) NOT NULL,
  class_term VARCHAR(20) NOT NULL,
  class_year INTEGER(4) NOT NULL,
  class_section_num INTEGER(10) NOT NULL
);

CREATE TABLE student (
  student_id INTEGER PRIMARY KEY AUTO_INCREMENT,
  student_username VARCHAR(100) NOT NULL,
  student_name VARCHAR(100) NOT NULL
);

CREATE TABLE grade (
  grade_score INTEGER(10) NOT NULL

  PRIMARY KEY (student_id, item_id)

  FOREIGN KEY (grade_id) REFERENCES item (item_id)
);

CREATE TABLE student_enrolls_class (
  student_id INTEGER NOT NULL REFERENCES student (student_id),
  class_id INTEGER NOT NULL REFERENCES class (class_id)

  PRIMARY KEY (student_id, class_id)
);

CREATE TABLE item (
  item_id INTEGER PRIMARY KEY AUTO_INCREMENT,
  item_point_value INTEGER(10) NOT NULL,
  item_description TEXT NOT NULL,
  item_name VARCHAR(100) NOT NULL

  FOREIGN KEY (item_id) REFERENCES category (category_id)
);

CREATE TABLE category (
  category_id INTEGER PRIMARY KEY AUTO_INCREMENT,
  category_name VARCHAR(100) NOT NULL,
  category_weight INTEGER(10) NOT NULL

  FOREIGN KEY (category_id) REFERENCES class (class_id)

  UNIQUE (category_name, class_id)
);

-- BEGIN TABLE class
INSERT INTO class (class_course_num, class_term, class_year, class_section_num) VALUES ('309', 'Spring', '2014', '2');

-- BEGIN TABLE student
INSERT INTO student (student_username, student_name) VALUES ('alec22', 'Alec Wooding');

-- BEGIN TABLE grade
INSERT INTO grade (grade_score) VALUES ('90');

-- BEGIN TABLE student_enrolls_class
-- INSERT INTO student_enrolls_class (_, _) VALUES (' ', ' ');

-- BEGIN TABLE item
INSERT INTO item (item_point_value, item_description, item_name) VALUES ('100', 'First Assignment', 'Assignment_1');

-- BEGIN TABLE category
INSERT INTO category (category_name, category_weight) VALUES ('Homework', '20');
