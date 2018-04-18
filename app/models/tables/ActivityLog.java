package models.tables;

import models.BaseModel;
import models.helpers.ActivityType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activity_log")
public class ActivityLog extends BaseModel {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "activity_type")
    private ActivityType activityType;

    @Column(name = "description")
    private String description;

    public ActivityLog() {
    }

    public ActivityLog(ActivityType type, String description) {
        this.id = UUID.randomUUID();
        this.timestamp = Timestamp.valueOf(LocalDateTime.now());
        this.activityType = type;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
