package carrentalsystem.ui;

import carrentalsystem.model.Car;
import carrentalsystem.model.Customer;
import carrentalsystem.model.Rental;
import carrentalsystem.service.CarService;
import carrentalsystem.service.CustomerService;
import carrentalsystem.service.RentalService;
import carrentalsystem.util.BackgroundTask;
import carrentalsystem.util.FileManager;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ReportsPanel extends JPanel {
    private final CarService carService;
    private final CustomerService customerService;
    private final RentalService rentalService;
    private JTextArea reportArea;
    private JButton generateSummaryButton;
    private JButton generateCarUsageButton;
    private JButton generateCustomerActivityButton;
    private JButton exportReportButton;
    
    public ReportsPanel() {
        carService = new CarService();
        customerService = new CustomerService();
        rentalService = new RentalService();
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        reportArea = new JTextArea(20, 60);
        reportArea.setEditable(false);
        
        generateSummaryButton = new JButton("Generate Summary Report");
        generateCarUsageButton = new JButton("Generate Car Usage Report");
        generateCustomerActivityButton = new JButton("Generate Customer Activity Report");
        exportReportButton = new JButton("Export Report");
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Add report area to scroll pane
        JScrollPane scrollPane = new JScrollPane(reportArea);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(generateSummaryButton);
        buttonPanel.add(generateCarUsageButton);
        buttonPanel.add(generateCustomerActivityButton);
        buttonPanel.add(exportReportButton);
        
        // Create info panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("Select a report type and click Generate to view the report."), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(buttonPanel, gbc);
        
        // Add info panel to frame
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        generateSummaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateSummaryReport();
            }
        });
        
        generateCarUsageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateCarUsageReport();
            }
        });
        
        generateCustomerActivityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateCustomerActivityReport();
            }
        });
        
        exportReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportReport();
            }
        });
    }
    
    private void generateSummaryReport() {
        reportArea.setText("Generating summary report...");
        
        new BackgroundTask<String, Void>("Generate Summary Report") {
            @Override
            protected String executeInBackground() throws Exception {
                StringBuilder report = new StringBuilder();
                report.append("=====================================\n");
                report.append("      CAR RENTAL SUMMARY REPORT      \n");
                report.append("=====================================\n\n");
                
                // Get total counts
                int totalCars = carService.getAllCars().size();
                int availableCars = carService.getAvailableCars().size();
                int totalCustomers = customerService.getAllCustomers().size();
                int totalRentals = rentalService.getAllRentals().size();
                int activeRentals = rentalService.getActiveRentals().size();
                
                report.append("TOTAL CARS: ").append(totalCars).append("\n");
                report.append("AVAILABLE CARS: ").append(availableCars).append("\n");
                report.append("RENTED CARS: ").append(totalCars - availableCars).append("\n\n");
                
                report.append("TOTAL CUSTOMERS: ").append(totalCustomers).append("\n\n");
                
                report.append("TOTAL RENTALS: ").append(totalRentals).append("\n");
                report.append("ACTIVE RENTALS: ").append(activeRentals).append("\n");
                report.append("COMPLETED RENTALS: ").append(totalRentals - activeRentals).append("\n\n");
                
                // Calculate revenue from completed rentals
                double totalRevenue = 0.0;
                for (Rental rental : rentalService.getAllRentals()) {
                    if ("Completed".equals(rental.getStatus())) {
                        totalRevenue += rental.getTotalCost();
                    }
                }
                
                report.append("TOTAL REVENUE: $").append(String.format("%.2f", totalRevenue)).append("\n");
                
                report.append("\n=====================================\n");
                report.append("         END OF REPORT              \n");
                report.append("=====================================\n");
                
                return report.toString();
            }
            
            @Override
            protected void onSuccess(String result) {
                reportArea.setText(result);
                FileManager.logAction("Generated summary report");
            }
        }.execute();
    }
    
    private void generateCarUsageReport() {
        reportArea.setText("Generating car usage report...");
        
        new BackgroundTask<String, Void>("Generate Car Usage Report") {
            @Override
            protected String executeInBackground() throws Exception {
                StringBuilder report = new StringBuilder();
                report.append("=====================================\n");
                report.append("      CAR USAGE REPORT              \n");
                report.append("=====================================\n\n");
                
                List<Car> cars = carService.getAllCars();
                
                // Create a map to track rental count for each car
                Map<Integer, Integer> rentalCount = new HashMap<>();
                Map<Integer, Double> totalRevenue = new HashMap<>();
                
                // Initialize maps
                for (Car car : cars) {
                    rentalCount.put(car.getId(), 0);
                    totalRevenue.put(car.getId(), 0.0);
                }
                
                // Calculate rental statistics
                for (Rental rental : rentalService.getAllRentals()) {
                    rentalCount.put(rental.getCarId(), rentalCount.get(rental.getCarId()) + 1);
                    if ("Completed".equals(rental.getStatus())) {
                        totalRevenue.put(rental.getCarId(), 
                                totalRevenue.get(rental.getCarId()) + rental.getTotalCost());
                    }
                }
                
                // Generate report
                report.append(String.format("%-20s %-15s %-15s %-15s\n", 
                        "CAR", "TIMES RENTED", "REVENUE", "STATUS"));
                report.append("------------------------------------------------------------\n");
                
                for (Car car : cars) {
                    report.append(String.format("%-20s %-15d $%-14.2f %-15s\n", 
                            car.toString(), 
                            rentalCount.get(car.getId()), 
                            totalRevenue.get(car.getId()),
                            car.isAvailable() ? "Available" : "Rented"));
                }
                
                report.append("\n=====================================\n");
                report.append("         END OF REPORT              \n");
                report.append("=====================================\n");
                
                return report.toString();
            }
            
            @Override
            protected void onSuccess(String result) {
                reportArea.setText(result);
                FileManager.logAction("Generated car usage report");
            }
        }.execute();
    }
    
    private void generateCustomerActivityReport() {
        reportArea.setText("Generating customer activity report...");
        
        new BackgroundTask<String, Void>("Generate Customer Activity Report") {
            @Override
            protected String executeInBackground() throws Exception {
                StringBuilder report = new StringBuilder();
                report.append("=====================================\n");
                report.append("   CUSTOMER ACTIVITY REPORT         \n");
                report.append("=====================================\n\n");
                
                List<Customer> customers = customerService.getAllCustomers();
                
                // Create a map to track rental count for each customer
                Map<Integer, Integer> rentalCount = new HashMap<>();
                Map<Integer, Double> totalSpent = new HashMap<>();
                
                // Initialize maps
                for (Customer customer : customers) {
                    rentalCount.put(customer.getId(), 0);
                    totalSpent.put(customer.getId(), 0.0);
                }
                
                // Calculate rental statistics
                for (Rental rental : rentalService.getAllRentals()) {
                    rentalCount.put(rental.getCustomerId(), rentalCount.get(rental.getCustomerId()) + 1);
                    if ("Completed".equals(rental.getStatus())) {
                        totalSpent.put(rental.getCustomerId(), 
                                totalSpent.get(rental.getCustomerId()) + rental.getTotalCost());
                    }
                }
                
                // Generate report
                report.append(String.format("%-25s %-15s %-15s\n", 
                        "CUSTOMER", "RENTALS", "TOTAL SPENT"));
                report.append("------------------------------------------------\n");
                
                for (Customer customer : customers) {
                    report.append(String.format("%-25s %-15d $%-14.2f\n", 
                            customer.toString(), 
                            rentalCount.get(customer.getId()), 
                            totalSpent.get(customer.getId())));
                }
                
                report.append("\n=====================================\n");
                report.append("         END OF REPORT              \n");
                report.append("=====================================\n");
                
                return report.toString();
            }
            
            @Override
            protected void onSuccess(String result) {
                reportArea.setText(result);
                FileManager.logAction("Generated customer activity report");
            }
        }.execute();
    }
    
    private void exportReport() {
        String reportText = reportArea.getText();
        
        if (reportText.isEmpty() || reportText.startsWith("Generating")) {
            JOptionPane.showMessageDialog(this, 
                    "Please generate a report first", 
                    "No Report", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String filename = "report_" + System.currentTimeMillis() + ".txt";
        
        try {
            java.io.BufferedWriter writer = new java.io.BufferedWriter(
                    new java.io.FileWriter("exports/" + filename));
            writer.write(reportText);
            writer.close();
            
            JOptionPane.showMessageDialog(this, 
                    "Report exported successfully to " + filename, 
                    "Export Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
            
            FileManager.logAction("Exported report to " + filename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Failed to export report: " + e.getMessage(), 
                    "Export Failed", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}