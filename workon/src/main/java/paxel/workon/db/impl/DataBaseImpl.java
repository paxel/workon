package paxel.workon.db.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import paxel.workon.db.DataBase;

public class DataBaseImpl implements DataBase {

    private Activity current;

    private final List<Activity> stack = new LinkedList<>();
    private final Map<String, Activity> lookup = new HashMap<>();
    private final List<Activity> finished = new LinkedList<>();

    void start(String id, String description) {
        putCurrentOnStack();
        Activity previous = lookup.get(id);
        if (previous != null) {
            // there is already an activity with that ID. so we remove it from the stack 
            stack.remove(previous);
            if (previous.getDescription().equals(description)) {
                activateActivity(previous);
            } else {
                // the previous activity is moved to finished and we create a new one
                finished.add(previous);
                activateNewActivity(id, description);
            }
        } else {
            // new activity
            activateNewActivity(id, description);
        }
    }

    private void activateActivity(Activity activity) {
        // we activate this activity
        current = activity;
    }

    private void putCurrentOnStack() {
        // put current on the stack.
        if (current != null) {
            stack.add(0, current);
        }
    }

    private void activateNewActivity(String id, String description) {
        final Activity activity = new Activity(id, description);
        activateActivity(activity);
        lookup.put(id, activity);
    }

    Activity getCurrent() {
        return current;
    }

    List<Activity> getActivityStack() {
        return stack;
    }

    List<Activity> getFinishedActivities() {
        return finished;
    }

}
