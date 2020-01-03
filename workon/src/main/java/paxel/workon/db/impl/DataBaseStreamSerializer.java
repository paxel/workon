package paxel.workon.db.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Supplier;
import paxel.workon.db.DataBase;
import paxel.workon.db.DataBaseSerializer;

public class DataBaseStreamSerializer implements DataBaseSerializer {

    private final Supplier<InputStream> inputStreamSupplier;
    private final Supplier<OutputStream> outputStreamSupplier;

    public DataBaseStreamSerializer(Supplier<InputStream> inputStreamSupplier, Supplier<OutputStream> outputStreamSupplier) {
        this.inputStreamSupplier = inputStreamSupplier;
        this.outputStreamSupplier = outputStreamSupplier;
    }

    @Override
    public DataBase read() throws IOException {
        try (InputStream in = inputStreamSupplier.get()) {
            DataInputStream dataInputStream = new DataInputStream(in);
            DataBaseImpl result = new DataBaseImpl();
            int version = dataInputStream.readUnsignedByte();
            if (version == 0) {
                boolean current = dataInputStream.readBoolean();
                if (current) {
                    long start = dataInputStream.readLong();
                    Activity currentActivity = readActivity(dataInputStream);
                    result.setCurrent(currentActivity);
                    result.setStart(start);
                    result.getLookup().put(currentActivity.getId(), currentActivity);
                }
                int stack = dataInputStream.readUnsignedShort();
                for (int i = 0; i < stack; i++) {
                    Activity stackActivity = readActivity(dataInputStream);
                    result.getStack().add(stackActivity);
                    result.getLookup().put(stackActivity.getId(), stackActivity);
                }
                int finished = dataInputStream.readUnsignedShort();
                for (int i = 0; i < finished; i++) {
                    Activity finishedActivity = readActivity(dataInputStream);
                    result.getFinished().add(finishedActivity);
                }
                return result;
            } else {
                throw new IOException("Unsupported file version " + version);
            }
        }
    }

    @Override
    public void write(DataBase db) throws IOException {
        if (db instanceof DataBaseImpl) {
            try (OutputStream out = outputStreamSupplier.get()) {
                DataOutputStream dataOutputStream = new DataOutputStream(out);
                // version
                dataOutputStream.writeByte(0);
                final Activity current = db.getCurrent();
                final boolean currentAvailable = current != null;
                dataOutputStream.writeBoolean(currentAvailable);
                if (currentAvailable) {
                    dataOutputStream.writeLong(((DataBaseImpl) db).getStart());
                    writeActivity(current, dataOutputStream);
                }
                final List<Activity> stack = db.getActivityStack();
                dataOutputStream.writeShort(stack.size());
                for (Activity activity : stack) {
                    writeActivity(activity, dataOutputStream);
                }
                final List<Activity> finished = db.getFinishedActivities();
                dataOutputStream.writeShort(finished.size());
                for (Activity activity : finished) {
                    writeActivity(activity, dataOutputStream);
                }

            }
        } else {
            throw new IllegalArgumentException("Unsupported DataBase implementation " + db.getClass());
        }
    }

    private Activity readActivity(DataInputStream dataInputStream) throws IOException {
        String id = dataInputStream.readUTF();
        String description = dataInputStream.readUTF();
        long duration = dataInputStream.readLong();
        final Activity activity = new Activity(id, description);
        activity.addDurationInMillis(duration);
        return activity;
    }

    private void writeActivity(Activity current, DataOutputStream out) throws IOException {
        out.writeUTF(current.getId());
        out.writeUTF(current.getDescription());
        out.writeLong(current.getDurationInMillis());
    }
}
