package black.door.node.java.io;

import black.door.dbp.DBP;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cjbur on 10/29/2015.
 */
public class NodeDBPool {
    private HikariDataSource ds;
    private String password;
    private String url;
    private Map<String, Object> config;

    public NodeDBPool(HikariDataSource ds) {
        this.config = new HashMap<>();
        this.ds = ds;
    }

    public static NodeDBPool build() {
        Config as = ConfigFactory.load();
        //
        HikariConfig hcf = new HikariConfig();
        try {
            hcf.setUsername(as.getString("username"));
            hcf.setPassword(as.getString("password"));
            hcf.setJdbcUrl(as.getString("url"));
            //        Config ls = conf.getConfig("other-settings");
//
//        for (String x : ls.getList(). ) {
//            hcf.addDataSourceProperty(x, config.get(x));
//        }
        } catch (ConfigException.Missing e) {
            DBP.error().log("Missing Configuration for DBPool");
            throw new RuntimeException(e);
        }

        HikariDataSource ds = new HikariDataSource(hcf);
        NodeDBPool tmp = new NodeDBPool(ds);
        return tmp;
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void close() {
        ds.close();
    }

}
