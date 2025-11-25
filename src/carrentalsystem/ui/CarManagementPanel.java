package carrentalsystem.ui;

import carrentalsystem.model.Car;
import carrentalsystem.service.CarService;
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
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class CarManagementPanel extends JPanel {
    private final CarService carService;
    private JTable carsTable;
    private DefaultTableModel tableModel;
    private JTextField makeField;
    private JTextField modelField;
    private JTextField yearField;
    private JTextField licensePlateField;
    private JTextField dailyRateField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private int selectedCarId = -1;
    
    public CarManagementPanel() {
        carService = new CarService();
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        loadCars();
    }
    
    private void initializeComponents() {
        // Initialize table
        String[] columnNames = {"ID", "Make", "Model", "Year", "License Plate", "Daily Rate", "Available"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        carsTable = new JTable(tableModel);
        
        // Initialize form fields
        makeField = new JTextField(20);
        modelField = new JTextField(20);
        yearField = new JTextField(20);
        licensePlateField = new JTextField(20);
        dailyRateField = new JTextField(20);
        
        // Initialize buttons
        addButton = new JButton("Add Car");
        updateButton = new JButton("Update Car");
        deleteButton = new JButton("Delete Car");
        refreshButton = new JButton("Refresh");
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(carsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Add form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new javax.swing.JLabel("Make:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(makeField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new javax.swing.JLabel("Model:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(modelField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new javax.swing.JLabel("Year:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(yearField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new javax.swing.JLabel("License Plate:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(licensePlateField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new javax.swing.JLabel("Daily Rate:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(dailyRateField, gbc);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);
        
        // Add form panel to frame
        add(formPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Table selection listener
        carsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = carsTable.getSelectedRow();
                if (row >= 0) {
                    selectedCarId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    makeField.setText(tableModel.getValueAt(row, 1).toString());
                    modelField.setText(tableModel.getValueAt(row, 2).toString());
                    yearField.setText(tableModel.getValueAt(row, 3).toString());
                    licensePlateField.setText(tableModel.getValueAt(row, 4).toString());
                    dailyRateField.setText(tableModel.getValueAt(row, 5).toString());
                }
            }
        });
        
        // Button listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCar();
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCar();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCar();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCars();
            }
        });
    }
    
    private void loadCars() {
        new BackgroundTask<List<Car>, Void>("Load Cars") {
            @Override
            protected List<Car> executeInBackground() throws Exception {
                return carService.getAllCars();
            }
            
            @Override
            protected void onSuccess(List<Car> cars) {
                // Clear table
                tableModel.setRowCount(0);
                
                // Add cars to table
                for (Car car : cars) {
                    Object[] row = {
                        car.getId(),
                        car.getMake(),
                        car.getModel(),
                        car.getYear(),
                        car.getLicensePlate(),
                        car.getDailyRate(),
                        car.isAvailable() ? "Yes" : "No"
                    };
                    tableModel.addRow(row);
                }
            }
        }.execute();
    }
    
    private void addCar() {
        try {
            String make = makeField.getText().trim();
            String model = modelField.getText().trim();
            int year = Integer.parseInt(yearField.getText().trim());
            String licensePlate = licensePlateField.getText().trim();
            double dailyRate = Double.parseDouble(dailyRateField.getText().trim());
            
            Car car = new Car(make, model, year, licensePlate, dailyRate, true);
            
            new BackgroundTask<Boolean, Void>("Add Car") {
                @Override
                protected Boolean executeInBackground() throws Exception {
                    return carService.addCar(car);
                }
                
                @Override
                protected void onSuccess(Boolean result) {
                    if (result) {
                        JOptionPane.showMessageDialog(CarManagementPanel.this, 
                                "Car added successfully", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        clearForm();
                        loadCars();
                        FileManager.logAction("Added new car: " + make + " " + model);
                    } else {
                        JOptionPane.showMessageDialog(CarManagementPanel.this, 
                                "Failed to add car", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                    "Please enter valid numeric values for year and daily rate", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateCar() {
        if (selectedCarId == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a car to update", 
                    "Selection Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String make = makeField.getText().trim();
            String model = modelField.getText().trim();
            int year = Integer.parseInt(yearField.getText().trim());
            String licensePlate = licensePlateField.getText().trim();
            double dailyRate = Double.parseDouble(dailyRateField.getText().trim());
            
            Car car = carService.getCarById(selectedCarId);
            if (car != null) {
                car.setMake(make);
                car.setModel(model);
                car.setYear(year);
                car.setLicensePlate(licensePlate);
                car.setDailyRate(dailyRate);
                
                new BackgroundTask<Boolean, Void>("Update Car") {
                    @Override
                    protected Boolean executeInBackground() throws Exception {
                        return carService.updateCar(car);
                    }
                    
                    @Override
                    protected void onSuccess(Boolean result) {
                        if (result) {
                            JOptionPane.showMessageDialog(CarManagementPanel.this, 
                                    "Car updated successfully", 
                                    "Success", 
                                    JOptionPane.INFORMATION_MESSAGE);
                            clearForm();
                            loadCars();
                            FileManager.logAction("Updated car: " + make + " " + model);
                        } else {
                            JOptionPane.showMessageDialog(CarManagementPanel.this, 
                                    "Failed to update car", 
                                    "Error", 
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }.execute();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                    "Please enter valid numeric values for year and daily rate", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteCar() {
        if (selectedCarId == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a car to delete", 
                    "Selection Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this car?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new BackgroundTask<Boolean, Void>("Delete Car") {
                @Override
                protected Boolean executeInBackground() throws Exception {
                    return carService.deleteCar(selectedCarId);
                }
                
                @Override
                protected void onSuccess(Boolean result) {
                    if (result) {
                        JOptionPane.showMessageDialog(CarManagementPanel.this, 
                                "Car deleted successfully", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        clearForm();
                        loadCars();
                        FileManager.logAction("Deleted car with ID: " + selectedCarId);
                    } else {
                        JOptionPane.showMessageDialog(CarManagementPanel.this, 
                                "Failed to delete car", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
    
    private void clearForm() {
        makeField.setText("");
        modelField.setText("");
        yearField.setText("");
        licensePlateField.setText("");
        dailyRateField.setText("");
        selectedCarId = -1;
    }
}