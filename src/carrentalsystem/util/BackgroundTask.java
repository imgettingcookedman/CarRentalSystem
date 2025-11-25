package carrentalsystem.util;

import javax.swing.SwingWorker;

public abstract class BackgroundTask<T, V> extends SwingWorker<T, V> {
    private final String taskName;
    
    public BackgroundTask(String taskName) {
        this.taskName = taskName;
    }
    
    @Override
    protected final T doInBackground() throws Exception {
        FileManager.logAction("Started background task: " + taskName);
        try {
            return executeInBackground();
        } finally {
            FileManager.logAction("Completed background task: " + taskName);
        }
    }
    
    protected abstract T executeInBackground() throws Exception;
    
    @Override
    protected void done() {
        try {
            T result = get();
            onSuccess(result);
        } catch (Exception e) {
            onError(e);
        }
    }
    
    protected abstract void onSuccess(T result);
    
    protected void onError(Exception e) {
        System.err.println("Error in background task " + taskName + ": " + e.getMessage());
        FileManager.logAction("Error in background task " + taskName + ": " + e.getMessage());
    }
}