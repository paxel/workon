package paxel.workon.db.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import paxel.workon.db.DataBase;

public class DataBaseImpl implements DataBase {

    private final LinkedList<Activity> stack = new LinkedList<>();
    private final Map<String, Activity> lookup = new HashMap<>();
    private final List<Activity> finished = new LinkedList<>();
    private Activity current;
    private Long start;

    /**
     * Start the activity with given ID.
     * The current activity is put on the stack.
     * If the given ID does not exist, nothing else happens and the result is false.
     * Otherwise the activity with given ID is set as current and the result is true.
     *
     * @param id the ID of the activity to start.
     *
     * @return {@code true} if the ID existed.
     */
    public boolean start(String id) {
        pushCurrentOnStack();
        Activity previous = lookup.get(id);
        if (previous == null) {
            return false;
        }
        startPrevious(previous, previous.getDescription(), id);
        return true;
    }

    /**
     * Start the previous activity.
     * The current activity is put on the stack.
     * If there was no previous activity, nothing else happens and the result is false.
     * Otherwise the previous activity is set as current and the result is true.
     *
     * @return {@code true} if the ID existed.
     */
    public boolean startPrevious() {
        // first remove previous from stack before the current is pushed to stack
        Activity previous = stack.pollFirst();
        pushCurrentOnStack();
        if (previous == null) {
            return false;
        }
        startPrevious(previous, previous.getDescription(), previous.getId());
        return true;
    }

    public void start(String id, String description) {
        pushCurrentOnStack();
        Activity previous = lookup.get(id);
        if (previous != null) {
            startPrevious(previous, description, id);
        } else {
            // new activity
            activateNewActivity(id, description);
        }
    }

    public boolean stop() {
        if (current != null) {
            finishActivity(current);
            addDuration(current);
            current = null;
            return true;
        }
        return false;
    }

    public Activity getCurrent() {
        return current;
    }

    public List<Activity> getActivityStack() {
        return stack;
    }

    public List<Activity> getFinishedActivities() {
        return finished;
    }

    public void startPrevious(Activity previous, String description, String id) {
        // there is already an activity with that ID. so we remove it from the stack
        stack.remove(previous);
        if (previous.getDescription().equals(description)) {
            activateActivity(previous);
        } else {
            // the previous activity is moved to finished and we create a new one
            finishActivity(previous);
            activateNewActivity(id, description);
        }
    }

    private void activateActivity(Activity activity) {
        // we activate this activity
        current = activity;
        start = System.currentTimeMillis();
    }

    private void pushCurrentOnStack() {
        // put current on the stack.
        if (current != null) {
            stack.addFirst(current);
            addDuration(current);
            current = null;

        }
    }

    private void addDuration(Activity activity) {
        long duration = System.currentTimeMillis() - start;
        activity.addDurationInMillis(duration);
        start = null;
    }

    private void activateNewActivity(String id, String description) {
        final Activity activity = new Activity(id, description);
        activateActivity(activity);
        lookup.put(id, activity);
    }

    private void finishActivity(Activity activity) {
        finished.add(activity);
        lookup.remove(activity.getId());
    }

    // ***************************************************
    // serialisation
    // ***************************************************
    LinkedList<Activity> getStack() {
        return stack;
    }

    Map<String, Activity> getLookup() {
        return lookup;
    }

    List<Activity> getFinished() {
        return finished;
    }

    Long getStart() {
        return start;
    }

    void setStart(Long start) {
        this.start = start;
    }

    void setCurrent(Activity current) {
        this.current = current;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.stack);
        hash = 11 * hash + Objects.hashCode(this.lookup);
        hash = 11 * hash + Objects.hashCode(this.finished);
        hash = 11 * hash + Objects.hashCode(this.current);
        hash = 11 * hash + Objects.hashCode(this.start);
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
        final DataBaseImpl other = (DataBaseImpl) obj;
        if (!Objects.equals(this.stack, other.stack)) {
            return false;
        }
        if (!Objects.equals(this.lookup, other.lookup)) {
            return false;
        }
        if (!Objects.equals(this.finished, other.finished)) {
            return false;
        }
        if (!Objects.equals(this.current, other.current)) {
            return false;
        }
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DataBaseImpl{" + "stack=" + stack + ", lookup=" + lookup + ", finished=" + finished + ", current=" + current + ", start=" + start + '}';
    }

    @Override
    public void clearFinishedActivities() {
        finished.clear();
    }

}
