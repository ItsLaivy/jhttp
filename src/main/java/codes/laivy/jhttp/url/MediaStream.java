package codes.laivy.jhttp.url;

import codes.laivy.jhttp.module.content.ContentSecurityPolicy;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

// todo: 10/06/2024 MediaStream
//  It's extremely bad documented, i'll wait a little bit more before
//  creating this class.
public final class MediaStream implements ContentSecurityPolicy.Source {

    // Static initializers

    public static boolean validate(@NotNull String string) {
        return string.startsWith("mediastream:");
    }
    public static @NotNull MediaStream parse(@NotNull String string) {
        // todo: 10/06/2024 MediaStream parser
        return new MediaStream(UUID.randomUUID(), false);
    }

    // Object

    private final @NotNull UUID uuid;
    private final boolean active;

    // todo: 10/06/2024 MediaStream tracks
    public MediaStream(@NotNull UUID uuid, boolean active) {
        this.uuid = uuid;
        this.active = active;

        throw new UnsupportedOperationException("media streams still not supported by jhttp");
    }

    // Getters

    @Contract(pure = true)
    public @NotNull UUID getUniqueId() {
        return uuid;
    }
    public boolean isActive() {
        return active;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull MediaStream that = (MediaStream) object;
        return active == that.active && Objects.equals(uuid, that.uuid);
    }
    @Override
    public int hashCode() {
        return Objects.hash(uuid, active);
    }

    @Override
    public @NotNull String toString() {
        // todo: MediaStream serializer
        throw new UnsupportedOperationException("mediastream serializers still not supported by jhttp");
    }

}
