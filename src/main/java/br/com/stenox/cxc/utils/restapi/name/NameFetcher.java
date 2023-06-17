package br.com.stenox.cxc.utils.restapi.name;

import br.com.stenox.cxc.utils.http.HttpRequest;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class NameFetcher {

    private final DataSource dataSource;

    private final JsonParser jsonParser = new JsonParser();
    private final LoadingCache<UUID, Optional<String>> cache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.HOURS)
            .build(new CacheLoader<UUID, Optional<String>>() {
                @Override
                public Optional<String> load(@NonNull UUID uuid) throws Exception {
                    return Optional.ofNullable(fetchFromMojang(uuid));
                }
            });

    private String fetchFromMojang(UUID uuid) throws Exception {
        String id = uuid.toString().replace("-", "");
        HttpRequest request = HttpRequest.get("https://api.mojang.com/user/profiles/" + id + "/names")
                .connectTimeout(15000)
                .readTimeout(15000)
                .acceptJson();

        if (request.ok()) {
            JsonArray jsonArray = jsonParser.parse(request.reader()).getAsJsonArray();
            JsonObject jsonObject = jsonArray.get(jsonArray.size() - 1).getAsJsonObject();
            return jsonObject.get("name").getAsString();
        } else if (request.noContent()) {
            return fetchFromDatabase(uuid);
        }

        return null;
    }

    public String getName(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (uuid.equals(UUID.fromString("b3521779-9e0d-4d6e-95a3-ed05abdcacbf")))
            return "Sistema";
        try {
            return cache.get(uuid).orElse(null);
        } catch (Exception ignored) {
        }

        return null;
    }

    public String fetchFromDatabase(UUID uuid) throws SQLException {
        String sql = "SELECT `name` FROM `global_account` WHERE `uuid` = ? AND `name` IS NOT NULL LIMIT 1";
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    public String random() throws SQLException {
        final String sql = "SELECT `name` FROM `players` " +
                "WHERE `name` IS NOT NULL AND LENGTH(`name`) >= 3 AND LENGTH(`name`) < 12 " +
                "ORDER BY RANDOM()" +
                "LIMIT 1";

        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }
}
