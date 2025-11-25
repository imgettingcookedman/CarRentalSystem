package carrentalsystem.util;

import carrentalsystem.model.Car;
import carrentalsystem.model.Customer;
import carrentalsystem.model.Rental;
import carrentalsystem.service.CarService;
import carrentalsystem.service.CustomerService;
import carrentalsystem.service.RentalService;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileManager {
    private static final String LOG_FILE = "car_rental_log.txt";
    private static final String EXPORT_DIR = "exports";
    
    public static void logAction(String action) {
        try {
            // Create log file if it doesn't exist
            Path logPath = Paths.get(LOG_FILE);
            if (!Files.exists(logPath)) {
                Files.createFile(logPath);
            }
            
            // Append log entry
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                writer.write(timestamp + " - " + action);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
    
    public static boolean exportCarsToCSV(String filename) {
        CarService carService = new CarService();
        List<Car> cars = carService.getAllCars();
        
        try {
            // Create export directory if it doesn't exist
            Path exportDir = Paths.get(EXPORT_DIR);
            if (!Files.exists(exportDir)) {
                Files.createDirectories(exportDir);
            }
            
            // Write cars to CSV
            Path filePath = Paths.get(EXPORT_DIR, filename);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
                // Write header
                writer.write("ID,Make,Model,Year,License Plate,Daily Rate,Available");
                writer.newLine();
                
                // Write data
                for (Car car : cars) {
                    writer.write(String.format("%d,%s,%s,%d,%s,%.2f,%b",
                            car.getId(),
                            car.getMake(),
                            car.getModel(),
                            car.getYear(),
                            car.getLicensePlate(),
                            car.getDailyRate(),
                            car.isAvailable()));
                    writer.newLine();
                }
            }
            
            logAction("Exported cars to " + filename);
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting cars to CSV: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean exportCustomersToCSV(String filename) {
        CustomerService customerService = new CustomerService();
        List<Customer> customers = customerService.getAllCustomers();
        
        try {
            // Create export directory if it doesn't exist
            Path exportDir = Paths.get(EXPORT_DIR);
            if (!Files.exists(exportDir)) {
                Files.createDirectories(exportDir);
            }
            
            // Write customers to CSV
            Path filePath = Paths.get(EXPORT_DIR, filename);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
                // Write header
                writer.write("ID,Name,Email,Phone,Address");
                writer.newLine();
                
                // Write data
                for (Customer customer : customers) {
                    writer.write(String.format("%d,%s,%s,%s,%s",
                            customer.getId(),
                            customer.getName(),
                            customer.getEmail(),
                            customer.getPhone(),
                            customer.getAddress()));
                    writer.newLine();
                }
            }
            
            logAction("Exported customers to " + filename);
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting customers to CSV: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean exportRentalsToCSV(String filename) {
        RentalService rentalService = new RentalService();
        List<Rental> rentals = rentalService.getAllRentals();
        
        try {
            // Create export directory if it doesn't exist
            Path exportDir = Paths.get(EXPORT_DIR);
            if (!Files.exists(exportDir)) {
                Files.createDirectories(exportDir);
            }
            
            // Write rentals to CSV
            Path filePath = Paths.get(EXPORT_DIR, filename);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
                // Write header
                writer.write("ID,Car ID,Customer ID,Start Date,End Date,Total Cost,Status");
                writer.newLine();
                
                // Write data
                for (Rental rental : rentals) {
                    writer.write(String.format("%d,%d,%d,%s,%s,%.2f,%s",
                            rental.getId(),
                            rental.getCarId(),
                            rental.getCustomerId(),
                            rental.getStartDate(),
                            rental.getEndDate(),
                            rental.getTotalCost(),
                            rental.getStatus()));
                    writer.newLine();
                }
            }
            
            logAction("Exported rentals to " + filename);
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting rentals to CSV: " + e.getMessage());
            return false;
        }
    }
}