package br.com.stenox.cxc.gamer;

import br.com.stenox.cxc.gamer.group.Group;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class GamerAdapter {

    public Gamer read(ResultSet rs) throws SQLException {
        Gamer gamer = new Gamer(UUID.fromString(rs.getString("uuid")));
        gamer.setName(rs.getString("name"));
        gamer.setIp(rs.getString("ip"));
        gamer.setGroup(Group.getGroupByName(rs.getString("group")), rs.getLong("groupTime"));
        gamer.setMuted(Boolean.parseBoolean(rs.getString("muted")));
        gamer.setMuteTime(rs.getLong("muteTime"));
        gamer.setMuteReason(rs.getString("muteReason"));
        gamer.setBanned(Boolean.parseBoolean(rs.getString("banned")));
        gamer.setBanTime(rs.getLong("banTime"));
        gamer.setBanReason(rs.getString("banReason"));
        gamer.setPassword(rs.getString("password"));

        return gamer;
    }
}