package carrentalsystem.service;

import carrentalsystem.dao.CustomerDAO;
import carrentalsystem.model.Customer;
import java.util.List;

public class CustomerService {
    private final CustomerDAO customerDAO;
    
    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }
    
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }
    
    public Customer getCustomerById(int id) {
        return customerDAO.getCustomerById(id);
    }
    
    public boolean addCustomer(Customer customer) {
        // Validate customer data
        if (customer.getName() == null || customer.getName().isEmpty()) {
            System.err.println("Customer name cannot be empty");
            return false;
        }
        
        if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
            System.err.println("Customer email cannot be empty");
            return false;
        }
        
        if (customer.getPhone() == null || customer.getPhone().isEmpty()) {
            System.err.println("Customer phone cannot be empty");
            return false;
        }
        
        return customerDAO.addCustomer(customer);
    }
    
    public boolean updateCustomer(Customer customer) {
        // Validate customer data
        if (customer.getName() == null || customer.getName().isEmpty()) {
            System.err.println("Customer name cannot be empty");
            return false;
        }
        
        if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
            System.err.println("Customer email cannot be empty");
            return false;
        }
        
        if (customer.getPhone() == null || customer.getPhone().isEmpty()) {
            System.err.println("Customer phone cannot be empty");
            return false;
        }
        
        return customerDAO.updateCustomer(customer);
    }
    
    public boolean deleteCustomer(int id) {
        // Check if customer exists
        Customer customer = customerDAO.getCustomerById(id);
        if (customer == null) {
            System.err.println("Customer not found");
            return false;
        }
        
        return customerDAO.deleteCustomer(id);
    }
}