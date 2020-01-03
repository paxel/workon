package paxel.workon.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

/**
 *
 */
public class PidLock implements AutoCloseable {

    private Path lockFile;
    private final String appDir;

    public PidLock(String appDir) {
        this.appDir = appDir;
    }

    public boolean tryLock() throws IOException {
        if (lockFile != null) {
            return false;
        }
        Path workonDir = Paths.get(System.getProperty("user.home"), appDir);
        Path createDirectories = Files.createDirectories(workonDir);
        lockFile = createDirectories.resolve(".lock");

        if (Files.exists(lockFile)) {
            // check PID?
            return false;
        }

        BufferedWriter out = Files.newBufferedWriter(lockFile);
        out.write("#" + Instant.now());
        out.close();
        return true;
    }

    @Override
    public void close() throws Exception {
        Files.delete(lockFile);
        lockFile = null;
    }
}
