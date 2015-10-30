package black.door.node.java;

import black.door.node.java.loops.GenericIoLoop;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

/**
 * Created by nfischer on 10/29/2015.
 */
public class kluglikg {
	kluglikg(){

		Object httpRequest = null;

		GenericIoLoop.submit(()->{
			// get weiner from database
			return 5;
		}, n -> {
			System.out.println(httpRequest);
			System.out.println(n);
		});

		readEntireFile(new File("myfile"), b -> System.out.println(b.length));
	}

	static void readEntireFile(File f, Consumer<byte[]> callback){
		GenericIoLoop.submit(() -> {
			try {
				return Files.readAllBytes(f.toPath());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, callback);
	}
}
