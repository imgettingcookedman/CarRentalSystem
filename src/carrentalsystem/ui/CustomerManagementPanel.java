package carrentalsystem.ui;

import carrentalsystem.model.Customer;
import carrentalsystem.service.CustomerService;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class CustomerManagementPanel extends JPanel {
    private final CustomerService customerService;
    private JTable customersTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private int selectedCustomerId = -1;
    
    public CustomerManagementPanel() {
        customerService = new CustomerService();
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        loadCustomers();
    }
    
    private void initializeComponents() {
        // Initialize table
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customersTable = new JTable(tableModel);
        
        // Initialize form fields
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        addressField = new JTextArea(3, 20);
        
        // Initialize buttons
        addButton = new JButton("Add Customer");
        updateButton = new JButton("Update Customer");
        deleteButton = new JButton("Delete Customer");
        refreshButton = new JButton("Refresh");
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(customersTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Add form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new javax.swing.JLabel("Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new javax.swing.JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new javax.swing.JLabel("Phone:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(phoneField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new javax.swing.JLabel("Address:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(new JScrollPane(addressField), gbc);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
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
        customersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = customersTable.getSelectedRow();
                if (row >= 0) {
                    selectedCustomerId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    nameField.setText(tableModel.getValueAt(row, 1).toString());
                    emailField.setText(tableModel.getValueAt(row, 2).toString());
                    phoneField.setText(tableModel.getValueAt(row, 3).toString());
                    addressField.setText(tableModel.getValueAt(row, 4).toString());
                }
            }
        });
        
        // Button listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCustomer();
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCustomer();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCustomer();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCustomers();
            }
        });
    }
    
    private void loadCustomers() {
        new BackgroundTask<List<Customer>, Void>("Load Customers") {
            @Override
            protected List<Customer> executeInBackground() throws Exception {
                return customerService.getAllCustomers();
            }
            
            @Override
            protected void onSuccess(List<Customer> customers) {
                // Clear table
                tableModel.setRowCount(0);
                
                // Add customers to table
                for (Customer customer : customers) {
                    Object[] row = {
                        customer.getId(),
                        customer.getName(),
                        customer.getEmail(),
                        customer.getPhone(),
                        customer.getAddress()
                    };
                    tableModel.addRow(row);
                }
            }
        }.execute();
    }
    
    private void addCustomer() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        
        Customer customer = new Customer(name, email, phone, address);
        
        new BackgroundTask<Boolean, Void>("Add Customer") {
            @Override
            protected Boolean executeInBackground() throws Exception {
                return customerService.addCustomer(customer);
            }
            
            @Override
            protected void onSuccess(Boolean result) {
                if (result) {
                    JOptionPane.showMessageDialog(CustomerManagementPanel.this, 
                            "Customer added successfully", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadCustomers();
                    FileManager.logAction("Added new customer: " + name);
                } else {
                    JOptionPane.showMessageDialog(CustomerManagementPanel.this, 
                            "Failed to add customer", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    
    private void updateCustomer() {
        if (selectedCustomerId == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a customer to update", 
                    "Selection Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        
        Customer customer = customerService.getCustomerById(selectedCustomerId);
        if (customer != null) {
            customer.setName(name);
            customer.setEmail(email);
            customer.setPhone(phone);
            customer.setAddress(address);
            
            new BackgroundTask<Boolean, Void>("Update Customer") {
                @Override
                protected Boolean executeInBackground() throws Exception {
                    return customerService.updateCustomer(customer);
                }
                
                @Override
                protected void onSuccess(Boolean result) {
                    if (result) {
                        JOptionPane.showMessageDialog(CustomerManagementPanel.this, 
                                "Customer updated successfully", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        clearForm();
                        loadCustomers();
                        FileManager.logAction("Updated customer: " + name);
                    } else {
                        JOptionPane.showMessageDialog(CustomerManagementPanel.this, 
                                "Failed to update customer", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
    
    private void deleteCustomer() {
        if (selectedCustomerId == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a customer to delete", 
                    "Selection Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this customer?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new BackgroundTask<Boolean, Void>("Delete Customer") {
                @Override
                protected Boolean executeInBackground() throws Exception {
                    return customerService.deleteCustomer(selectedCustomerId);
                }
                
                @Override
                protected void onSuccess(Boolean result) {
                    if (result) {
                        JOptionPane.showMessageDialog(CustomerManagementPanel.this, 
                                "Customer deleted successfully", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        clearForm();
                        loadCustomers();
                        FileManager.logAction("Deleted customer with ID: " + selectedCustomerId);
                    } else {
                        JOptionPane.showMessageDialog(CustomerManagementPanel.this, 
                                "Failed to delete customer", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
    
    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        selectedCustomerId = -1;
    }
}