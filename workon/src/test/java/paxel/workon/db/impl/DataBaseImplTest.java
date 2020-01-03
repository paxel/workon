package paxel.workon.db.impl;

import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.Test;
import static org.junit.Assert.*;

public class DataBaseImplTest {

    private DataBaseImpl dataBaseImpl;
    private boolean result;

    // ################################################################
    // start (ID, Description) tests
    // ################################################################
    @Test
    public void startNewActivityOnEmptyDb() {
        withEmptyDb();
        whenStarted("first", "My first activity");
        thenCurrentActivityIdIs("first");
        thenCurrentActivityDescriptionIs("My first activity");
        thenPreviousActivityIdIs(null);
        thenPreviousActivityDescriptionIs(null);
        thenPreviousActivitiesCountIs(0);
        thenFinishedActivitiesCountIs(0);
    }

    @Test
    public void startNewActivityOnCurrentActivity() {
        startNewActivityOnEmptyDb();
        whenStarted("second", "My second activity");
        thenCurrentActivityIdIs("second");
        thenCurrentActivityDescriptionIs("My second activity");
        thenPreviousActivityIdIs("first");
        thenPreviousActivityDescriptionIs("My first activity");
        thenPreviousActivitiesCountIs(1);
        thenFinishedActivitiesCountIs(0);
    }

    @Test
    public void startExistingActivityOnCurrentActivity() {
        startNewActivityOnCurrentActivity();
        whenStarted("first", "My first activity");
        thenCurrentActivityIdIs("first");
        thenCurrentActivityDescriptionIs("My first activity");
        thenPreviousActivityIdIs("second");
        thenPreviousActivityDescriptionIs("My second activity");
        thenPreviousActivitiesCountIs(1);
        thenFinishedActivitiesCountIs(0);
    }

    @Test
    public void startCurrentActivity() {
        startNewActivityOnEmptyDb();
        whenStarted("first", "My first activity");
        thenCurrentActivityIdIs("first");
        thenCurrentActivityDescriptionIs("My first activity");
        thenPreviousActivitiesCountIs(0);
        thenFinishedActivitiesCountIs(0);
    }

    @Test
    public void startCurrentActivityWithDifferentId() {
        startNewActivityOnEmptyDb();
        whenStarted("first", "My second activity");
        thenCurrentActivityIdIs("first");
        thenCurrentActivityDescriptionIs("My second activity");
        thenPreviousActivitiesCountIs(0);
        thenFinishedActivitiesCountIs(1);
        thenFinishedActivityDurationsAreNotZero();
    }
    // ################################################################
    // start (ID) tests
    // ################################################################

    @Test
    public void restartActivityByIdOnEmptyDb() {
        withEmptyDb();
        whenRestarted("first");
        thenResultIs(false);
        thenCurrentActivityIdIs(null);
        thenCurrentActivityDescriptionIs(null);
        thenPreviousActivityIdIs(null);
        thenPreviousActivityDescriptionIs(null);
        thenPreviousActivitiesCountIs(0);
        thenFinishedActivitiesCountIs(0);
    }

    @Test
    public void restartCurrentActivityById() {
        startNewActivityOnEmptyDb();
        whenRestarted("first");
        thenResultIs(true);
        thenCurrentActivityIdIs("first");
        thenCurrentActivityDescriptionIs("My first activity");
        thenPreviousActivityIdIs(null);
        thenPreviousActivityDescriptionIs(null);
        thenPreviousActivitiesCountIs(0);
        thenFinishedActivitiesCountIs(0);
    }

    @Test
    public void restartSomeActivityById() {
        startNewActivityOnCurrentActivity();
        whenRestarted("first");
        thenResultIs(true);
        thenCurrentActivityIdIs("first");
        thenCurrentActivityDescriptionIs("My first activity");
        thenPreviousActivityIdIs("second");
        thenPreviousActivityDescriptionIs("My second activity");
        thenPreviousActivitiesCountIs(1);
        thenFinishedActivitiesCountIs(0);
    }

    @Test
    public void restartUnknownActivityById() {
        startNewActivityOnEmptyDb();
        whenRestarted("second");
        thenResultIs(false);
        thenCurrentActivityIdIs(null);
        thenCurrentActivityDescriptionIs(null);
        thenPreviousActivityIdIs("first");
        thenPreviousActivityDescriptionIs("My first activity");
        thenPreviousActivitiesCountIs(1);
        thenFinishedActivitiesCountIs(0);
    }

    // ################################################################
    // start () tests
    // ################################################################
    @Test
    public void restartPreviousActivityOnEmptyDb() {
        withEmptyDb();
        whenRestartedPrevious();
        thenResultIs(false);
        thenCurrentActivityIdIs(null);
        thenCurrentActivityDescriptionIs(null);
        thenPreviousActivityIdIs(null);
        thenPreviousActivityDescriptionIs(null);
        thenPreviousActivitiesCountIs(0);
        thenFinishedActivitiesCountIs(0);
    }

    @Test
    public void restartPreviousActivityOnEmptyStack() {
        startNewActivityOnEmptyDb();
        whenRestartedPrevious();
        thenResultIs(false);
        thenCurrentActivityIdIs(null);
        thenCurrentActivityDescriptionIs(null);
        thenPreviousActivityIdIs("first");
        thenPreviousActivityDescriptionIs("My first activity");
        thenPreviousActivitiesCountIs(1);
        thenFinishedActivitiesCountIs(0);
    }

