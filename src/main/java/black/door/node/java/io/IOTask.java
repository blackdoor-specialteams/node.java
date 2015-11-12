package black.door.node.java.io;

import black.door.dbp.DBP;
import black.door.node.java.exception.WrappedException;
import black.door.node.java.loops.BlockingLoop;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by cjbur on 10/29/2015.
 */
public class IOTask {


    static void readFromFile(File f, Consumer<byte[]> callback, Consumer<Throwable> failure) {
        BlockingLoop.submits(() -> {
            try {
                return Files.readAllBytes(f.toPath());
            } catch (IOException e) {
                throw new WrappedException(e);
            }
        }, callback, failure);
    }


    static void queryDB(PreparedStatement p, Consumer<ResultSet> callback, Consumer<Throwable> failure) {
        BlockingLoop.submits(() -> {
                p.execute();
                return p.getResultSet();
        }, callback, failure);
    }

}
