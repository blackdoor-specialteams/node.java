package black.door.node.java.io;

import black.door.node.java.loops.GenericIoLoop;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * Created by cjbur on 10/29/2015.
 */
public class IOTask {


    static void readFromFile(File f, Consumer<byte[]> callback) {
        GenericIoLoop.submit(() -> {
            try {
                return Files.readAllBytes(f.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, callback);
    }

    static void queryDB(PreparedStatement p, Consumer<byte[]> callback) {
        GenericIoLoop.submit(() -> {
            try {
                p.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
//                return p.getResultSet();
//
            return null;
        }, callback);
    }
}
