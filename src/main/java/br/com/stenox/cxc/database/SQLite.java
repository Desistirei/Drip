package br.com.stenox.cxc.database;

import br.com.stenox.cxc.Main;
import lombok.Getter;

import java.io.File;
import java.sql.*;

@Getter
public class SQLite {

    private Connection connection;
    private final Main plugin;
    private String url;

    public SQLite(Main plugin){
        this.plugin = plugin;
    }

    public void openConnection() {
        File file = new File(plugin.getDataFolder(), "database.db");

        url = "jdbc:sqlite:" + file;

        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection(url);

            plugin.getLogger().info("SQLite Connected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;

                plugin.getLogger().info("SQLite Closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    
    public void createTables() {
        PreparedStatement ps;

        try {
            ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `players`(`uuid` TEXT, `name` TEXT, `ip` TEXT, `group` TEXT, `groupTime` LONG, `muted` BOOLEAN, `muteTime` LONG, `muteReason` TEXT, `banned` BOOLEAN, `banTime` LONG, `banReason` TEXT, `password` TEXT);");
            ps.execute();
            ps.close();
            plugin.getLogger().info("SQLite Tables Created");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
