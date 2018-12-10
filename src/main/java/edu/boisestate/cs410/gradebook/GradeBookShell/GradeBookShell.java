package main.java.edu.boisestate.cs410.gradebook.GradeBookShell;

import com.budhash.cliche.Command;
import com.budhash.cliche.ShellFactory;

import java.io.IOException;
import java.sql.*;

/*
 * Simple Java application for managing grades in a class. Command shell uses Cliche.
 */
public class GradeBookShell {
    private final Connection db;

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
     * Adds a student 
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
      	
      	try (PreparedStatement stmt = db.prepareStatement(insert)) {
      			stmt.setInt(1, studentid);
      			stmt.setString(2, username);
      			stmt.setString(3, name);
      			stmt.execute();
      	}
    }
    
    /**
     * Prints all stored students
     * 
     * @throws SQLException
     */
    @Command
    public void showStudents() throws SQLException
    {    	
    	String query =
  			  "SELECT s.student_id, s.student_username, s.student_name "
  			+ "FROM student s ";
    	
    	try (PreparedStatement stmt = db.prepareStatement(query)) {
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
     * Prints all stored students where the provided string is found in name or username
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
    			+ "WHERE s.student_name LIKE ('%' || ? || '%') OR "
    			+ "WHERE s.student_username LIKE ('%' || ? || '%') ";
      	
      	try (PreparedStatement stmt = db.prepareStatement(query)) {
      		stmt.setString(1, match);
      		stmt.setString(2, match);
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
