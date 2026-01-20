package dk.dtu.shared;

import java.io.File;

// Shared configuration class for server and client
public final class Config {
    
    // SERVER CONFIGURATION
    // public static final String SERVER_IP = "127.0.0.1";
    
    // Home network IP
    public static final String SERVER_IP = "192.168.0.168";
    
    public static final int PORT = 9001;                        // Port for jSpace server
    public static final String SERVER_BIND_HOST = "0.0.0.0";    // Bind to all interfaces
    
    // DATA PERSISTENCE CONFIGURATION
    // Default data directory in user home folder (works for both development and MSI installations)
    // Can be overridden via system property: -Dtodolist.data.dir=/custom/path
    public static final String DEFAULT_DATA_DIR = System.getProperty("user.home") + File.separator + ".todolist-data";
    
    /**
     * Get the data directory path for storing session files.
     * Checks system property first, then falls back to default.
     * @return Path to data directory
     */
    public static String getDataDirectory() {
        return System.getProperty("todolist.data.dir", DEFAULT_DATA_DIR);
    }
    
    // Server gate URI for jSpace
    public static String getServerGateUri() {
        return "tcp://" + SERVER_BIND_HOST + ":" + PORT + "/?keep";
    }
    
    // CLIENT CONFIGURATION
    // Base URI for client connections
    public static String getClientBaseUri() {
        return "tcp://" + SERVER_IP + ":" + PORT + "/";
    }
    
    // Client URI for todoLists space
    public static String getTodoListsUri() {
        return getClientBaseUri() + "todoLists?keep";
    }
    
    // Client URI for users space
    public static String getUsersUri() {
        return getClientBaseUri() + TupleSpaces.USERS + "?keep";
    }
    
    // Client URI for tasks space
    public static String getTasksUri() {
        return getClientBaseUri() + TupleSpaces.TASKS + "?keep";
    }
    
    // Client URI for requests space
    public static String getRequestsUri() {
        return getClientBaseUri() + TupleSpaces.REQUESTS + "?keep";
    }
    
    // Client URI for responses space
    public static String getResponsesUri() {
        return getClientBaseUri() + TupleSpaces.RESPONSES + "?keep";
    }
    
    // Client URI for notifications space
    public static String getNotificationsUri() {
        return getClientBaseUri() + TupleSpaces.NOTIFICATIONS + "?keep";
    }
    
    // Private constructor to prevent instantiation
    private Config() {}
}
