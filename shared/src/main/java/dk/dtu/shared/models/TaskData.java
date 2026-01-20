package dk.dtu.shared.models;

// Model for persisting task data
public class TaskData {
    private String listId;
    private String taskId;
    private String title;
    private String assignee;
    private String status;
    private String dueDate;

    public TaskData() {
    }

    public TaskData(String listId, String taskId, String title, String assignee, String status, String dueDate) {
        this.listId = listId;
        this.taskId = taskId;
        this.title = title;
        this.assignee = assignee;
        this.status = status;
        this.dueDate = dueDate;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
