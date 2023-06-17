package br.com.stenox.cxc.utils.restapi;
import br.com.stenox.cxc.utils.restapi.parser.UUIDParser;
import br.com.stenox.cxc.utils.http.HttpRequest;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@AllArgsConstructor
public class UUIDFetcher {

    private final DataSource dataSource;

    private final Pattern namePattern = Pattern.compile("[a-zA-Z0-9_]{3,16}");
    private final JsonParser jsonParser = new JsonParser();

    private final LoadingCache<String, Optional<UUID>> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(1L, TimeUnit.HOURS)
            .build(new CacheLoader<String, Optional<UUID>>() {
                @Override
                public Optional<UUID> load(@NonNull String name) throws Exception {
                    return Optional.ofNullable(fetchFromMojang(name));
                }
            });

    private UUID fetchFromMojang(String name) throws Exception {
        HttpRequest request = HttpRequest.get("https://api.mojang.com/users/profiles/minecraft/" + name)
                .connectTimeout(15000)
                .readTimeout(15000)
                .acceptJson();
        if (request.ok()) {
            JsonObject jsonObject = jsonParser.parse(request.reader()).getAsJsonObject();
            return UUIDParser.parse(jsonObject.get("id").getAsString());
        } else if (request.noContent()) {
            return fetchFromDatabase(name);
        }
        return null;
    }

    private UUID fetchFromDatabase(String name) throws Exception {
        final String sql = "SELECT `uuid` FROM `players` WHERE LOWER(`name`) = LOWER(?) LIMIT 1";
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return UUID.fromString(rs.getString("uniqueId"));
            }
        }
        return null;
    }

    public UUID getUUID(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Name cannot be null or empty");

        name = name.toLowerCase();

        if (!namePattern.matcher(name).matches()) {
            return UUIDParser.parse(name);
        }

        try {
            return cache.get(name).orElse(null);
        } catch (Exception ignored) {
        }
        return null;
    }
}
