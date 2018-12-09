CREATE TABLE course (
  course_id SERIAL PRIMARY KEY,
  course_class_num INTEGER NOT NULL,
  course_term VARCHAR(20) NOT NULL,
  course_year INTEGER NOT NULL,
  course_section_num INTEGER NOT NULL,
  course_description VARCHAR(100) NOT NULL
);

CREATE TABLE category (
  category_id SERIAL PRIMARY KEY,
  category_name VARCHAR(100) NOT NULL,
  category_weight INTEGER NOT NULL,

  course_id INTEGER NOT NULL REFERENCES course (course_id),

  UNIQUE (category_name, course_id)
);

CREATE TABLE item (
  item_id SERIAL PRIMARY KEY,
  item_point_value INTEGER NOT NULL,
  item_description TEXT NOT NULL,
  item_name VARCHAR(100) NOT NULL,
	
  category_id INTEGER NOT NULL REFERENCES category (category_id)
);

CREATE TABLE student (
  student_id INTEGER PRIMARY KEY,
  student_username VARCHAR(100) NOT NULL,
  student_name VARCHAR(100) NOT NULL
);

CREATE TABLE grade (
  grade_score INTEGER NOT NULL,
  item_id INTEGER NOT NULL REFERENCES item (item_id),
  student_id INTEGER NOT NULL REFERENCES student (student_id),
  PRIMARY KEY (student_id, item_id)
);

CREATE TABLE student_enrolls_course (
  student_id INTEGER NOT NULL REFERENCES student (student_id),
  course_id INTEGER NOT NULL REFERENCES course (course_id),

  PRIMARY KEY (student_id, course_id)
);
