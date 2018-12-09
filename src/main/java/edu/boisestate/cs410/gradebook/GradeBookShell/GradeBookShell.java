package main.java.edu.boisestate.cs410.gradebook.GradeBookShell;

import com.budhash.cliche.Command;
import com.budhash.cliche.ShellFactory;

import java.io.IOException;
import java.sql.*;

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
    
    @Command
    public void findDonor(String name) throws SQLException
    {
    	
    }
    
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
    
    @Command
    public void newClass(String courseNum, String term, int year, int section, String courseDescription) throws SQLException
    {
    	String query =
    			  "INSERT INTO course (course_class_num, course_term, course_year, course_section_num, course_description) "
    			+ "VALUES (?, ?, ?, ?, ?) ";
    	
    	try (PreparedStatement stmt = db.prepareStatement(query)) {
    		stmt.setString(1, courseNum);
    		stmt.setString(2, term);
    		stmt.setInt(3, year);
    		stmt.setInt(4, section);
    		stmt.setString(5, courseDescription);
    		
    		stmt.execute();
    	}
    	
    }
    
    @Command
    public void studentReport() throws SQLException
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
    
    @Command
    public void topDonors(int year) throws SQLException
    {
    	
    }
}
