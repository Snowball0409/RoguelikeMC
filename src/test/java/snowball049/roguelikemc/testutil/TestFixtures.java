package snowball049.roguelikemc.testutil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class TestFixtures {
    public static final String UPGRADES = "fixtures/upgrades.json";
    public static final String PERSISTENCE = "fixtures/persistence.json";
    public static final String GAMEPLAY = "fixtures/gameplay.json";
    public static final String HANDLERS = "fixtures/handlers.json";
    public static final String CONFIG = "fixtures/config.json";
    public static final String ITEMS = "fixtures/items.json";

    private static final Gson GSON = new Gson();

    private TestFixtures() {
    }

    public static JsonObject root(String fixtureFile) {
        return readJsonObject(fixtureFile);
    }

    public static JsonArray array(String fixtureFile, String key) {
        return root(fixtureFile).getAsJsonArray(key);
    }

    public static JsonObject object(String fixtureFile, String... keys) {
        JsonObject current = root(fixtureFile);
        for (String key : keys) {
            current = current.getAsJsonObject(key);
        }
        return current;
    }

    public static Stream<JsonObject> cases(String fixtureFile, String key) {
        return stream(array(fixtureFile, key));
    }

    public static Stream<JsonObject> stream(JsonArray array) {
        return StreamSupport.stream(array.spliterator(), false).map(JsonElement::getAsJsonObject);
    }

    public static String readUtf8(String resourcePath) {
        InputStream stream = TestFixtures.class.getClassLoader().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalArgumentException("Missing test resource: " + resourcePath);
        }

        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                builder.append(buffer, 0, read);
            }
            return builder.toString();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read test resource: " + resourcePath, exception);
        }
    }

    public static JsonObject readJsonObject(String resourcePath) {
        return JsonParser.parseString(readUtf8(resourcePath)).getAsJsonObject();
    }

    public static JsonArray readJsonArray(String resourcePath) {
        return JsonParser.parseString(readUtf8(resourcePath)).getAsJsonArray();
    }

    public static <T> T readJson(String resourcePath, Class<T> type) {
        return GSON.fromJson(readUtf8(resourcePath), type);
    }

    public static List<String> readStringList(JsonArray array) {
        List<String> values = new ArrayList<>();
        for (JsonElement element : array) {
            values.add(element.getAsString());
        }
        return values;
    }
}
