package paxel.workon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import paxel.workon.db.DataBase;
import paxel.workon.db.impl.Activity;
import paxel.workon.db.impl.DataBaseImpl;
import paxel.workon.db.impl.DataBaseStreamSerializer;
import paxel.workon.io.PidLock;

public class WorkOn {

    public static void main(String... argv) {

        try {
            PidLock lock = new PidLock(WORKON);
            try {
                if (!lock.tryLock()) {
                    System.err.println("Can't get a lock.\nProbably another workon process is running. If you are sure that no other process is running, delete '~/.workon/.lock'");
                    return;
                }
                new WorkOn().handle(Arrays.asList(argv));

            } finally {
                lock.close();
            }
        } catch (Exception exception) {
            System.err.println("" + exception.getLocalizedMessage());
        }
    }
    private static final String WORKON = ".workon";
    private final Path dbFile;
    private final DataBase db;
    private final DataBaseStreamSerializer baseStreamSerializer;

    public WorkOn() throws IOException {
        // complete bull crap code! :D
        Path workonDir = Paths.get(System.getProperty("user.home"), WORKON);
        Path createDirectories = Files.createDirectories(workonDir);
        dbFile = createDirectories.resolve(".db");
        baseStreamSerializer = new DataBaseStreamSerializer(() -> {
            try {
                return Files.newInputStream(dbFile);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }, () -> {
            try {
                return Files.newOutputStream(dbFile);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        if (Files.exists(dbFile)) {
            db = baseStreamSerializer.read();
        } else {
            db = new DataBaseImpl();
        }
    }

    private List<String> removeCommand(List<String> args) {
        if (args.size() == 1) {
            return Collections.emptyList();
        }
        return args.subList(1, args.size());
    }

    private void handle(List<String> argv) {
        if (argv.isEmpty() || argv.get(0).equalsIgnoreCase("status") || argv.get(0).equals("#")) {
            printStatus();
        } else {
            final String command = argv.get(0);
            if (command.equalsIgnoreCase("start") || command.equals("+")) {
                start(removeCommand(argv));
            } else if (command.equalsIgnoreCase("stop") || command.equals("-")) {
                idle(removeCommand(argv));
            } else if (command.equalsIgnoreCase("finish") || command.equals("!")) {
                stop();
            } else if (command.equalsIgnoreCase("switch") || command.equals(".")) {
                switchTo(removeCommand(argv));
            } else if (command.equalsIgnoreCase("clear")) {
                purge();
            } else if (command.equalsIgnoreCase("reset")) {
                reset();
            } else if (command.equalsIgnoreCase("help") || command.equals("?")) {
                syntax();
            } else {
                printStatus();
            }
        }
    }

    private void printStatus() {
        Activity current = db.getCurrent();
        if (!db.getActivityStack().isEmpty()) {
            System.out.println("Your stack:");
            for (Activity activity : db.getActivityStack()) {
                System.out.println(" * " + activity.getId() + " : '" + activity.getDescription() + "' (" + formatTime(activity.getDurationInMillis()) + ")");
            }
        }

        if (!db.getFinishedActivities().isEmpty()) {
            System.out.println("You have finished:");
            for (Activity activity : db.getFinishedActivities()) {
                System.out.println(" - '" + activity.getDescription() + "' (" + formatTime(activity.getDurationInMillis()) + ")");
            }
        }

        if (current == null) {
            System.out.println("\nYou are idle.\n");
        } else {
            System.out.println("\nYou are working on '" + current.getDescription() + "' (for " + formatTime(db.getCurrent().getDurationInMillis() + db.getCurrentDuration()) + ")");
        }

    }

    private void start(List<String> param) {
        if (param.isEmpty()) {
            switchToPrevious();
        }

        if (param.size() == 1) {
            switchToId(param);
        }
        if (param.size() > 1) {
            String id = param.get(0);

            String description = param.subList(1, param.size()).stream().collect(Collectors.joining(" "));
            db.start(id, description);
            printStatus();
            saveDb();
        }
    }

    private void switchToId(List<String> param) {
        String id = param.get(0);
        boolean start = db.start(id);
        if (start) {
            printStatus();
            saveDb();
        } else {
            System.err.println("Could not switch to activity with ID '" + id + "'.");
            printStatus();
        }
    }

    private void stop() {
        db.stop();
        printStatus();
        saveDb();
    }

    private void switchTo(List<String> param) {
        if (param.size() >= 1) {
            switchToId(param);
        } else {
            switchToPrevious();
        }
    }

    private void switchToPrevious() {
        boolean startPrevious = db.startPrevious();
        if (startPrevious) {
            System.out.println("Switched to previous activity.");
            printStatus();
            saveDb();
        } else {
            System.err.println("Could not switch to previous activity.");
            printStatus();
        }
    }

    private void purge() {
        db.clearFinishedActivities();
        System.out.println("Purged finished activities");
        printStatus();
        saveDb();
    }

    private void saveDb() {
        try {
            this.baseStreamSerializer.write(db);
        } catch (IOException ex) {
            System.err.println("Could not write DB file " + dbFile + ": " + ex.getMessage());
        }
    }

    private void idle(List<String> removeCommand) {
        db.idle();
        printStatus();
        saveDb();
    }

    private void reset() {
        db.clearFinishedActivities();
        System.out.println("DB reseted.");
        printStatus();
        saveDb();
    }

    private String formatTime(long durationInMillis) {
        StringBuilder b = new StringBuilder();
        long hours = TimeUnit.MILLISECONDS.toHours(durationInMillis);

        if (hours > 0) {
            b.append(hours).append("h ");
            durationInMillis -= TimeUnit.HOURS.toMillis(hours);
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis);

        if (minutes > 0) {
            b.append(minutes).append("m ");
            durationInMillis -= TimeUnit.MINUTES.toMillis(minutes);
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis);

        b.append(seconds).append("s ");
        return b.toString();
    }

    private void syntax() {
        System.out.println("wo <command> ");
        System.out.println(" where commands are");
        System.out.println("  help/?: shows this command ");
        System.out.println("  status/# or no command: prints the db.");
        System.out.println("  start/+ [id [description]]: starts previous activity, the activity with given ID or a new or previous activity with given ID and description.");
        System.out.println("  stop/-: pushs current activity to the stack.");
        System.out.println("  switch/. [id]: starts previous activity or the previous activity with given ID.");
        System.out.println("  finish/!: stops current activity.");
        System.out.println("  purge: removes all finished activities.");
        System.out.println("  reset: clears the data base");
    }

}
