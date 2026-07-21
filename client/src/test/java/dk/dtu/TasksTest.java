package dk.dtu;

import dk.dtu.methods.Tasks;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Tasks input validation.
 * No backend required.
 */
public class TasksTest {

    @Test
    public void testAddTaskRejectsNullTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            Tasks.addTask("req", "resp", "list1", null, "2026-01-01", "alice");
        });
    }

    @Test
    public void testAddTaskRejectsBlankTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            Tasks.addTask("req", "resp", "list1", "   ", "2026-01-01", "alice");
        });
    }

    @Test
    public void testAssignTaskRejectsNullOwner() {
        assertThrows(IllegalArgumentException.class, () -> {
            Tasks.assignTask("req", "resp", "list1", "task1", null);
        });
    }

    @Test
    public void testAssignTaskRejectsBlankOwner() {
        assertThrows(IllegalArgumentException.class, () -> {
            Tasks.assignTask("req", "resp", "list1", "task1", "   ");
        });
    }

    @Test
    public void testDeleteTaskRejectsNullTaskId() {
        assertThrows(IllegalArgumentException.class, () -> {
            Tasks.deleteTask("req", "resp", null);
        });
    }

    @Test
    public void testDeleteTaskRejectsBlankTaskId() {
        assertThrows(IllegalArgumentException.class, () -> {
            Tasks.deleteTask("req", "resp", "   ");
        });
    }

    // setTaskYear now hits the API; its id guards still run before any network call.

    @Test
    public void testSetTaskYearRejectsBlankListId() {
        assertThrows(IllegalArgumentException.class, () -> {
            Tasks.setTaskYear("req", "resp", "   ", "task1", 2027);
        });
    }

    @Test
    public void testSetTaskYearRejectsNullTaskId() {
        assertThrows(IllegalArgumentException.class, () -> {
            Tasks.setTaskYear("req", "resp", "list1", null, 2027);
        });
    }
}

