package dk.dtu.shared.models;

// Model for persisting user data
public class UserData {
    private String username;

    public UserData() {
    }

    public UserData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
