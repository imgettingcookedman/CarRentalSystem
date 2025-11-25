package carrentalsystem.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AutoRefreshService {
    private final ScheduledExecutorService scheduler;
    private boolean isRunning;
    
    public AutoRefreshService() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.isRunning = false;
    }
    
    public void startAutoRefresh(JTable table, int intervalSeconds) {
        if (isRunning) {
            stopAutoRefresh();
        }
        
        isRunning = true;
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Refresh table data
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.fireTableDataChanged();
                FileManager.logAction("Auto-refreshed table data");
            } catch (Exception e) {
                System.err.println("Error during auto-refresh: " + e.getMessage());
                FileManager.logAction("Error during auto-refresh: " + e.getMessage());
            }
        }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
        
        FileManager.logAction("Started auto-refresh with interval: " + intervalSeconds + " seconds");
    }
    
    public void stopAutoRefresh() {
        if (isRunning) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            isRunning = false;
            FileManager.logAction("Stopped auto-refresh");
        }
    }
    
    public boolean isRunning() {
        return isRunning;
    }
}