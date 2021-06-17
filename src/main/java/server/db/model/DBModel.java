package server.db.model;

import java.time.LocalDateTime;

public abstract class DBModel {
    public int id;
    public boolean isDeleted;
    public LocalDateTime createdAt;
    public LocalDateTime lastModified;

    public DBModel() {
        isDeleted = false;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}