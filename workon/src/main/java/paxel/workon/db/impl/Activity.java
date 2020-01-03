package paxel.workon.db.impl;

import java.util.Objects;

/**
 *
 */
public class Activity {

    private final String id;
    private final String description;
    private long durationInMillis;

    public Activity(String id, String activity) {
        this.id = id;
        this.description = activity;
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.id);
        hash = 47 * hash + Objects.hashCode(this.description);
        hash = 47 * hash + (int) (this.durationInMillis ^ (this.durationInMillis >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Activity other = (Activity) obj;
        if (this.durationInMillis != other.durationInMillis) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Activity{" + "id=" + id + ", description=" + description + ", duration=" + durationInMillis + '}';
    }

    public String getDescription() {
        return description;
    }

    public long getDurationInMillis() {
        return durationInMillis;
    }

    void addDurationInMillis(long duration) {
        this.durationInMillis += duration;
    }
}
