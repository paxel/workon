package paxel.workon.db;

import java.util.List;
import paxel.workon.db.impl.Activity;

public interface DataBase {

    /**
     * Retrieves a read only view on the Activity stack.
     *
     * @return the unfinished activities.
     */
    List<Activity> getActivityStack();

    /**
     * Retrieve the current Activity if not idle.
     *
     * @return the current activity or {@code null}.
     */
    Activity getCurrent();

    /**
     * Retrieve the read only view on the finished activities.
     *
     * @return the finished activities.
     */
    List<Activity> getFinishedActivities();

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
    boolean start(String id);

    /**
     * Start the activity with given ID and Description.
     * The current activity is put on the stack.
     * If the given ID exists and the description is the same, the existing activity is set as current.
     * If the given ID exists and the description does not match, the previous activity is finished.
     * If the given ID does not exist, or the description does not match a new activity is set as current.
     *
     * @param id          The ID of the new current activity.
     * @param description The description of the new current activity,
     */
    void start(String id, String description);

    /**
     * Start the previous activity.
     * The current activity is put on the stack.
     * If there was no previous activity, nothing else happens and the result is false.
     * Otherwise the previous activity is set as current and the result is true.
     *
     * @return {@code true} if the ID existed.
     */
    boolean startPrevious();

    /**
     * Finishes the current activity if any.
     *
     * @return {@code true} if the activity was finished.
     */
    boolean stop();

    public void clearFinishedActivities();

    public void clearAll();

    public void idle();

    public long getCurrentDuration();
}
