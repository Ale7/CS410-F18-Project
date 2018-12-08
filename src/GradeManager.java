import com.budhash.cliche.Command;
import com.budhash.cliche.ShellFactory;

import java.io.IOException;
import java.sql.*;

public class GradeManager {
    private final Connection db;

    public GradeManager(Connection cxn) {
        db = cxn;
    }

    public static void main(String[] args) throws IOException, SQLException {
        String dbUrl = args[0];
        try (Connection cxn = DriverManager.getConnection("jdbc:" + dbUrl)) {
            GradeManager shell = new GradeManager(cxn);
            ShellFactory.createConsoleShell("grade-manager", "", shell)
                        .commandLoop();
        }
    }
    
    @Command
    public void findDonor(String name) throws SQLException
    {
    	
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
