package dk.dtu.model;

import java.io.Serializable;

// Class for Tasks / To-Do Items
public final class Task implements Serializable {
        private final String id;
        private final String title;
        private final String owner;
        private final TaskStatus status;

        public Task(String id, String title, String owner, TaskStatus status) {
                this.id = id;
                this.title = title;
                this.owner = owner;
                this.status = status;
        }

        public String getId() {
                return id;
        }

        public String getTitle() {
                return title;
        }

        public String getOwner() {
                return owner;
        }

        public TaskStatus getStatus() {
                return status;
        }
}