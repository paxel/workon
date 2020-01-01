package paxel.workon.db.impl;

/**
 *
 */
public class Activity {

    private final String id;
    private final String description;
    private long duration;

    public Activity(String id, String activity) {
        this.id = id;
        this.description = activity;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public long getDuration() {
        return duration;
    }
}
