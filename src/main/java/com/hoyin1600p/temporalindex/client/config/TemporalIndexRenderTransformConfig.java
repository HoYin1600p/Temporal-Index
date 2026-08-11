package com.hoyin1600p.temporalindex.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hoyin1600p.temporalindex.TemporalIndex;
import com.hoyin1600p.temporalindex.storage.TemporalIndexStorage;
import com.hoyin1600p.temporalindex.storage.TemporalRelics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Runtime JSON source for the book model and every dynamic cover-sprite transform. This class has
 * no dependency on the temporary calibration screen, so the screen can be
 * removed without changing config loading or rendering.
 */
public final class TemporalIndexRenderTransformConfig {
    private static final String DEFAULT_RESOURCE =
            "/assets/temporal_index/config/item_render_transforms.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Transform IDENTITY = new Transform(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    private static final TemporalIndexRenderTransformConfig INSTANCE =
            new TemporalIndexRenderTransformConfig();

    private final Path filePath = FMLPaths.CONFIGDIR.get()
            .resolve(TemporalIndex.MOD_ID)
            .resolve("item_render_transforms.json");
    private final EnumMap<RenderContext, Transform> bookDefaults = new EnumMap<>(RenderContext.class);
    private final EnumMap<RenderContext, Transform> bookValues = new EnumMap<>(RenderContext.class);
    private final Map<String, EnumMap<RenderContext, Transform>> defaults = new LinkedHashMap<>();
    private final Map<String, EnumMap<RenderContext, Transform>> values = new LinkedHashMap<>();

    private boolean loaded;

    private TemporalIndexRenderTransformConfig() {
    }

    public static TemporalIndexRenderTransformConfig getInstance() {
        INSTANCE.ensureLoaded();
        return INSTANCE;
    }

    public synchronized Transform get(String itemKey, RenderContext context) {
        ensureLoaded();
        EnumMap<RenderContext, Transform> item = values.get(itemKey);
        return item == null ? IDENTITY : item.getOrDefault(context, IDENTITY);
    }

    public synchronized Transform getBook(RenderContext context) {
        ensureLoaded();
        return bookValues.getOrDefault(context, IDENTITY);
    }

    public synchronized void set(String itemKey, RenderContext context, Transform transform) {
        ensureLoaded();
        values.computeIfAbsent(itemKey, ignored -> new EnumMap<>(RenderContext.class))
                .put(context, transform);
    }

    public synchronized void setBook(RenderContext context, Transform transform) {
        ensureLoaded();
        bookValues.put(context, transform);
    }

    public synchronized void reset(String itemKey, RenderContext context) {
        ensureLoaded();
        Transform defaultValue = defaults.getOrDefault(itemKey, new EnumMap<>(RenderContext.class))
                .getOrDefault(context, IDENTITY);
        set(itemKey, context, defaultValue);
    }

    public synchronized void resetBook(RenderContext context) {
        ensureLoaded();
        setBook(context, bookDefaults.getOrDefault(context, IDENTITY));
    }

    public synchronized void reload() {
        load(true);
    }

