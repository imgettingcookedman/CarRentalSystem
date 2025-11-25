package carrentalsystem.ui;

import carrentalsystem.util.FileManager;
import carrentalsystem.util.AutoRefreshService;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {
    private final JTabbedPane tabbedPane;
    private CarManagementPanel carManagementPanel;
    private CustomerManagementPanel customerManagementPanel;
    private RentalManagementPanel rentalManagementPanel;
    private ReportsPanel reportsPanel;
    private AutoRefreshService autoRefreshService;
    
    public MainFrame() {
        setTitle("Car Rental Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize auto-refresh service
        autoRefreshService = new AutoRefreshService();
        
        // Create menu bar
        createMenuBar();
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Add panels
        carManagementPanel = new CarManagementPanel();
        customerManagementPanel = new CustomerManagementPanel();
        rentalManagementPanel = new RentalManagementPanel();
        reportsPanel = new ReportsPanel();
        
        tabbedPane.addTab("Cars", carManagementPanel);
        tabbedPane.addTab("Customers", customerManagementPanel);
        tabbedPane.addTab("Rentals", rentalManagementPanel);
        tabbedPane.addTab("Reports", reportsPanel);
        
        // Add tabbed pane to frame
        add(tabbedPane, BorderLayout.CENTER);
        
        // Add status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setPreferredSize(new Dimension(getWidth(), 30));
        add(statusPanel, BorderLayout.SOUTH);
        
        // Log application start
        FileManager.logAction("Application started");
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitApplication();
            }
        });
        fileMenu.add(exitItem);
        
        // Export menu
        JMenu exportMenu = new JMenu("Export");
        
        JMenuItem exportCarsItem = new JMenuItem("Export Cars to CSV");
        exportCarsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filename = "cars_" + System.currentTimeMillis() + ".csv";
                if (FileManager.exportCarsToCSV(filename)) {
                    JOptionPane.showMessageDialog(MainFrame.this, 
                            "Cars exported successfully to " + filename, 
                            "Export Successful", 
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, 
                            "Failed to export cars", 
                            "Export Failed", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        exportMenu.add(exportCarsItem);
        
        JMenuItem exportCustomersItem = new JMenuItem("Export Customers to CSV");
        exportCustomersItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filename = "customers_" + System.currentTimeMillis() + ".csv";
                if (FileManager.exportCustomersToCSV(filename)) {
                    JOptionPane.showMessageDialog(MainFrame.this, 
                            "Customers exported successfully to " + filename, 
                            "Export Successful", 
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, 
                            "Failed to export customers", 
                            "Export Failed", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        exportMenu.add(exportCustomersItem);
        
        JMenuItem exportRentalsItem = new JMenuItem("Export Rentals to CSV");
        exportRentalsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filename = "rentals_" + System.currentTimeMillis() + ".csv";
                if (FileManager.exportRentalsToCSV(filename)) {
                    JOptionPane.showMessageDialog(MainFrame.this, 
                            "Rentals exported successfully to " + filename, 
                            "Export Successful", 
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, 
                            "Failed to export rentals", 
                            "Export Failed", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        exportMenu.add(exportRentalsItem);
        
        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");
        
        JMenuItem autoRefreshItem = new JMenuItem("Toggle Auto-Refresh");
        autoRefreshItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (autoRefreshService.isRunning()) {
                    autoRefreshService.stopAutoRefresh();
                    JOptionPane.showMessageDialog(MainFrame.this, 
                            "Auto-refresh stopped", 
                            "Auto-Refresh", 
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String input = JOptionPane.showInputDialog(
                            MainFrame.this, 
                            "Enter refresh interval in seconds:", 
                            "Auto-Refresh Settings", 
                            JOptionPane.QUESTION_MESSAGE);
                    
                    try {
                        int interval = Integer.parseInt(input);
                        if (interval > 0) {
                            autoRefreshService.startAutoRefresh(
                                    rentalManagementPanel.getRentalsTable(), 
                                    interval);
                            JOptionPane.showMessageDialog(MainFrame.this, 
                                    "Auto-refresh started with interval: " + interval + " seconds", 
                                    "Auto-Refresh", 
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(MainFrame.this, 
                                    "Interval must be greater than 0", 
                                    "Invalid Input", 
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(MainFrame.this, 
                                "Please enter a valid number", 
                                "Invalid Input", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        toolsMenu.add(autoRefreshItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainFrame.this, 
                        "Car Rental Management System\nVersion 1.0\n\nA Java Swing application for managing car rentals.", 
                        "About", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        helpMenu.add(aboutItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(exportMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        // Set menu bar
        setJMenuBar(menuBar);
    }
    
    private void exitApplication() {
        // Stop auto-refresh if running
        if (autoRefreshService.isRunning()) {
            autoRefreshService.stopAutoRefresh();
        }
        
        // Log application exit
        FileManager.logAction("Application exited");
        
        // Dispose of the frame
        dispose();
    }
}