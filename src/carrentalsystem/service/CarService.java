package carrentalsystem.service;

import carrentalsystem.dao.CarDAO;
import carrentalsystem.model.Car;
import java.util.List;

public class CarService {
    private final CarDAO carDAO;
    
    public CarService() {
        this.carDAO = new CarDAO();
    }
    
    public List<Car> getAllCars() {
        return carDAO.getAllCars();
    }
    
    public List<Car> getAvailableCars() {
        return carDAO.getAvailableCars();
    }
    
    public Car getCarById(int id) {
        return carDAO.getCarById(id);
    }
    
    public boolean addCar(Car car) {
        // Validate car data
        if (car.getMake() == null || car.getMake().isEmpty()) {
            System.err.println("Car make cannot be empty");
            return false;
        }
        
        if (car.getModel() == null || car.getModel().isEmpty()) {
            System.err.println("Car model cannot be empty");
            return false;
        }
        
        if (car.getLicensePlate() == null || car.getLicensePlate().isEmpty()) {
            System.err.println("License plate cannot be empty");
            return false;
        }
        
        if (car.getYear() < 1900 || car.getYear() > java.time.Year.now().getValue() + 1) {
            System.err.println("Invalid car year");
            return false;
        }
        
        if (car.getDailyRate() <= 0) {
            System.err.println("Daily rate must be greater than 0");
            return false;
        }
        
        return carDAO.addCar(car);
    }
    
    public boolean updateCar(Car car) {
        // Validate car data
        if (car.getMake() == null || car.getMake().isEmpty()) {
            System.err.println("Car make cannot be empty");
            return false;
        }
        
        if (car.getModel() == null || car.getModel().isEmpty()) {
            System.err.println("Car model cannot be empty");
            return false;
        }
        
        if (car.getLicensePlate() == null || car.getLicensePlate().isEmpty()) {
            System.err.println("License plate cannot be empty");
            return false;
        }
        
        if (car.getYear() < 1900 || car.getYear() > java.time.Year.now().getValue() + 1) {
            System.err.println("Invalid car year");
            return false;
        }
        
        if (car.getDailyRate() <= 0) {
            System.err.println("Daily rate must be greater than 0");
            return false;
        }
        
        return carDAO.updateCar(car);
    }
    
    public boolean deleteCar(int id) {
        // Check if car exists
        Car car = carDAO.getCarById(id);
        if (car == null) {
            System.err.println("Car not found");
            return false;
        }
        
        return carDAO.deleteCar(id);
    }
    
    public boolean updateCarAvailability(int carId, boolean available) {
        return carDAO.updateCarAvailability(carId, available);
    }
}