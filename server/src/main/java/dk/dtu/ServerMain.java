package dk.dtu;

/**
 * Main server application
 * - Create a jSpace SpaceRepository
 * - Add spaces for requests/responses and shared state
 * - Add a TCP gate so JavaFX clients can connect via RemoteSpace
 * - Start one or more service loops (one for each connected client) (TaskManagerService)
 */
public class ServerMain {

    public static void main(String[] args) {
        System.out.println("Hello World! Server started!");
        // TODO: Implement server startup (SpaceRepository + gates + spaces).
    }
}