    public synchronized void save() {
        ensureLoaded();
        JsonObject root = new JsonObject();
        root.addProperty("schemaVersion", 2);
        JsonObject book = new JsonObject();
        for (RenderContext context : RenderContext.values()) {
            book.add(context.jsonName(), writeTransform(getBook(context)));
        }
        root.add("book", book);
        JsonObject items = new JsonObject();
        for (String key : itemKeys()) {
            JsonObject item = new JsonObject();
            for (RenderContext context : RenderContext.values()) {
                item.add(context.jsonName(), writeTransform(get(key, context)));
            }
            items.add(key, item);
        }
        root.add("items", items);

        try {
            Files.createDirectories(filePath.getParent());
            Path temporary = filePath.resolveSibling(filePath.getFileName() + ".tmp");
            Files.writeString(temporary, GSON.toJson(root), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, filePath, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicMoveFailure) {
                Files.move(temporary, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            TemporalIndex.LOGGER.error("Could not save Temporal Index render transforms to {}", filePath, exception);
        }
    }

    public Path getFilePath() {
        return filePath;
    }

    public static String keyFor(ItemStack book) {
        return keyForSlot(TemporalIndexStorage.getSelectedSlot(book));
    }

    public static String keyForSlot(int slot) {
        if (slot == TemporalIndexStorage.SHARD_SLOT) {
            return "temporal_shard";
        }
        if (slot > TemporalIndexStorage.SHARD_SLOT && slot < TemporalIndexStorage.SLOT_COUNT) {
            return TemporalRelics.DEFINITIONS.get(slot - 1).modifier().toString();
        }
        return "temporal_shard";
    }

    public static List<String> itemKeys() {
        List<String> keys = new ArrayList<>(TemporalIndexStorage.SLOT_COUNT);
        keys.add("temporal_shard");
        for (TemporalRelics.Definition definition : TemporalRelics.DEFINITIONS) {
            keys.add(definition.modifier().toString());
        }
        return List.copyOf(keys);
    }

    private synchronized void ensureLoaded() {
        if (!loaded) {
            load(false);
        }
    }

    private void load(boolean force) {
        if (loaded && !force) {
            return;
        }

        bookDefaults.clear();
        bookValues.clear();
        defaults.clear();
        values.clear();
        try (InputStream stream = TemporalIndexRenderTransformConfig.class.getResourceAsStream(DEFAULT_RESOURCE)) {
            if (stream == null) {
                throw new IOException("Missing packaged default " + DEFAULT_RESOURCE);
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            readBook(root, bookDefaults);
            readRelicDefaults(root, defaults);
            readItems(root, defaults, false);
        } catch (Exception exception) {
            TemporalIndex.LOGGER.error("Could not read packaged Temporal Index render transforms", exception);
        }
        fillMissingBook(bookDefaults, new EnumMap<>(RenderContext.class));
        bookValues.putAll(bookDefaults);
        copyMap(defaults, values);
        try {
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) {
                try (InputStream stream = TemporalIndexRenderTransformConfig.class.getResourceAsStream(DEFAULT_RESOURCE)) {
                    if (stream == null) {
                        throw new IOException("Missing packaged default " + DEFAULT_RESOURCE);
                    }
                    Files.copy(stream, filePath);
                }
            }
            try (InputStream stream = Files.newInputStream(filePath)) {
                JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                        .getAsJsonObject();
                readBook(root, bookValues);
                readItems(root, values, schemaVersion(root) < 2);
            }
        } catch (Exception exception) {
            TemporalIndex.LOGGER.error("Could not load Temporal Index render transforms from {}; using packaged defaults",
                    filePath, exception);
        }
        fillMissingBook(bookValues, bookDefaults);
        fillMissing(values, defaults);
        loaded = true;
    }

    private static void readBook(JsonObject root, EnumMap<RenderContext, Transform> target) {
        JsonObject book = object(root.get("book"));
        if (book == null) {
            return;
        }
        for (RenderContext context : RenderContext.values()) {
            JsonObject transform = object(book.get(context.jsonName()));
            if (transform != null) {
                target.put(context, readTransform(transform));
            }
        }
    }

    private static void readRelicDefaults(
            JsonObject root,
            Map<String, EnumMap<RenderContext, Transform>> target
    ) {
        JsonObject relicDefaults = object(root.get("relic_defaults"));
        if (relicDefaults == null) {
            return;
        }
        for (int index = 1; index < itemKeys().size(); index++) {
            EnumMap<RenderContext, Transform> contexts =
                    target.computeIfAbsent(itemKeys().get(index), ignored -> new EnumMap<>(RenderContext.class));
            for (RenderContext context : RenderContext.values()) {
                JsonObject transform = object(relicDefaults.get(context.jsonName()));
                if (transform != null) {
                    contexts.put(context, readTransform(transform));
                }
            }
        }
    }

    private static void readItems(
            JsonObject root,
            Map<String, EnumMap<RenderContext, Transform>> target,
            boolean migrateLegacyDefaults
    ) {
        JsonObject items = root.getAsJsonObject("items");
        if (items == null) {
            return;
        }
        for (String itemKey : itemKeys()) {
            JsonObject item = object(items.get(itemKey));
            if (item == null) {
                continue;
            }
            EnumMap<RenderContext, Transform> contexts =
                    target.computeIfAbsent(itemKey, ignored -> new EnumMap<>(RenderContext.class));
            for (RenderContext context : RenderContext.values()) {
                JsonObject transform = object(item.get(context.jsonName()));
                if (transform != null) {
                    Transform value = readTransform(transform);
                    if (!migrateLegacyDefaults
                            || itemKey.equals("temporal_shard")
                            || !value.equals(legacyRelicDefault(context))) {
                        contexts.put(context, value);
                    }
                }
            }
        }
    }

    private static int schemaVersion(JsonObject root) {
        if (!root.has("schemaVersion") || !root.get("schemaVersion").isJsonPrimitive()) {
            return 1;
        }
        return root.get("schemaVersion").getAsInt();
    }

    private static Transform legacyRelicDefault(RenderContext context) {
        return switch (context) {
            case FIRST_PERSON -> new Transform(0.04D, 0.175D, 0.075D, 0.0D, 0.0D, 19.0D);
            case THIRD_PERSON, DROPPED -> new Transform(0.04D, 0.175D, 0.075D, 0.0D, 0.0D, -19.0D);
            case ITEM_FRAME -> new Transform(0.04D, 0.175D, -0.03D, 0.0D, 0.0D, 19.0D);
        };
    }

    private static Transform readTransform(JsonObject object) {
        JsonObject translation = object.getAsJsonObject("translation");
        JsonObject rotation = object.getAsJsonObject("rotation");
        return new Transform(
                number(translation, "x"),
                number(translation, "y"),
                number(translation, "z"),
                number(rotation, "x"),
                number(rotation, "y"),
                number(rotation, "z")
        );
    }

    private static JsonObject writeTransform(Transform transform) {
        JsonObject object = new JsonObject();
        JsonObject translation = new JsonObject();
        translation.addProperty("x", transform.translationX());
        translation.addProperty("y", transform.translationY());
        translation.addProperty("z", transform.translationZ());
        JsonObject rotation = new JsonObject();
        rotation.addProperty("x", transform.rotationX());
        rotation.addProperty("y", transform.rotationY());
        rotation.addProperty("z", transform.rotationZ());
        object.add("translation", translation);
        object.add("rotation", rotation);
        return object;
    }

    private static JsonObject object(JsonElement element) {
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    private static double number(JsonObject object, String key) {
        if (object == null || !object.has(key) || !object.get(key).isJsonPrimitive()) {
            return 0.0D;
        }
        double value = object.get(key).getAsDouble();
        return Double.isFinite(value) ? value : 0.0D;
    }

    private static void copyMap(
            Map<String, EnumMap<RenderContext, Transform>> source,
            Map<String, EnumMap<RenderContext, Transform>> target
    ) {
        for (Map.Entry<String, EnumMap<RenderContext, Transform>> entry : source.entrySet()) {
            target.put(entry.getKey(), new EnumMap<>(entry.getValue()));
        }
    }

    private static void fillMissing(
            Map<String, EnumMap<RenderContext, Transform>> target,
            Map<String, EnumMap<RenderContext, Transform>> fallback
    ) {
        for (String key : itemKeys()) {
            EnumMap<RenderContext, Transform> contexts =
                    target.computeIfAbsent(key, ignored -> new EnumMap<>(RenderContext.class));
            EnumMap<RenderContext, Transform> fallbackContexts =
                    fallback.getOrDefault(key, new EnumMap<>(RenderContext.class));
            for (RenderContext context : RenderContext.values()) {
                contexts.putIfAbsent(context, fallbackContexts.getOrDefault(context, IDENTITY));
            }
        }
    }

    private static void fillMissingBook(
            EnumMap<RenderContext, Transform> target,
            EnumMap<RenderContext, Transform> fallback
    ) {
        for (RenderContext context : RenderContext.values()) {
            target.putIfAbsent(context, fallback.getOrDefault(context, IDENTITY));
        }
    }

    public enum RenderContext {
        FIRST_PERSON("first_person", "First Person"),
        THIRD_PERSON("third_person", "Third Person"),
        DROPPED("dropped", "Dropped Item"),
        ITEM_FRAME("item_frame", "Item Frame");

        private final String jsonName;
        private final String displayName;

        RenderContext(String jsonName, String displayName) {
            this.jsonName = jsonName;
            this.displayName = displayName;
        }

        public String jsonName() {
            return jsonName;
        }

        public String displayName() {
            return displayName;
        }
    }

    public enum Parameter {
        TRANSLATION_X("Translation X", -1.0D, 1.0D),
        TRANSLATION_Y("Translation Y", -1.0D, 1.0D),
        TRANSLATION_Z("Translation Z", -1.0D, 1.0D),
        ROTATION_X("Rotation X", -180.0D, 180.0D),
        ROTATION_Y("Rotation Y", -180.0D, 180.0D),
        ROTATION_Z("Rotation Z", -180.0D, 180.0D);

        private final String displayName;
        private final double minimum;
        private final double maximum;

        Parameter(String displayName, double minimum, double maximum) {
            this.displayName = displayName;
            this.minimum = minimum;
            this.maximum = maximum;
        }

        public String displayName() {
            return displayName;
        }

        public double minimum() {
            return minimum;
        }

        public double maximum() {
            return maximum;
        }
    }

    public record Transform(
            double translationX,
            double translationY,
            double translationZ,
            double rotationX,
            double rotationY,
            double rotationZ
    ) {
        public double get(Parameter parameter) {
            return switch (parameter) {
                case TRANSLATION_X -> translationX;
                case TRANSLATION_Y -> translationY;
                case TRANSLATION_Z -> translationZ;
                case ROTATION_X -> rotationX;
                case ROTATION_Y -> rotationY;
                case ROTATION_Z -> rotationZ;
            };
        }

        public Transform with(Parameter parameter, double value) {
            return switch (parameter) {
                case TRANSLATION_X -> new Transform(value, translationY, translationZ,
                        rotationX, rotationY, rotationZ);
                case TRANSLATION_Y -> new Transform(translationX, value, translationZ,
                        rotationX, rotationY, rotationZ);
                case TRANSLATION_Z -> new Transform(translationX, translationY, value,
                        rotationX, rotationY, rotationZ);
                case ROTATION_X -> new Transform(translationX, translationY, translationZ,
                        value, rotationY, rotationZ);
                case ROTATION_Y -> new Transform(translationX, translationY, translationZ,
                        rotationX, value, rotationZ);
                case ROTATION_Z -> new Transform(translationX, translationY, translationZ,
                        rotationX, rotationY, value);
            };
        }
    }
}
