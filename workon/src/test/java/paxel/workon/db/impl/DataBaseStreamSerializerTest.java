package paxel.workon.db.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import paxel.workon.db.DataBase;

public class DataBaseStreamSerializerTest {

    @Test
    public void testActiveCurrent() throws IOException {
        DataBaseImpl original = new DataBaseImpl();

        original.start("1", "This is an example");
        original.start("2", "This is an other example");
        original.start("3", "This is a third example");
        original.start("4", "This is the last example");
        DataBase copy = writeAndReadDataBase(original);

        assertThat(original, is(copy));
    }

    @Test
    public void testIdle() throws IOException {
        DataBaseImpl original = new DataBaseImpl();

        original.start("1", "This is an example");
        original.start("2", "This is an other example");
        original.start("3", "This is a third example");
        original.start("4", "This is the last example");
        original.stop();
        DataBase copy = writeAndReadDataBase(original);

        assertThat(original, is(copy));
    }

    @Test
    public void testAllFinished() throws IOException {
        DataBaseImpl original = new DataBaseImpl();

        original.start("1", "This is an example");
        original.stop();
        DataBase copy = writeAndReadDataBase(original);

        assertThat(original, is(copy));
    }

    @Test
    public void testEmpty() throws IOException {
        DataBaseImpl original = new DataBaseImpl();

        DataBase copy = writeAndReadDataBase(original);

        assertThat(original, is(copy));
    }

    private DataBase writeAndReadDataBase(DataBaseImpl baseImpl) throws IOException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        DataBaseStreamSerializer dataBaseStreamSerializer = new DataBaseStreamSerializer(() -> null, () -> arrayOutputStream);
        dataBaseStreamSerializer.write(baseImpl);
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(arrayOutputStream.toByteArray());
        dataBaseStreamSerializer = new DataBaseStreamSerializer(() -> arrayInputStream, () -> null);
        DataBase copy = dataBaseStreamSerializer.read();
        return copy;
    }

}
