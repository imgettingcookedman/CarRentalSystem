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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class RentalManagementPanel extends JPanel {
    private final RentalService rentalService;
    private final CarService carService;
    private final CustomerService customerService;
    private JTable rentalsTable;
    private DefaultTableModel tableModel;
    private JComboBox<Car> carComboBox;
    private JComboBox<Customer> customerComboBox;
    private JComboBox<String> startYearCombo;
    private JComboBox<String> startMonthCombo;
    private JComboBox<String> startDayCombo;
    private JComboBox<String> endYearCombo;
    private JComboBox<String> endMonthCombo;
    private JComboBox<String> endDayCombo;
    private JButton createRentalButton;
    private JButton completeRentalButton;
    private JButton deleteRentalButton;
    private JButton refreshButton;
    private int selectedRentalId = -1;
    
    public RentalManagementPanel() {
        rentalService = new RentalService();
        carService = new CarService();
        customerService = new CustomerService();
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        loadRentals();
        loadAvailableCars();
        loadCustomers();
    }
    
    private void initializeComponents() {
        // Initialize table
        String[] columnNames = {"ID", "Car", "Customer", "Start Date", "End Date", "Total Cost", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        rentalsTable = new JTable(tableModel);
        
        // Initialize form fields
        carComboBox = new JComboBox<>();
        customerComboBox = new JComboBox<>();
        
        // Date components for start date
        startYearCombo = new JComboBox<>();
        startMonthCombo = new JComboBox<>();
        startDayCombo = new JComboBox<>();
        
        // Date components for end date
        endYearCombo = new JComboBox<>();
        endMonthCombo = new JComboBox<>();
        endDayCombo = new JComboBox<>();
        
        // Initialize date combos
        initializeDateCombos();
        
        // Initialize buttons
        createRentalButton = new JButton("Create Rental");
        completeRentalButton = new JButton("Complete Rental");
        deleteRentalButton = new JButton("Delete Rental");
        refreshButton = new JButton("Refresh");
    }
    
    private void initializeDateCombos() {
        int currentYear = java.time.Year.now().getValue();
        
        // Initialize year combos (current year to next 5 years)
        for (int i = 0; i <= 5; i++) {
            String year = String.valueOf(currentYear + i);
            startYearCombo.addItem(year);
            endYearCombo.addItem(year);
        }
        
        // Initialize month combos
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (String month : months) {
            startMonthCombo.addItem(month);
            endMonthCombo.addItem(month);
        }
        
        // Initialize day combos
        for (int i = 1; i <= 31; i++) {
            startDayCombo.addItem(String.valueOf(i));
            endDayCombo.addItem(String.valueOf(i));
        }
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(rentalsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Add form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new javax.swing.JLabel("Car:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(carComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new javax.swing.JLabel("Customer:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(customerComboBox, gbc);
        
        // Start date
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new javax.swing.JLabel("Start Date:"), gbc);
        
        JPanel startDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        startDatePanel.add(startDayCombo);
        startDatePanel.add(startMonthCombo);
        startDatePanel.add(startYearCombo);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(startDatePanel, gbc);
        
        // End date
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new javax.swing.JLabel("End Date:"), gbc);
        
        JPanel endDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        endDatePanel.add(endDayCombo);
        endDatePanel.add(endMonthCombo);
        endDatePanel.add(endYearCombo);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(endDatePanel, gbc);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createRentalButton);
        buttonPanel.add(completeRentalButton);
        buttonPanel.add(deleteRentalButton);
        buttonPanel.add(refreshButton);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);
        
        // Add form panel to frame
        add(formPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Table selection listener
        rentalsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = rentalsTable.getSelectedRow();
                if (row >= 0) {
                    selectedRentalId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                }
            }
        });
        
        // Button listeners
        createRentalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createRental();
            }
        });
        
        completeRentalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                completeRental();
            }
        });
        
        deleteRentalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRental();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRentals();
                loadAvailableCars();
                loadCustomers();
            }
        });
    }
    
    private void loadRentals() {
        new BackgroundTask<List<Rental>, Void>("Load Rentals") {
            @Override
            protected List<Rental> executeInBackground() throws Exception {
                return rentalService.getAllRentals();
            }
            
            @Override
            protected void onSuccess(List<Rental> rentals) {
                // Clear table
                tableModel.setRowCount(0);
                
                // Add rentals to table
                for (Rental rental : rentals) {
                    Car car = carService.getCarById(rental.getCarId());
                    Customer customer = customerService.getCustomerById(rental.getCustomerId());
                    
                    Object[] row = {
                        rental.getId(),
                        car != null ? car.toString() : "Unknown Car",
                        customer != null ? customer.toString() : "Unknown Customer",
                        rental.getStartDate(),
                        rental.getEndDate(),
                        rental.getTotalCost(),
                        rental.getStatus()
                    };
                    tableModel.addRow(row);
                }
            }
        }.execute();
    }
    
    private void loadAvailableCars() {
        new BackgroundTask<List<Car>, Void>("Load Available Cars") {
            @Override
            protected List<Car> executeInBackground() throws Exception {
                return carService.getAvailableCars();
            }
            
            @Override
            protected void onSuccess(List<Car> cars) {
                // Clear combo box
                carComboBox.removeAllItems();
                
                // Add cars to combo box
                for (Car car : cars) {
                    carComboBox.addItem(car);
                }
            }
        }.execute();
    }
    
    private void loadCustomers() {
        new BackgroundTask<List<Customer>, Void>("Load Customers") {
            @Override
            protected List<Customer> executeInBackground() throws Exception {
                return customerService.getAllCustomers();
            }
            
            @Override
            protected void onSuccess(List<Customer> customers) {
                // Clear combo box
                customerComboBox.removeAllItems();
                
                // Add customers to combo box
                for (Customer customer : customers) {
                    customerComboBox.addItem(customer);
                }
            }
        }.execute();
    }
    
    private void createRental() {
        try {
            Car selectedCar = (Car) carComboBox.getSelectedItem();
            Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
            
            if (selectedCar == null) {
                JOptionPane.showMessageDialog(this, 
                        "Please select a car", 
                        "Selection Error", 
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (selectedCustomer == null) {
                JOptionPane.showMessageDialog(this, 
                        "Please select a customer", 
                        "Selection Error", 
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse dates
            int startDay = Integer.parseInt((String) startDayCombo.getSelectedItem());
            int startMonth = startMonthCombo.getSelectedIndex() + 1;
            int startYear = Integer.parseInt((String) startYearCombo.getSelectedItem());
            
            int endDay = Integer.parseInt((String) endDayCombo.getSelectedItem());
            int endMonth = endMonthCombo.getSelectedIndex() + 1;
            int endYear = Integer.parseInt((String) endYearCombo.getSelectedItem());
            
            LocalDate startDate = LocalDate.of(startYear, startMonth, startDay);
            LocalDate endDate = LocalDate.of(endYear, endMonth, endDay);
            
            new BackgroundTask<Boolean, Void>("Create Rental") {
                @Override
                protected Boolean executeInBackground() throws Exception {
                    return rentalService.createRental(
                            selectedCar.getId(), 
                            selectedCustomer.getId(), 
                            startDate, 
                            endDate);
                }
                
                @Override
                protected void onSuccess(Boolean result) {
                    if (result) {
                        JOptionPane.showMessageDialog(RentalManagementPanel.this, 
                                "Rental created successfully", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        loadRentals();
                        loadAvailableCars();
                        FileManager.logAction("Created rental for car: " + selectedCar.toString() + 
                                " and customer: " + selectedCustomer.toString());
                    } else {
                        JOptionPane.showMessageDialog(RentalManagementPanel.this, 
                                "Failed to create rental", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                    "Invalid date format", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void completeRental() {
        if (selectedRentalId == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a rental to complete", 
                    "Selection Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        new BackgroundTask<Boolean, Void>("Complete Rental") {
            @Override
            protected Boolean executeInBackground() throws Exception {
                return rentalService.completeRental(selectedRentalId);
            }
            
            @Override
            protected void onSuccess(Boolean result) {
                if (result) {
                    JOptionPane.showMessageDialog(RentalManagementPanel.this, 
                            "Rental completed successfully", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    loadRentals();
                    loadAvailableCars();
                    FileManager.logAction("Completed rental with ID: " + selectedRentalId);
                } else {
                    JOptionPane.showMessageDialog(RentalManagementPanel.this, 
                            "Failed to complete rental", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    
    private void deleteRental() {
        if (selectedRentalId == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a rental to delete", 
                    "Selection Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this rental?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new BackgroundTask<Boolean, Void>("Delete Rental") {
                @Override
                protected Boolean executeInBackground() throws Exception {
                    return rentalService.deleteRental(selectedRentalId);
                }
                
                @Override
                protected void onSuccess(Boolean result) {
                    if (result) {
                        JOptionPane.showMessageDialog(RentalManagementPanel.this, 
                                "Rental deleted successfully", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        loadRentals();
                        loadAvailableCars();
                        FileManager.logAction("Deleted rental with ID: " + selectedRentalId);
                    } else {
                        JOptionPane.showMessageDialog(RentalManagementPanel.this, 
                                "Failed to delete rental", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
    
    public JTable getRentalsTable() {
        return rentalsTable;
    }
}