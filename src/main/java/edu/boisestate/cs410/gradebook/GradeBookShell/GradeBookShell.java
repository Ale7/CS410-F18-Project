package main.java.edu.boisestate.cs410.gradebook.GradeBookShell;

import com.budhash.cliche.Command;
import com.budhash.cliche.ShellFactory;

import java.io.IOException;
import java.sql.*;

/*
 * @authors Alec Wooding and Brandon Barker
 * Simple Java application for managing grades in a class. Command shell uses Cliche.
 */
public class GradeBookShell {
    private final Connection db;
    public int selectedClassID;

    public GradeBookShell(Connection cxn) {
        db = cxn;
    }

    public static void main(String[] args) throws IOException, SQLException {
        String dbUrl = args[0];
        try (Connection cxn = DriverManager.getConnection("jdbc:" + dbUrl)) {
            GradeBookShell shell = new GradeBookShell(cxn);
            ShellFactory.createConsoleShell("grade-manager", "", shell)
                        .commandLoop();
        }
    }
    
    /**
     * Create a new class
     * 
     * @param courseNum - course number, EX: CS410
     * @param term - the term of the course, EX: Spring
     * @param year - year of the course, EX: 2015
     * @param section - section # of course, EX: 2
     * @param courseDescription - short description of course, EX: "Databases"
     * @throws SQLException
     */
    @Command
    public void newClass(String courseNum, String term, int year, int section, String courseDescription) throws SQLException
    {
    	String insert =
    			  "INSERT INTO course (course_class_num, course_term, course_year, course_section_num, course_description) "
    			+ "VALUES (?, ?, ?, ?, ?) ";
    	
    	try (PreparedStatement stmt = db.prepareStatement(insert)) {
    		stmt.setString(1, courseNum);
    		stmt.setString(2, term);
    		stmt.setInt(3, year);
    		stmt.setInt(4, section);
    		stmt.setString(5, courseDescription);
    		stmt.execute();
    	}
    	
    }
    
    /**
     * Adds a category to the currently selected class
     * 
     * @param name - category name, EX: Homework
     * @param weight - int value of the weight of the category, EX: 15
     * @throws SQLException
     */
    @Command
    public void addCategory(String name, int weight) throws SQLException
    {
    	if (selectedClassID == 0) {
    		System.out.println("You do not currently have a selected class. One needs to be selected before adding a category.");
    		return;
    	}
    	
    	String insert =
    			  "INSERT INTO category (category_name, category_weight, course_id) "
    			+ "VALUES (?, ?, ? )";
    	
    	try (PreparedStatement stmt = db.prepareStatement(insert)) {
    		stmt.setString(1, name);
    		stmt.setInt(2, weight);
    		stmt.setInt(3, selectedClassID);
    		stmt.execute();
    		//System.out.println("Debug: Category added.");
    	}
    	
    }
    
    /**
     * Adds a category to the currently selected class
     * 
     * @param name - item, EX: Assignment1
     * @param category - the category the item falls under
     * @param description - description of the assignment, EX: practice basic math
     * @param points - int value of the total possible points, EX: 100
     * @throws SQLException
     */
    @Command
    public void addItem(String name, String category, String description, int points) throws SQLException
    {
    	int category_id = 0;
    	
    	if (selectedClassID == 0) {
    		System.out.println("You do not currently have a selected class. One needs to be selected before adding a category.\n");
    		return;
    	}
    	
    	String query = 
    			"SELECT category_id "
    		  + "FROM category "
    		  + "WHERE category_name = ? AND course_id = ? ";
    	
    	//Get the category_id
    	try (PreparedStatement qry = db.prepareStatement(query)) {
    		qry.setString(1, category);
    		qry.setInt(2, selectedClassID);
    		
    		try(ResultSet rs = qry.executeQuery()) { 
    			if(rs.next()) {
    				category_id = rs.getInt("category_id");
    			}
    		}
    	}
    	
    	if (category_id == 0) {
    		System.out.println("Could not find the specified category for the currently selected class. Are you sure it exists?\n");
    		return;
    	}
    	
    	String insert =
    			  "INSERT INTO item (item_name, category_id, item_description, item_point_value) "
    			+ "VALUES (?, ?, ?, ?)";
    	
    	try (PreparedStatement stmt = db.prepareStatement(insert)) {
    		stmt.setString(1, name);
    		stmt.setInt(2, category_id);
    		stmt.setString(3, description);
    		stmt.setInt(4, points);
    		stmt.execute();
    		//System.out.println("Debug: Item added.");
    	}
    
    }
    
