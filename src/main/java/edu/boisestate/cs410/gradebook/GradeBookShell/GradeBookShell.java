package main.java.edu.boisestate.cs410.gradebook.GradeBookShell;

import com.budhash.cliche.Command;
import com.budhash.cliche.ShellFactory;

import java.io.IOException;
import java.sql.*;

/*
 * @author Alec Wooding
 * @author Brandon Barker
 * 
 * Revised 3/16/2020
 * 
 * Simple Java application for managing grades in a class, command shell uses Cliche
 */
public class GradeBookShell {
	private final Connection db; // EX: jdbc:postgresql://localhost/charity?user=postgres&password=password
	public int selectedClassID; // 'course_id' of currently active/selected class, EX: 2
	private String selectClassBaseQuery = "SELECT course_id, course_year, course_term " 
            							+ "FROM course "
            							+ "WHERE course_class_num = ? ";

	public GradeBookShell(Connection cxn) {
		db = cxn;
	}

	public static void main(String[] args) throws IOException, SQLException {
		String dbUrl = args[0];
		try (Connection cxn = DriverManager.getConnection("jdbc:" + dbUrl)) {
			GradeBookShell shell = new GradeBookShell(cxn);
			ShellFactory.createConsoleShell("grade-manager", "", shell).commandLoop();
		}
	}

	/**
	 * Sets any number of values for a prepared statement
	 * 
	 * @param preparedStatement - PreparedStatement object, EX: stmt
	 * @param values            - String/int values used in query, EX: courseNum, year ... 
	 * @throws SQLException
	 */
	private static void setValues(PreparedStatement preparedStatement, Object... values) throws SQLException {
		for (int i = 0; i < values.length; i++) {
			preparedStatement.setObject(i + 1, values[i]);
		}
	}
	
