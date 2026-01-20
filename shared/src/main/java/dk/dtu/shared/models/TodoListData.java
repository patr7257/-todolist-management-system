package dk.dtu.shared.models;

// Model for persisting todo list data
public class TodoListData {
    private String listId;
    private String listName;
    private int completionPercentage;

    public TodoListData() {
    }

    public TodoListData(String listId, String listName, int completionPercentage) {
        this.listId = listId;
        this.listName = listName;
        this.completionPercentage = completionPercentage;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
}
