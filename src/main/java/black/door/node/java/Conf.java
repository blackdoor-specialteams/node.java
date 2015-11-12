package black.door.node.java;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by nfischer on 11/10/2015.
 */
public enum Conf {
	INST;

	private Config config;

	Conf(){
		config = ConfigFactory.load();
	}
	public static Config get(){
		return INST.config;
	}
}
