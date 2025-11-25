package carrentalsystem.dao;

import carrentalsystem.model.Car;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {
    
    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars";
        
        try (Connection conn = carrentalsystem.DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Car car = new Car(
                    rs.getInt("id"),
                    rs.getString("make"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getString("license_plate"),
                    rs.getDouble("daily_rate"),
                    rs.getBoolean("available")
                );
                cars.add(car);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all cars: " + e.getMessage());
        }
        
        return cars;
    }
    
    public List<Car> getAvailableCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE available = 1";
        
        try (Connection conn = carrentalsystem.DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Car car = new Car(
                    rs.getInt("id"),
                    rs.getString("make"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getString("license_plate"),
                    rs.getDouble("daily_rate"),
                    rs.getBoolean("available")
                );
                cars.add(car);
            }
        } catch (SQLException e) {
            System.err.println("Error getting available cars: " + e.getMessage());
        }
        
        return cars;
    }
    
    public Car getCarById(int id) {
        String sql = "SELECT * FROM cars WHERE id = ?";
        
        try (Connection conn = carrentalsystem.DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Car(
                    rs.getInt("id"),
                    rs.getString("make"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getString("license_plate"),
                    rs.getDouble("daily_rate"),
                    rs.getBoolean("available")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting car by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean addCar(Car car) {
        String sql = "INSERT INTO cars(make, model, year, license_plate, daily_rate, available) VALUES(?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = carrentalsystem.DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, car.getMake());
            pstmt.setString(2, car.getModel());
            pstmt.setInt(3, car.getYear());
            pstmt.setString(4, car.getLicensePlate());
            pstmt.setDouble(5, car.getDailyRate());
            pstmt.setBoolean(6, car.isAvailable());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding car: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateCar(Car car) {
        String sql = "UPDATE cars SET make = ?, model = ?, year = ?, license_plate = ?, daily_rate = ?, available = ? WHERE id = ?";
        
        try (Connection conn = carrentalsystem.DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, car.getMake());
            pstmt.setString(2, car.getModel());
            pstmt.setInt(3, car.getYear());
            pstmt.setString(4, car.getLicensePlate());
            pstmt.setDouble(5, car.getDailyRate());
            pstmt.setBoolean(6, car.isAvailable());
            pstmt.setInt(7, car.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating car: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteCar(int id) {
        String sql = "DELETE FROM cars WHERE id = ?";
        
        try (Connection conn = carrentalsystem.DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting car: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateCarAvailability(int carId, boolean available) {
        String sql = "UPDATE cars SET available = ? WHERE id = ?";
        
        try (Connection conn = carrentalsystem.DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, available);
            pstmt.setInt(2, carId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating car availability: " + e.getMessage());
            return false;
        }
    }
}