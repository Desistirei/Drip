package br.com.stenox.cxc.utils.mojang;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ProfileAPI {

    public static final LoadingCache<WrappedGameProfile, Optional<WrappedSignedProperty>> Textures = CacheBuilder.newBuilder()
            .expireAfterWrite(30L, TimeUnit.MINUTES)
            .build(new CacheLoader<WrappedGameProfile, Optional<WrappedSignedProperty>>() {
                @Override
                public Optional<WrappedSignedProperty> load(WrappedGameProfile key) throws Exception {
                    return Optional.ofNullable(loadTextures(key));
                }
            });

    private static  WrappedSignedProperty loadTextures(WrappedGameProfile profile) throws Exception {
        Object minecraftServer = MinecraftReflection.getMinecraftServerClass().getMethod("getServer").invoke(null);
        ((MinecraftSessionService) minecraftServer.getClass().getMethod("aD").invoke(minecraftServer))
                .fillProfileProperties((GameProfile) profile.getHandle(), true);
        return Iterables.getFirst(profile.getProperties().get("textures"), null);
    }
}
