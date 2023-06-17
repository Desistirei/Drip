package br.com.stenox.cxc.gamer;

import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class GamerRepository {

    private final DataSource dataSource;

    private final GamerAdapter adapter = new GamerAdapter();

    public Gamer fetch(UUID uniqueId){
        try (Connection connection = dataSource.getConnection()) {
            final PreparedStatement statement = connection.prepareStatement("SELECT * FROM `players` WHERE `uuid` = '" + uniqueId.toString() + "';");

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) return null;

            Gamer gamer = adapter.read(resultSet);

            resultSet.close();
            statement.close();

            return gamer;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> fetch(String ip){
        try (Connection connection = dataSource.getConnection()) {
            final PreparedStatement statement = connection.prepareStatement("SELECT * FROM `players` WHERE `ip` = '" + ip + "';");

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) return Collections.emptyList();

            List<String> names = new ArrayList<>();

            names.add(resultSet.getString("name"));

            while (resultSet.next())
                names.add(resultSet.getString("name"));

            resultSet.close();
            statement.close();

            return names;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insert(Gamer occurrence) {
        try (Connection connection = dataSource.getConnection()) {
            final PreparedStatement statement = connection.prepareStatement("INSERT INTO `players`(`uuid`, `name`, `ip`, `group`, `groupTime`, `muted`, `muteTime`, `muteReason`, `banned`, `banTime`, `banReason`, `password`) VALUES ('" + occurrence.getUniqueId().toString() + "'," +
                    " '" + occurrence.getName() + "'," +
                    " '" + occurrence.getIp() + "'," +
                    " '" + occurrence.getGroup().getName() + "'," +
                    " '" + occurrence.getGroupTime() + "'," +
                    " '" + occurrence.isMuted() + "'," +
                    " '" + occurrence.getMuteTime() + "'," +
                    " '" + occurrence.getMuteReason() + "'," +
                    " '" + occurrence.isBanned() + "'," +
                    " '" + occurrence.getBanTime() + "'," +
                    " '" + occurrence.getBanReason() + "'," +
                    " '" + occurrence.getPassword() + "');");

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean contains(UUID uniqueId) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `players` WHERE `uuid` = '" + uniqueId.toString() + "';");

            ResultSet rs = statement.executeQuery();

            if (!rs.next())
                return false;

            rs.close();
            statement.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void update(Gamer occurrence) {
        try (Connection connection = dataSource.getConnection()) {
            if (contains(occurrence.getUniqueId())){
                final PreparedStatement statement = connection.prepareStatement("UPDATE `players` SET `name` = '" + occurrence.getName() + "'," +
                        " `ip` = '" + occurrence.getIp() + "', `group` = '" + occurrence.getGroup().getName() + "', `groupTime` = '" + occurrence.getGroupTime() + "'," +
                        " `muted` = '" + occurrence.isMuted() + "'," +
                        " `muteTime` = '" + occurrence.getMuteTime() + "', `muteReason` ='" + occurrence.getMuteReason() + "'," +
                        " `banned` = '" + occurrence.isBanned() + "', `banTime` = '" + occurrence.getBanTime() + "'," +
                        " `banReason` = '" + occurrence.getBanReason() + "', `password` = '" + occurrence.getPassword() + "' WHERE `uuid` = '" + occurrence.getUniqueId().toString() + "';");

                statement.executeUpdate();
                statement.close();
            } else
                insert(occurrence);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
