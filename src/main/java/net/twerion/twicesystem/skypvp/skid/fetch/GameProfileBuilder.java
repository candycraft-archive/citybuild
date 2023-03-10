/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.authlib.properties.PropertyMap$Serializer
 *  com.mojang.util.UUIDTypeAdapter
 *  org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
 */
package net.twerion.twicesystem.skypvp.skid.fetch;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameProfileBuilder {
    private static final String SERVICE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    private static final String JSON_SKIN = "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}";
    private static final String JSON_CAPE = "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"},\"CAPE\":{\"url\":\"%s\"}}}";
    private static Gson gson = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(UUID.class, (Object) new UUIDTypeAdapter()).registerTypeAdapter(GameProfile.class, (Object) new GameProfileSerializer()).registerTypeAdapter(PropertyMap.class, (Object) new PropertyMap.Serializer()).create();
    private static HashMap<UUID, CachedProfile> cache = new HashMap();
    private static long cacheTime = -1L;

    public static GameProfile fetch(UUID uuid) throws IOException {
        return GameProfileBuilder.fetch(uuid, false);
    }

    public static GameProfile fetch(UUID uuid, boolean forceNew) throws IOException {
        if (!forceNew && cache.containsKey(uuid) && cache.get(uuid).isValid()) {
            return cache.get(uuid).profile;
        }
        HttpURLConnection connection = (HttpURLConnection) new URL(String.format(SERVICE_URL, UUIDTypeAdapter.fromUUID((UUID) uuid))).openConnection();
        connection.setReadTimeout(5000);
        if (connection.getResponseCode() == 200) {
            String json = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            GameProfile result = (GameProfile) gson.fromJson(json, GameProfile.class);
            cache.put(uuid, new CachedProfile(result));
            return result;
        }
        if (!forceNew && cache.containsKey(uuid)) {
            return cache.get(uuid).profile;
        }
        JsonObject error = (JsonObject) new JsonParser().parse(new BufferedReader(new InputStreamReader(connection.getErrorStream())).readLine());
        throw new IOException(error.get("error").getAsString() + ": " + error.get("errorMessage").getAsString());
    }

    public static GameProfile getProfile(UUID uuid, String name, String skin) {
        return GameProfileBuilder.getProfile(uuid, name, skin, null);
    }

    public static GameProfile getProfile(UUID uuid, String name, String skinUrl, String capeUrl) {
        GameProfile profile = new GameProfile(uuid, name);
        boolean cape = capeUrl != null && !capeUrl.isEmpty();
        ArrayList<Object> args = new ArrayList<Object>();
        args.add(System.currentTimeMillis());
        args.add(UUIDTypeAdapter.fromUUID((UUID) uuid));
        args.add(name);
        args.add(skinUrl);
        if (cape) {
            args.add(capeUrl);
        }
        profile.getProperties().put((Object) "textures", (Object) new Property("textures", Base64Coder.encodeString((String) String.format(cape ? JSON_CAPE : JSON_SKIN, args.toArray(new Object[args.size()])))));
        return profile;
    }

    public static void setCacheTime(long time) {
        cacheTime = time;
    }

    private static class CachedProfile {
        private long timestamp = System.currentTimeMillis();
        private GameProfile profile;

        public CachedProfile(GameProfile profile) {
            this.profile = profile;
        }

        public boolean isValid() {
            return cacheTime < 0L;
        }
    }

    private static class GameProfileSerializer
            implements JsonSerializer<GameProfile>,
            JsonDeserializer<GameProfile> {
        private GameProfileSerializer() {
        }

        public GameProfile deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = (JsonObject) json;
            UUID id = object.has("id") ? (UUID) context.deserialize(object.get("id"), UUID.class) : null;
            String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;
            GameProfile profile = new GameProfile(id, name);
            if (object.has("properties")) {
                for (Map.Entry prop : ((PropertyMap) context.deserialize(object.get("properties"), PropertyMap.class)).entries()) {
                    profile.getProperties().put((Object) ((String) prop.getKey()), (Object) ((Property) prop.getValue()));
                }
            }
            return profile;
        }

        public JsonElement serialize(GameProfile profile, Type type, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            if (profile.getId() != null) {
                result.add("id", context.serialize((Object) profile.getId()));
            }
            if (profile.getName() != null) {
                result.addProperty("name", profile.getName());
            }
            if (!profile.getProperties().isEmpty()) {
                result.add("properties", context.serialize((Object) profile.getProperties()));
            }
            return result;
        }
    }

}

