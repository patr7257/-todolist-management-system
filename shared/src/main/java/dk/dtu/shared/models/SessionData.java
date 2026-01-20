package dk.dtu.shared.models;

import java.util.ArrayList;
import java.util.List;

// Container for all persistent session data
public class SessionData {
    private List<UserData> users = new ArrayList<>();
    private List<TodoListData> todoLists = new ArrayList<>();
    private List<TaskData> tasks = new ArrayList<>();
    private long lastSaved;

    public SessionData() {
    }

    public List<UserData> getUsers() {
        return users;
    }

    public void setUsers(List<UserData> users) {
        this.users = users;
    }

    public List<TodoListData> getTodoLists() {
        return todoLists;
    }

    public void setTodoLists(List<TodoListData> todoLists) {
        this.todoLists = todoLists;
    }

    public List<TaskData> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskData> tasks) {
        this.tasks = tasks;
    }

    public long getLastSaved() {
        return lastSaved;
    }

    public void setLastSaved(long lastSaved) {
        this.lastSaved = lastSaved;
    }
}
