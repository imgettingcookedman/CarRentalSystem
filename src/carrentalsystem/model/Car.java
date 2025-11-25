package carrentalsystem.model;

public class Car {
    private int id;
    private String make;
    private String model;
    private int year;
    private String licensePlate;
    private double dailyRate;
    private boolean available;
    
    public Car() {
    }
    
    public Car(String make, String model, int year, String licensePlate, double dailyRate, boolean available) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.dailyRate = dailyRate;
        this.available = available;
    }
    
    public Car(int id, String make, String model, int year, String licensePlate, double dailyRate, boolean available) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.dailyRate = dailyRate;
        this.available = available;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getMake() {
        return make;
    }
    
    public void setMake(String make) {
        this.make = make;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    public double getDailyRate() {
        return dailyRate;
    }
    
    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    @Override
    public String toString() {
        return make + " " + model + " (" + year + ") - " + licensePlate;
    }
}