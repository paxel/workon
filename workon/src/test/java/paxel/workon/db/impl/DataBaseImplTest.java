package paxel.workon.db.impl;

import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.Test;
import static org.junit.Assert.*;

public class DataBaseImplTest {

    private DataBaseImpl dataBaseImpl;

    @Test
    public void startNewActivityOnEmptyDb() {
        withEmptyDb();
        whenStarted("first", "My first activity");
        thenCurrentActivityIdIs("first");
        thenCurrentActivityDescriptionIs("My first activity");
        thenPreviousActivityIdIs(null);
        thenPreviousActivitiesCountIs(0);
        thenFinishedActivitiesCountIs(0);
    }

    @Test
    public void startNewActivityOnOngoingActivity() {
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
    public void startExistingActivityOnOngoingActivity() {
        startNewActivityOnOngoingActivity();
        whenStarted("first", "My first activity");
        thenCurrentActivityIdIs("first");
        thenCurrentActivityDescriptionIs("My first activity");
        thenPreviousActivityIdIs("second");
        thenPreviousActivityDescriptionIs("My second activity");
        thenPreviousActivitiesCountIs(1);
        thenFinishedActivitiesCountIs(0);
    }

    @Test
    public void restartOngoingActivity() {
        startNewActivityOnEmptyDb();
        whenStarted("first", "My first activity");
        thenCurrentActivityIdIs("first");
        thenCurrentActivityDescriptionIs("My first activity");
        thenPreviousActivitiesCountIs(0);
        thenFinishedActivitiesCountIs(0);
    }

    @Test
    public void restartOngoingActivityWithDifferentId() {
        startNewActivityOnEmptyDb();
        whenStarted("first", "My second activity");
        thenCurrentActivityIdIs("first");
        thenCurrentActivityDescriptionIs("My second activity");
        thenPreviousActivitiesCountIs(0);
        thenFinishedActivitiesCountIs(1);
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

}
