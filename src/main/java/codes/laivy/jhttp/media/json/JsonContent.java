package codes.laivy.jhttp.media.json;

import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.message.Content;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

public interface JsonContent extends Content<JsonElement> {

    @Override
    @NotNull MediaType<JsonElement, JsonContent> getMediaType();

    // Validators

    default boolean isJsonArray() {
        return getElement().isJsonArray();
    }
    default boolean isJsonObject() {
        return getElement().isJsonObject();
    }
    default boolean isJsonPrimitive() {
        return getElement().isJsonPrimitive();
    }
    default boolean isJsonNull() {
        return getElement().isJsonNull();
    }

    // Getters

    default @NotNull JsonObject getAsJsonObject() {
        return getElement().getAsJsonObject();
    }
    default @NotNull JsonArray getAsJsonArray() {
        return getElement().getAsJsonArray();
    }
    default @NotNull JsonPrimitive getAsJsonPrimitive() {
        return getElement().getAsJsonPrimitive();
    }
    default @NotNull JsonNull getAsJsonNull() {
        return getElement().getAsJsonNull();
    }

}
