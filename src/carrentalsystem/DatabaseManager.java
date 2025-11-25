package carrentalsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:car_rental.db";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create Cars table
            String createCarsTable = "CREATE TABLE IF NOT EXISTS cars (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "make TEXT NOT NULL, " +
                    "model TEXT NOT NULL, " +
                    "year INTEGER NOT NULL, " +
                    "license_plate TEXT UNIQUE NOT NULL, " +
                    "daily_rate REAL NOT NULL, " +
                    "available INTEGER DEFAULT 1)";
            
            // Create Customers table
            String createCustomersTable = "CREATE TABLE IF NOT EXISTS customers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "phone TEXT NOT NULL, " +
                    "address TEXT)";
            
            // Create Rentals table
            String createRentalsTable = "CREATE TABLE IF NOT EXISTS rentals (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "car_id INTEGER NOT NULL, " +
                    "customer_id INTEGER NOT NULL, " +
                    "start_date TEXT NOT NULL, " +
                    "end_date TEXT NOT NULL, " +
                    "total_cost REAL NOT NULL, " +
                    "status TEXT NOT NULL, " +
                    "FOREIGN KEY (car_id) REFERENCES cars(id), " +
                    "FOREIGN KEY (customer_id) REFERENCES customers(id))";
            
            stmt.execute(createCarsTable);
            stmt.execute(createCustomersTable);
            stmt.execute(createRentalsTable);
            
            System.out.println("Database initialized successfully");
            
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }
}