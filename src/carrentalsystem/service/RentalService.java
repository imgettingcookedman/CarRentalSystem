package carrentalsystem.service;

import carrentalsystem.dao.RentalDAO;
import carrentalsystem.model.Rental;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RentalService {
    private final RentalDAO rentalDAO;
    private final CarService carService;
    
    public RentalService() {
        this.rentalDAO = new RentalDAO();
        this.carService = new CarService();
    }
    
    public List<Rental> getAllRentals() {
        return rentalDAO.getAllRentals();
    }
    
    public List<Rental> getActiveRentals() {
        return rentalDAO.getActiveRentals();
    }
    
    public Rental getRentalById(int id) {
        return rentalDAO.getRentalById(id);
    }
    
    public boolean createRental(int carId, int customerId, LocalDate startDate, LocalDate endDate) {
        // Validate dates
        if (startDate == null || endDate == null) {
            System.err.println("Start date and end date cannot be null");
            return false;
        }
        
        if (startDate.isAfter(endDate)) {
            System.err.println("Start date cannot be after end date");
            return false;
        }
        
        if (startDate.isBefore(LocalDate.now())) {
            System.err.println("Start date cannot be in the past");
            return false;
        }
        
        // Check if car is available
        var car = carService.getCarById(carId);
        if (car == null) {
            System.err.println("Car not found");
            return false;
        }
        
        if (!car.isAvailable()) {
            System.err.println("Car is not available for rental");
            return false;
        }
        
        // Calculate total cost
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double totalCost = car.getDailyRate() * daysBetween;
        
        // Create rental
        Rental rental = new Rental(carId, customerId, startDate, endDate, totalCost, "Active");
        boolean result = rentalDAO.addRental(rental);
        
        if (result) {
            // Update car availability
            carService.updateCarAvailability(carId, false);
        }
        
        return result;
    }
    
    public boolean completeRental(int rentalId) {
        // Check if rental exists
        Rental rental = rentalDAO.getRentalById(rentalId);
        if (rental == null) {
            System.err.println("Rental not found");
            return false;
        }
        
        if (!"Active".equals(rental.getStatus())) {
            System.err.println("Rental is not active");
            return false;
        }
        
        boolean result = rentalDAO.completeRental(rentalId);
        
        if (result) {
            // Update car availability
            carService.updateCarAvailability(rental.getCarId(), true);
        }
        
        return result;
    }
    
    public boolean updateRental(Rental rental) {
        // Validate rental data
        if (rental.getStartDate() == null || rental.getEndDate() == null) {
            System.err.println("Start date and end date cannot be null");
            return false;
        }
        
        if (rental.getStartDate().isAfter(rental.getEndDate())) {
            System.err.println("Start date cannot be after end date");
            return false;
        }
        
        return rentalDAO.updateRental(rental);
    }
    
    public boolean deleteRental(int id) {
        // Check if rental exists
        Rental rental = rentalDAO.getRentalById(id);
        if (rental == null) {
            System.err.println("Rental not found");
            return false;
        }
        
        boolean result = rentalDAO.deleteRental(id);
        
        if (result && "Active".equals(rental.getStatus())) {
            // Update car availability if rental was active
            carService.updateCarAvailability(rental.getCarId(), true);
        }
        
        return result;
    }
}