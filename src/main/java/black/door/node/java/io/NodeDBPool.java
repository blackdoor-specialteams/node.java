package black.door.node.java.io;

import black.door.dbp.DBP;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.typesafe.config.*;

import javax.xml.soap.Node;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cjbur on 10/29/2015.
 */
public class NodeDBPool {
    private HikariDataSource ds;

    public NodeDBPool() {

    }

    /**
     * @param conf
     * @return
     */
    public static NodeDBPool build(DBConfig conf) {
        NodeDBPool tmp = new NodeDBPool();
        return tmp;
    }

    /**
     *
     */
    public static class DBConfig {
        private String usr;

        private String password;
        private String url;
        private Map<String, Object> config;

        public DBConfig() {
            this.config = new HashMap<>();
        }

        public static DBConfig load(File f) {
            DBConfig conf = new DBConfig();

            conf.set
            return conf;
        }

        public HikariConfig getHikariConfig() {
            HikariConfig hcf = new HikariConfig();
            for (String x : config.keySet()) {
                hcf.addDataSourceProperty(x, config.get(x));
            }
            return hcf;
        }

        public void putSetting(String k, Object v) {
            config.put(k, v);
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsr() {
            return usr;
        }

        public void setUsr(String usr) {
            this.usr = usr;
        }

    }

}
