package codes.laivy.jhttp.media.json;

import codes.laivy.jhttp.content.Content;
import codes.laivy.jhttp.media.MediaType;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

public interface JsonContent extends Content<JsonElement> {

    @Override
    @NotNull MediaType<JsonElement> getMediaType();

    // Validators

    default boolean isJsonArray() {
        return getData().isJsonArray();
    }
    default boolean isJsonObject() {
        return getData().isJsonObject();
    }
    default boolean isJsonPrimitive() {
        return getData().isJsonPrimitive();
    }
    default boolean isJsonNull() {
        return getData().isJsonNull();
    }

    // Getters

    default @NotNull JsonObject getAsJsonObject() {
        return getData().getAsJsonObject();
    }
    default @NotNull JsonArray getAsJsonArray() {
        return getData().getAsJsonArray();
    }
    default @NotNull JsonPrimitive getAsJsonPrimitive() {
        return getData().getAsJsonPrimitive();
    }
    default @NotNull JsonNull getAsJsonNull() {
        return getData().getAsJsonNull();
    }

}