	/**
	 * Prepares and executes an INSERT query
	 * 
	 * @param query  - String representation of query
	 * @param values - String/int values used in query, EX: courseNum, year ... 
	 * @throws SQLException
	 */
	private void insertQuery(String query, Object... values) throws SQLException {
		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeBookShell.setValues(stmt, values);
			stmt.execute();
		}
	}

	/**
	 * Create a new class
	 * 
	 * Example command: new-class CS410 Fall 2018 2 "Databases" 
	 * 
	 * @param courseNum         - course number, EX: CS410
	 * @param term              - the term of the course, EX: Fall
	 * @param year              - year of the course, EX: 2018
	 * @param section           - section # of course, EX: 2
	 * @param courseDescription - short description of course, EX: "Databases"
	 * @throws SQLException
	 */
	@Command
	public void newClass(String courseNum, String term, int year, int section, String courseDescription) throws SQLException {
		String query = "INSERT INTO course (course_class_num, course_term, course_year, course_section_num, course_description) "
					 + "VALUES (?, ?, ?, ?, ?)";
		insertQuery(query, courseNum, term, year, section, courseDescription);
	}

	/**
	 * Selects a class with the specified criteria
	 * 
	 * Example command: select-class CS410
	 * 
	 * @param courseNum - course number, EX: CS410
	 * @throws SQLException
	 */
	@Command
	public void selectClass(String courseNum) throws SQLException {
		String query = selectClassBaseQuery 
				     + "ORDER BY course_year DESC, course_term";

		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeBookShell.setValues(stmt, courseNum);

			try (ResultSet rs = stmt.executeQuery()) {

				if (rs.next()) {
					selectedClassID = rs.getInt("course_id");

					if (rs.next()) {
						if ((rs.getInt("course_year") == rs.getInt("course_year")) && (rs.getString("course_term").equals(rs.getString("course_term")))) {
							System.out.println("There are multiple sections with the given criteria in the latest term. Please specify a section.\n");
							return;
						}
					}

					System.out.println("Class successfully selected.\n");
				}
			}
		}
	}

	/**
	 * Selects a class with the specified criteria
	 * 
	 * Example command: select-class CS410 Fall 2018
	 * 
	 * @param courseNum - course number, EX: CS410
	 * @param term      - the term of the course, EX: Fall
	 * @param year      - year of the course, EX: 2018
	 * @throws SQLException
	 */
	@Command
	public void selectClass(String courseNum, String term, int year) throws SQLException {
		String query = selectClassBaseQuery
	                 + "AND course_term = ? " 
	                 + "AND course_year = ?";

		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeBookShell.setValues(stmt, courseNum, term, year);

			try (ResultSet rs = stmt.executeQuery()) {

				if (rs.next()) {
					if (!rs.next()) {
						selectedClassID = rs.getInt("course_id");
						System.out.println("Class successfully selected.\n");
					} else {
						System.out.println("There are multiple sections with the given criteria. Please specify a section.\n");
					}
				}
			}
		}
	}

	/**
	 * Selects a class with the specified criteria
	 * 
	 * Example command: select-class CS410 Fall 2018 2
	 * 
	 * @param courseNum - course number, EX: CS410
	 * @param term      - the term of the course, EX: Fall
	 * @param year      - year of the course, EX: 2018
	 * @param section   - section # of course, EX: 2
	 * @throws SQLException
	 */
	@Command
	public void selectClass(String courseNum, String term, int year, int section) throws SQLException {
		String query = selectClassBaseQuery
					 + "AND course_term = ? " 
					 + "AND course_year = ? " 
					 + "AND course_section_num = ?";

		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeBookShell.setValues(stmt, courseNum, term, year, section);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					selectedClassID = rs.getInt("course_id");
					System.out.println("Class successfully selected.\n\n");
				}
			}
		}
	}

	/**
	 * Prints the currently selected class.
	 * 
	 * Example command: show-class
	 * 
	 * @throws SQLException
	 */
	@Command
	public void showClass() throws SQLException {
		String query = "SELECT * " 
					 + "FROM course c " 
					 + "WHERE c.course_id = ?";

		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeBookShell.setValues(stmt, selectedClassID);

			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("Currently active class:\n");

				while (rs.next()) {
					System.out.format("%-20s%-20s%-20d%-20d%-20s\n", rs.getString("course_class_num"), rs.getString("course_term"), 
									  rs.getInt("course_year"), rs.getInt("course_section_num"), rs.getString("course_description"));
				}
			}
		}
	}

	/**
	 * Prints all categories from the currently selected class
	 * 
	 * Example command: show-categories
	 * 
	 * @throws SQLException
	 */
	@Command
	public void showCategories() throws SQLException {
		String query = "SELECT * " 
					 + "FROM category cat " 
					 + "JOIN course c ON cat.course_id = c.course_id "
					 + "WHERE c.course_id = ?";

		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeBookShell.setValues(stmt, selectedClassID);

			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("CATEGORIES:\n");

				while (rs.next()) {
					System.out.format("%-20s%-20d\n", rs.getString("category_name"), rs.getInt("category_weight"));
				}
			}
		}
	}

	/**
	 * Adds a category to the currently selected class
	 * 
	 * Example command: add-category "Homework" 20
	 * 
	 * @param name   - category name, EX: Homework
	 * @param weight - value of the weight of the category, EX: 20
	 * @throws SQLException
	 */
	@Command
	public void addCategory(String name, int weight) throws SQLException {
		if (selectedClassID == 0) {
			System.out.println("You do not currently have a selected class. One needs to be selected before adding a category.");
			return;
		}
		String query = "INSERT INTO category (category_name, category_weight, course_id) " 
					 + "VALUES (?, ?, ?)";
		insertQuery(query, name, weight, selectedClassID);
	}

	/**
	 * Prints all items from the currently selected class, grouped by category
	 * 
	 * Example command: show-items
	 * 
	 * @throws SQLException
	 */
	@Command
	public void showItems() throws SQLException {
		String query = "SELECT * " 
					 + "FROM item i " 
					 + "JOIN category cat ON i.category_id = cat.category_id "
					 + "JOIN course c ON cat.course_id = c.course_id " 
					 + "WHERE c.course_id = ? "
					 + "ORDER BY cat.category_id";

		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeBookShell.setValues(stmt, selectedClassID);

			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("ITEMS:\n");

				while (rs.next()) {
					System.out.format("%-20s%-20s%-20s%-20d\n", rs.getString("category_name"), rs.getString("item_name"), 
									  rs.getString("item_description"), rs.getInt("item_point_value"));
				}
			}
		}
	}

	/**
	 * Adds a new item to the currently selected class
	 * 
	 * Example command: add-item HW3 "Homework" "Practice Queries" 40
	 * 
	 * @param name        - item, EX: HW3
	 * @param category    - the category the item falls under, EX: "Homework"
	 * @param description - description of the assignment, EX: "Practice Queries"
	 * @param points      - value of the total possible points, EX: 40
	 * @throws SQLException
	 */
	@Command
	public void addItem(String name, String category, String description, int points) throws SQLException {
		if (selectedClassID == 0) {
			System.out.println("You do not currently have a selected class. One needs to be selected before adding an item.");
			return;
		}
		String query = "SELECT category_id " 
					 + "FROM category " 
					 + "WHERE category_name = ? " 
					 + "AND course_id = ?";

		int category_id = 0;
		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeBookShell.setValues(stmt, category, selectedClassID);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					category_id = rs.getInt("category_id");
				}
			}
		}

		if (category_id == 0) {
			System.out.println("Could not find the specified category for the currently selected class. Are you sure it exists?\n");
			return;
		}

		query = "INSERT INTO item (item_name, category_id, item_description, item_point_value) "
			  + "VALUES (?, ?, ?, ?)";
		insertQuery(query, name, category_id, description, points);
		
		System.out.println("Item added.\n");
	}

	/**
	 * Adds a student to the currently active class
	 * 
	 * Example command: add-student alecw2938 298342187 "Wooding, Alec"
	 * 
	 * @param username  - username of student, EX: alecw2938
	 * @param studentid - student id, EX: 298342187
	 * @param name      - name of student, EX: "Wooding, Alec"
	 * @throws SQLException
	 */
	@Command
	public void addStudent(String username, int studentid, String name) throws SQLException {
		String query = "INSERT INTO student (student_id, student_username, student_name) "
					  + "VALUES (?, ?, ?)";
		insertQuery(query, studentid, username, name);
		
		query = "INSERT INTO student_enrolls_course (student_id, course_id) "
			       + "VALUES (?, ?)";
		insertQuery(query, studentid, selectedClassID);
	}

	/**
	 * Prints all stored students from the currently active class
	 * 
	 * Example command: show-students
	 * 
	 * @throws SQLException
	 */
	@Command
	public void showStudents() throws SQLException {
		String query = "SELECT s.student_id, s.student_username, s.student_name " 
					 + "FROM student s "
					 + "JOIN student_enrolls_course sec ON s.student_id = sec.student_id " 
					 + "WHERE sec.course_id = ?";

		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeBookShell.setValues(stmt, selectedClassID);

			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("STUDENTS:\n");

				while (rs.next()) {
					System.out.format("%-20d%-20s%-20s\n", rs.getInt("student_id"), rs.getString("student_username"), rs.getString("student_name"));
				}
			}
		}
	}

	/**
	 * Prints all stored students where the provided string is found in name or
	 * username from the currently active class
	 * 
	 * Example command: show-students "Alec"
	 * 
	 * @param match - the string to match
	 * @throws SQLException
	 */
	@Command
	public void showStudents(String match) throws SQLException {
		String query = "SELECT s.student_id, s.student_username, s.student_name " 
					 + "FROM student s "
					 + "JOIN student_enrolls_course sec ON s.student_id = sec.student_id " 
					 + "WHERE (sec.course_id = ?) "
					 + "AND (s.student_name ILIKE ('%' || ? || '%') OR s.student_username ILIKE ('%' || ? || '%'))";

		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeBookShell.setValues(stmt, selectedClassID, match, match);

			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("STUDENTS:\n");

				while (rs.next()) {
					System.out.format("%-20d%-20s%-20s\n", rs.getInt("student_id"), rs.getString("student_username"), rs.getString("student_name"));
				}
			}
		}
	}

	/**
	 * Prints the grades of a specified student of the currently select class
	 * 
	 * Example command: student-grades alecw2938
	 * 
	 * @param username - username of student, EX: alecw2938
	 * @throws SQLException
	 */
	@Command
	public void studentGrades(String username) throws SQLException {
		String query = "SELECT i.item_name, i.item_description, cat.category_name, i.item_point_value, g.grade_score "
					 + "FROM grade g " 
					 + "JOIN student s ON g.student_id = s.student_id "
					 + "JOIN item i ON g.item_id = i.item_id "
					 + "JOIN student_enrolls_course sec ON s.student_id = sec.student_id "
					 + "JOIN category cat ON i.category_id = cat.category_id "
					 + "JOIN course c ON cat.course_id = c.course_id "
					 + "JOIN student_enrolls_course ON c.course_id = sec.course_id " 
					 + "WHERE c.course_id = ? "
					 + "AND s.student_username = ? "
					 + "GROUP BY i.item_name, i.item_description, cat.category_name, i.item_point_value, g.grade_score "
					 + "ORDER BY cat.category_name";

		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeBookShell.setValues(stmt, selectedClassID, username);

			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("Grades for: " + username + "\n");
				System.out.format("%-20s%-20s%-20s%-20s%-20s\n\n", "Name", "Description", "Category", "Point Value", "Score");

				String category_name = "";
				int category_sub_value = 0;
				int category_sub_score = 0;
				int overall_value = 0;
				int overall_score = 0;

				while (rs.next()) {					
					if (rs.isFirst()) { 
						category_name = rs.getString("category_name");
					} else {
						if (!category_name.equals(rs.getString("category_name"))) {
							System.out.println(category_name + " Grade: " + category_sub_score + "/" + category_sub_value + "\n");
							category_name = rs.getString("category_name");
														
							category_sub_value = 0;
							category_sub_score = 0;
						}
					} 
					
					category_sub_value += rs.getInt("item_point_value");
					category_sub_score += rs.getInt("grade_score");
					overall_value += rs.getInt("item_point_value");
					overall_score += rs.getInt("grade_score");
					
					System.out.format("%-20s%-20s%-20s%-20d%-20d\n", rs.getString("item_name"), rs.getString("item_description"), 
							          rs.getString("category_name"), rs.getInt("item_point_value"), rs.getInt("grade_score"));
				}
				System.out.println(category_name + " Grade: " + category_sub_score + "/" + category_sub_value + "\n");
				System.out.println("OVERALL GRADE: " + overall_score + "/" + overall_value + "\n");
			}
		}
	}

	/**
	 * Adds or updates a students grade for a specified item
	 * 
	 * Example command: grade "HW3" alecw2938 20
	 * 
	 * @param itemname - the name of the item the grade is for, EX: Division Practice Assignment
	 * @param username - username of student, EX: alecw2938
	 * @param points   - the number of points the student received, EX: 20
	 * @throws SQLException
	 */
	@Command
	public void grade(String itemname, String username, int points) throws SQLException {
		String query = "SELECT g.grade_score, i.item_id, s.student_id " 
					 + "FROM item i "
					 + "JOIN grade g ON i.item_id = g.item_id " 
					 + "JOIN student s ON g.student_id = s.student_id "
					 + "WHERE i.item_name = ? " 
					 + "AND s.student_username = ?";

		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeBookShell.setValues(stmt, itemname, username);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					query = "UPDATE grade " 
						  + "SET grade_score = ? " 
						  + "WHERE item_id = ? "
						  + "AND student_id = ?";

					try (PreparedStatement updt = db.prepareStatement(query)) {
						GradeBookShell.setValues(updt, points, rs.getInt("item_id"), rs.getInt("student_id"));
						updt.executeUpdate();
						System.out.println("Grade updated.\n");
						return;
					}

				} else {
					int item_id;
					int student_id;

					query = "SELECT item_id " 
						  + "FROM item " 
						  + "WHERE item_name = ?";

					try (PreparedStatement itemIDStmt = db.prepareStatement(query)) {
						GradeBookShell.setValues(itemIDStmt, itemname);

						try (ResultSet itemRS = itemIDStmt.executeQuery()) {
							if (itemRS.next()) {
								item_id = itemRS.getInt("item_id");
							} else {
								System.out.println("No item with the provided item name exists in the database.");
								return;
							}
						}
					}

					query = "SELECT student_id " 
						  + "FROM student " 
						  + "WHERE student_username = ?";

					try (PreparedStatement studentIDStmt = db.prepareStatement(query)) {
						GradeBookShell.setValues(studentIDStmt, username);

						try (ResultSet studentRS = studentIDStmt.executeQuery()) {
							if (studentRS.next()) {
								student_id = studentRS.getInt("student_id");
							} else {
								System.out.println("No student with the provided username exists in the database.");
								return;
							}
						}
					}

					query = "INSERT INTO grade (grade_score, item_id, student_id) " 
								  + "VALUES(?, ?, ?)";
					insertQuery(query, points, item_id, student_id);
					System.out.println("Grade added.\n");
				}
			}
		}
	}

	/**
	 * Shows the currently selected class's gradebook
	 * 
	 * Example command: gradebook
	 * 
	 * @throws SQLException
	 */
	@Command
	public void gradebook() throws SQLException {
		String query = "SELECT s.student_id, s.student_username, s.student_name, i.item_point_value, g.grade_score "
					 + "FROM grade g " 
					 + "JOIN student s ON g.student_id = s.student_id "
					 + "JOIN item i ON g.item_id = i.item_id "
					 + "JOIN student_enrolls_course sec ON s.student_id = sec.student_id "
					 + "JOIN category cat ON i.category_id = cat.category_id "
					 + "JOIN course c ON cat.course_id = c.course_id "
					 + "JOIN student_enrolls_course ON c.course_id = sec.course_id " 
					 + "WHERE c.course_id = ? "
					 + "GROUP BY s.student_id, s.student_username, s.student_name, i.item_point_value, g.grade_score "
					 + "ORDER BY s.student_id";

		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeBookShell.setValues(stmt, selectedClassID);

			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("GRADEBOOK:\n");
				System.out.format("%-20s%-20s%-20s%-20s\n", "Student ID", "Student Username", "Student Name", "Class Grade");
				
				int student_id = 0;
				String student_username = "";
				String student_name = "";
				int student_total_value = 0;
				int student_total_score = 0;

				while (rs.next()) {	
					if (rs.isFirst()) {
						student_id = rs.getInt("student_id");
						student_username = rs.getString("student_username");
						student_name = rs.getString("student_name");
					} else if (!rs.isLast()) {
						if (rs.getInt("student_id") == student_id) {
							student_total_value += rs.getInt("item_point_value");
							student_total_score += rs.getInt("grade_score");
						} else {
							System.out.format("%-20d%-20s%-20s", student_id, student_username, student_name);
							System.out.println(student_total_score + "/" + student_total_value + "\n");
							
							student_id = rs.getInt("student_id");
							student_username = rs.getString("student_username");
							student_name = rs.getString("student_name");
							
							student_total_value = 0;
							student_total_score = 0;
						}
					} else {
						System.out.format("%-20d%-20s%-20s", student_id, student_username, student_name);
						System.out.println(student_total_score + "/" + student_total_value + "\n");
					}	
				}
			}
		}
	}

}
