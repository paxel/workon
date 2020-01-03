package paxel.workon.db;

import java.io.IOException;

public interface DataBaseSerializer {

    /**
     * Reads a database from a predefined source.
     *
     * @return The data base if readable.
     *
     * @throws java.io.IOException in case it could not be read.
     */
    DataBase read() throws IOException;

    /**
     * Writes the data base to a predefined destination.
     *
     * @param db the data base to write.
     *
     * @throws java.io.IOException in case it could not be written.
     */
    void write(DataBase db) throws IOException;
}
