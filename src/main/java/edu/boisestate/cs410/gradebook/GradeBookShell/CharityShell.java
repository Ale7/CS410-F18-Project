package main.java.edu.boisestate.cs410.gradebook.GradeBookShell;

import com.budhash.cliche.Command;
import com.budhash.cliche.ShellFactory;

import java.io.IOException;
import java.sql.*;

public class CharityShell {
    private final Connection db;

    public CharityShell(Connection cxn) {
        db = cxn;
    }

    public static void main(String[] args) throws IOException, SQLException {
        String dbUrl = args[0];
        try (Connection cxn = DriverManager.getConnection("jdbc:" + dbUrl)) {
            CharityShell shell = new CharityShell(cxn);
            ShellFactory.createConsoleShell("charity", "", shell)
                        .commandLoop();
        }
    }
    
    @Command
    public void findDonor(String name) throws SQLException
    {
    	String query =
    			  "SELECT SUM(gf.amount), d.donor_id, d.donor_name "
    			+ "FROM donor d "
    			+ "JOIN gift g ON g.donor_id = d.donor_id "
    			+ "JOIN gift_fund_allocation gf ON gf.gift_id = g.gift_id "
    			+ "WHERE d.donor_name LIKE ('%' || ? || '%') "
    			+ "GROUP BY d.donor_id, d.donor_name";
    	
    	try (PreparedStatement stmt = db.prepareStatement(query)) {
    		stmt.setString(1, name);
    		try (ResultSet rs = stmt.executeQuery()) {
    			System.out.println("Donors matching: " + name + "\n");
    			while (rs.next()) {
    				int donor_id = rs.getInt("donor_id");
    				String donor_name = rs.getString("donor_name");
    				int donor_total = rs.getInt(1);
    				System.out.println("Donor ID: " + donor_id + "\nName: " + donor_name + "\nTotal Donations: " + donor_total + "\n");
    			}
    		}
    	}
    }
    
    @Command
    public void donorReport(int id, int year) throws SQLException
    {
    	System.out.println("Donor report for id = " + id + ", year = " + year + "\n");
    	
    	String query =
  			  "SELECT d.donor_id, d.donor_name, g.gift_id, gift_date, SUM(amount) "
  			+ "FROM gift g "
  			+ "JOIN gift_fund_allocation gf ON g.gift_id = gf.gift_id "
  			+ "JOIN donor d ON g.donor_id = d.donor_id "
  			+ "WHERE d.donor_id = ? "
  			+ "AND EXTRACT(YEAR FROM gift_date) = ? "
  			+ "GROUP BY d.donor_id, g.gift_id";
    	
    	try (PreparedStatement stmt = db.prepareStatement(query)) {
    		stmt.setInt(1, id);
    		stmt.setInt(2, year);
    		try (ResultSet rs = stmt.executeQuery()) {
    			System.out.println("GIFTS GIVEN:\n");
    			System.out.format("%-15s%-15s%-15s\n\n", "Gift ID", "Gift Date", "Gift Amount");
    			int gift_total = 0;
    			while (rs.next()) {
    				int gift_id = rs.getInt("gift_id");
    				String gift_date = rs.getString("gift_date");
    				int gift_amount = rs.getInt("sum");
    				System.out.format("%-15d%-15s%-15d\n", gift_id, gift_date, gift_amount);
    				gift_total += gift_amount;
    			}
    			System.out.println("\nTotal of all gifts: " + gift_total + "\n");
    		}
    	}
    	
    	String query2 =
    		  "SELECT fund_name, SUM(amount) "
    		+ "FROM fund f "
    		+ "JOIN gift_fund_allocation gf ON f.fund_id = gf.fund_id "
    		+ "JOIN gift g ON gf.gift_id = g.gift_id "
    		+ "JOIN donor d ON g.donor_id = d.donor_id "
    		+ "WHERE d.donor_id = ? "
    		+ "AND EXTRACT(YEAR FROM gift_date) = ? "
    		+ "GROUP BY gf.fund_id, fund_name";
    	
    	try (PreparedStatement stmt = db.prepareStatement(query2)) {
    		stmt.setInt(1, id);
    		stmt.setInt(2, year);
    		try (ResultSet rs = stmt.executeQuery()) {
    			System.out.println("FUNDS SUPPORTED:\n");
    			System.out.format("%-30s%-30s\n\n", "Gift Fund", "Total Donated");
    			while (rs.next()) {
    				String gift_fund = rs.getString("fund_name");
    				int fund_total = rs.getInt("sum");
    				System.out.format("%-30s%-30d\n", gift_fund, fund_total);
    			}
    		}
    	}
    	
    	String query3 =
      		  "SELECT EXTRACT(YEAR FROM gift_date) as year, SUM(amount) AS total_received "
      		+ "FROM gift_fund_allocation gf "
      		+ "JOIN gift g ON gf.gift_id = g.gift_id "
      		+ "JOIN donor d ON g.donor_id = d.donor_id "
      		+ "WHERE d.donor_id = ? "
      		+ "AND EXTRACT(YEAR FROM gift_date) <= ? "
      		+ "GROUP BY year "
      		+ "ORDER BY year DESC";
      	
      	try (PreparedStatement stmt = db.prepareStatement(query3)) {
      		stmt.setInt(1, id);
      		stmt.setInt(2, year);
      		try (ResultSet rs = stmt.executeQuery()) {
      			System.out.println("\nHISTORY:\n");
      			System.out.format("%-15s%-15s\n\n", "Year", "Total Donated");
      			while (rs.next()) {
      				int prev_year = rs.getInt("year");
      				int fund_total = rs.getInt("total_received");
      				System.out.format("%-15s%-15d\n", prev_year, fund_total);
      			}
      		}
      	}
    }
    
    @Command
    public void topDonors(int year) throws SQLException
    {
    	String query =
  			  "SELECT d.donor_name, SUM(amount) "
  			+ "FROM donor d "
  			+ "JOIN gift g ON g.donor_id = d.donor_id "
  			+ "JOIN gift_fund_allocation gf ON gf.gift_id = g.gift_id "
  			+ "WHERE EXTRACT(YEAR FROM gift_date) = ? "
  			+ "GROUP BY d.donor_name "
  			+ "ORDER BY SUM(amount) DESC "
  			+ "LIMIT 10";
    	
    	try (PreparedStatement stmt = db.prepareStatement(query)) {
    		stmt.setInt(1, year);
    		try (ResultSet rs = stmt.executeQuery()) {
    			System.out.println("Top Donors of " + year + ":\n");
    			while (rs.next()) {
    				String donor_name = rs.getString("donor_name");
    				int donor_total = rs.getInt(2);
    				System.out.println("Donor Name: " + donor_name + "\nTotal Donated: " + donor_total + "\n");
    			}
    		}
    	}	
    }
}