    /**
     * Selects a class with the specified criteria
     * 
     * @param courseNum - course number, EX: CS410
     * @param term - the term of the course, EX: Spring
     * @param year - year of the course, EX: 2015
     * @throws SQLException
     */
    @Command
    public void selectClass(String courseNum, String term, int year) throws SQLException
    {
    	//Example: select-class cs444 Spring 2018
    	String query =
    			  "SELECT c.course_id "
    			+ "FROM course c "
    			+ "WHERE c.course_class_num = ? "
    			+ "AND c.course_term = ? "
    			+ "AND c.course_year = ? "; 
    	
    	try (PreparedStatement stmt = db.prepareStatement(query)) {
    		stmt.setString(1, courseNum);
    		stmt.setString(2, term);
    		stmt.setInt(3, year);
    		    		
    		try(ResultSet rs = stmt.executeQuery()) { 
    			
    			if(rs.next()) {
    				int tempHolder = rs.getInt("course_id");
    				if (!rs.next()){
    					selectedClassID = tempHolder;
    					System.out.println("Class successfully selected.\n");
    				} else {
    					System.out.println("There are multiple sections with the given criteria. Please specify a section.\n");
    				}
    			}
    		}
    	}
    	
    	//System.out.println("The currently selected class is ID: " + selectedClassID); //For Testing
    }
    
    
    /**
     * Prints the currently selected class.
     * 
     * @throws SQLException
     */
    @Command
    public void showClass() throws SQLException
    {
    	String query =
    			  "SELECT * "
    			+ "FROM course c "
    			+ "WHERE c.course_id = ?"; 
    	
    	try (PreparedStatement stmt = db.prepareStatement(query)) {
    		stmt.setInt(1, selectedClassID);
    		
    		try (ResultSet rs = stmt.executeQuery()) {
    			System.out.println("Currently active class:\n");
    			
    			while (rs.next()) {
    				String course_class_num = rs.getString("course_class_num");
    				String course_term = rs.getString("course_term");
    				int course_year = rs.getInt("course_year");
    				int course_section_num = rs.getInt("course_section_num");
    				String course_description = rs.getString("course_description");
    				System.out.format("%-15s%-15s%-15d%-15d%-15s\n", course_class_num,
    								  course_term, course_year, course_section_num, course_description);
    			}
    		}
    	}
    }
    
    /**
     * Adds a student to the currently active class
     * 
     * @param username - username of student, EX: Alecw2938
     * @param studentid - student id, EX: 0239931782
     * @param name - name of student, EX: "Wooding, Alec"
     * @throws SQLException
     */
    @Command
    public void addStudent(String username, int studentid, String name) throws SQLException
    {
    	String insert =
    			"INSERT INTO student (student_id, student_username, student_name) VALUES (?, ?, ?)";
    	String insert2 =
    			"INSERT INTO student_enrolls_course (student_id, course_id) VALUES (?, ?)";
      	
      	try (PreparedStatement stmt = db.prepareStatement(insert)) {
      		stmt.setInt(1, studentid);
      		stmt.setString(2, username);
      		stmt.setString(3, name);
      		stmt.execute();
      	}
      	
      	try (PreparedStatement stmt = db.prepareStatement(insert2)) {
      		stmt.setInt(1,  studentid);
      		stmt.setInt(2,  selectedClassID);
      		stmt.execute();
      	}
    }
    
    /**
     * Prints all stored students from the currently active class
     * 
     * @throws SQLException
     */
    @Command
    public void showStudents() throws SQLException
    {    	
    	String query =
  			  "SELECT s.student_id, s.student_username, s.student_name "
  			+ "FROM student s "
  			+ "JOIN student_enrolls_course sec ON s.student_id = sec.student_id "
  			+ "WHERE sec.course_id = ?";
    	
    	try (PreparedStatement stmt = db.prepareStatement(query)) {
    		stmt.setInt(1, selectedClassID);
    		
    		try (ResultSet rs = stmt.executeQuery()) {
    			System.out.println("STUDENTS:\n");
    			
    			while (rs.next()) {
    				int student_id = rs.getInt("student_id");
    				String student_username = rs.getString("student_username");
    				String student_name = rs.getString("student_name");
    				System.out.format("%-15d%-15s%-15s\n", student_id, student_username, student_name);
    			}
    		}
    	}
    }
    
    /**
     * Prints all stored students where the provided string is found in 
     * name or username from the currently active class
     * 
     * @param match - the string to match
     * @throws SQLException
     */
    @Command
    public void showStudents(String match) throws SQLException
    {
    	String query =
    			  "SELECT s.student_id, s.student_username, s.student_name "
    			+ "FROM student s "
    			+ "JOIN student_enrolls_course sec ON s.student_id = sec.student_id "
    			+ "WHERE (sec.course_id = ?) "
    			+ "AND (s.student_name ILIKE ('%' || ? || '%') "
    			+ "OR s.student_username ILIKE ('%' || ? || '%'))";
      	
      	try (PreparedStatement stmt = db.prepareStatement(query)) {
      		stmt.setInt(1, selectedClassID);
      		stmt.setString(2, match);
      		stmt.setString(3, match);
      		
      		try (ResultSet rs = stmt.executeQuery()) {
      			System.out.println("STUDENTS:\n");
      			
      			while (rs.next()) {
      				int student_id = rs.getInt("student_id");
      				String student_username = rs.getString("student_username");
      				String student_name = rs.getString("student_name");
      				System.out.format("%-15d%-15s%-15s\n", student_id, student_username, student_name);
      			}
      		}
      	}
    }

}