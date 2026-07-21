package dk.dtu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import dk.dtu.methods.Lists;

/**
 * Unit tests for Lists input validation.
 */
public class ListsTest {
    
    @Test
    public void testCreateTodoListRejectsEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            Lists.createTodoList(
                    "jndi://requests",
                    "jndi://responses",
                    "   ",  // invalid (blank)
                    "alice"
            );
        });
    }

    @Test
    public void testCreateTodoListRejectsNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            Lists.createTodoList(
                    "jndi://requests",
                    "jndi://responses",
                    null,   // invalid
                    "alice"
            );
        });
    }

    @Test
    public void testDeleteTodoListRejectsEmptyId() {
        assertThrows(IllegalArgumentException.class, () -> {
            Lists.deleteTodoList(
                    "jndi://requests",
                    "jndi://responses",
                    ""     // invalid
            );
        });
    }

    @Test
    public void testDeleteTodoListRejectsNullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            Lists.deleteTodoList(
                    "jndi://requests",
                    "jndi://responses",
                    null   // invalid
            );
        });
    }

    // The desktop-superset setters guard their inputs before any network call.

    @Test
    public void testSetListOwnerRejectsBlankOwner() {
        assertThrows(IllegalArgumentException.class, () -> {
            Lists.setListOwner("req", "resp", "list1", "   ");
        });
    }

    @Test
    public void testSetListPriorityRejectsBlankId() {
        assertThrows(IllegalArgumentException.class, () -> {
            Lists.setListPriority("req", "resp", "   ", 1);
        });
    }

    @Test
    public void testSetListYearRejectsNullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            Lists.setListYear("req", "resp", null, 2027);
        });
    }

    @Test
    public void testSetListDescriptionRejectsBlankId() {
        assertThrows(IllegalArgumentException.class, () -> {
            Lists.setListDescription("req", "resp", "", "some notes");
        });
    }

    @Test
    public void testClearListOwnerRejectsNullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            Lists.clearListOwner("req", "resp", null);
        });
    }
}
