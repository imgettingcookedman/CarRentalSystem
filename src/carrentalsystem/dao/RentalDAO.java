package carrentalsystem.dao;

import carrentalsystem.model.Rental;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {
    
    public List<Rental> getAllRentals() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals";
        
        try (Connection conn = carrentalsystem.DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Rental rental = new Rental(
                    rs.getInt("id"),
                    rs.getInt("car_id"),
                    rs.getInt("customer_id"),
                    LocalDate.parse(rs.getString("start_date")),
                    LocalDate.parse(rs.getString("end_date")),
                    rs.getDouble("total_cost"),
                    rs.getString("status")
                );
                rentals.add(rental);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all rentals: " + e.getMessage());
        }
        
        return rentals;
    }
    
    public List<Rental> getActiveRentals() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals WHERE status = 'Active'";
        
        try (Connection conn = carrentalsystem.DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Rental rental = new Rental(
                    rs.getInt("id"),
                    rs.getInt("car_id"),
                    rs.getInt("customer_id"),
                    LocalDate.parse(rs.getString("start_date")),
                    LocalDate.parse(rs.getString("end_date")),
                    rs.getDouble("total_cost"),
                    rs.getString("status")
                );
                rentals.add(rental);
            }
        } catch (SQLException e) {
            System.err.println("Error getting active rentals: " + e.getMessage());
        }
        
        return rentals;
    }
    
    public Rental getRentalById(int id) {
        String sql = "SELECT * FROM rentals WHERE id = ?";
        
        try (Connection conn = carrentalsystem.DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Rental(
                    rs.getInt("id"),
                    rs.getInt("car_id"),
                    rs.getInt("customer_id"),
                    LocalDate.parse(rs.getString("start_date")),
                    LocalDate.parse(rs.getString("end_date")),
                    rs.getDouble("total_cost"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting rental by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean addRental(Rental rental) {
        String sql = "INSERT INTO rentals(car_id, customer_id, start_date, end_date, total_cost, status) VALUES(?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = carrentalsystem.DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rental.getCarId());
            pstmt.setInt(2, rental.getCustomerId());
            pstmt.setString(3, rental.getStartDate().toString());
            pstmt.setString(4, rental.getEndDate().toString());
            pstmt.setDouble(5, rental.getTotalCost());
            pstmt.setString(6, rental.getStatus());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding rental: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateRental(Rental rental) {
        String sql = "UPDATE rentals SET car_id = ?, customer_id = ?, start_date = ?, end_date = ?, total_cost = ?, status = ? WHERE id = ?";
        
        try (Connection conn = carrentalsystem.DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rental.getCarId());
            pstmt.setInt(2, rental.getCustomerId());
            pstmt.setString(3, rental.getStartDate().toString());
            pstmt.setString(4, rental.getEndDate().toString());
            pstmt.setDouble(5, rental.getTotalCost());
            pstmt.setString(6, rental.getStatus());
            pstmt.setInt(7, rental.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating rental: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteRental(int id) {
        String sql = "DELETE FROM rentals WHERE id = ?";
        
        try (Connection conn = carrentalsystem.DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting rental: " + e.getMessage());
            return false;
        }
    }
    
    public boolean completeRental(int id) {
        String sql = "UPDATE rentals SET status = 'Completed' WHERE id = ?";
        
        try (Connection conn = carrentalsystem.DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error completing rental: " + e.getMessage());
            return false;
        }
    }
}