    @Test
    public void restartPreviousActivity() {
        startNewActivityOnCurrentActivity();
        whenRestartedPrevious();
        thenResultIs(true);
        thenCurrentActivityIdIs("first");
        thenCurrentActivityDescriptionIs("My first activity");
        thenPreviousActivityIdIs("second");
        thenPreviousActivityDescriptionIs("My second activity");
        thenPreviousActivitiesCountIs(1);
        thenFinishedActivitiesCountIs(0);
    }

    // ################################################################
    // stop () tests
    // ################################################################
    @Test
    public void stopOnEmptyDb() {
        withEmptyDb();
        whenStop();
        thenResultIs(false);
        thenCurrentActivityIdIs(null);
        thenCurrentActivityDescriptionIs(null);
        thenPreviousActivityIdIs(null);
        thenPreviousActivityDescriptionIs(null);
        thenPreviousActivitiesCountIs(0);
        thenFinishedActivitiesCountIs(0);
    }

    @Test
    public void stopOnEmptyStack() {
        startNewActivityOnEmptyDb();
        whenStop();
        thenResultIs(true);
        thenCurrentActivityIdIs(null);
        thenCurrentActivityDescriptionIs(null);
        thenPreviousActivityIdIs(null);
        thenPreviousActivityDescriptionIs(null);
        thenPreviousActivitiesCountIs(0);
        thenFinishedActivitiesCountIs(1);
        thenFinishedActivityDurationsAreNotZero();
    }

    @Test
    public void stop() {
        startNewActivityOnCurrentActivity();
        whenStop();
        thenResultIs(true);
        thenCurrentActivityIdIs(null);
        thenCurrentActivityDescriptionIs(null);
        thenPreviousActivityIdIs("first");
        thenPreviousActivityDescriptionIs("My first activity");
        thenPreviousActivitiesCountIs(1);
        thenFinishedActivitiesCountIs(1);
        thenFinishedActivityDurationsAreNotZero();
    }

    @Test
    public void purge() {
        startExistingActivityOnCurrentActivity();
        whenStarted("3", "Three");
        whenStarted("4", "Four");
        whenStarted("5", "Five");
        whenStarted("6", "Six");
        //6
        whenStop();
        thenResultIs(true);
        whenRestartedPrevious();
        //5
        whenStop();
        thenResultIs(true);
        whenRestartedPrevious();
        //4
        whenStop();
        thenResultIs(true);
        whenRestartedPrevious();
        //3
        whenStop();
        thenResultIs(true);
        whenRestartedPrevious();
        //2
        whenStop();
        thenResultIs(true);
        whenRestartedPrevious();
        //1
        whenStop();
        thenResultIs(true);

        thenCurrentActivityIdIs(null);
        thenCurrentActivityDescriptionIs(null);
        thenPreviousActivitiesCountIs(0);
        thenFinishedActivitiesCountIs(6);
        thenFinishedActivityDurationsAreNotZero();
    }

    private void withEmptyDb() {
        dataBaseImpl = new DataBaseImpl();
    }

    private void whenStarted(String id, String description) {
        dataBaseImpl.start(id, description);
    }

    private void thenCurrentActivityIdIs(String id) {
        if (id == null) {
            assertThat(dataBaseImpl.getCurrent(), is(nullValue()));
        } else {
            assertThat(dataBaseImpl.getCurrent().getId(), is(id));
        }
    }

    private void thenCurrentActivityDescriptionIs(String description) {
        if (description == null) {
            assertThat(dataBaseImpl.getCurrent(), is(nullValue()));
        } else {
            assertThat(dataBaseImpl.getCurrent().getDescription(), is(description));
        }
    }

    private void thenPreviousActivityIdIs(String id) {
        final List<Activity> activityStack = dataBaseImpl.getActivityStack();
        if (id == null) {
            if (activityStack.size() > 0) {
                assertThat(activityStack.get(0), is(nullValue()));
            }
            // else stack is empty. previous is null
        } else {
            assertThat(activityStack.get(0).getId(), is(id));
        }
    }

    private void thenPreviousActivityDescriptionIs(String description) {
        final List<Activity> activityStack = dataBaseImpl.getActivityStack();
        if (description == null) {
            if (activityStack.size() > 0) {
                assertThat(activityStack.get(0), is(nullValue()));
            }
            // else stack is empty. previous is null
        } else {
            assertThat(activityStack.get(0).getDescription(), is(description));
        }
    }

    private void thenPreviousActivitiesCountIs(int count) {
        final List<Activity> activityStack = dataBaseImpl.getActivityStack();
        assertThat(activityStack.size(), is(count));
    }

    private void thenFinishedActivitiesCountIs(int count) {
        final List<Activity> finishedActivities = dataBaseImpl.getFinishedActivities();
        assertThat(finishedActivities.size(), is(count));
    }

    private void whenRestarted(String id) {
        result = dataBaseImpl.start(id);
    }

    private void thenResultIs(boolean id) {
        assertThat(result, is(id));
    }

    private void whenRestartedPrevious() {
        result = dataBaseImpl.startPrevious();
    }

    private void whenStop() {
        result = dataBaseImpl.stop();
    }

    private void thenFinishedActivityDurationsAreNotZero() {
        List<Activity> finishedActivities = dataBaseImpl.getFinishedActivities();
        for (Activity finishedActivity : finishedActivities) {
            assertThat(finishedActivity.getDurationInMillis(), is(not(0)));
        }
    }

}
