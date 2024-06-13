package codes.laivy.jhttp.content;

import codes.laivy.jhttp.utilities.DateUtils;
import codes.laivy.jhttp.utilities.KeyReader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ContentDisposition {

    // Static initializers

    public static boolean validate(@NotNull String string) {
        try {
            parse(string);
            return true;
        } catch (@NotNull ParseException e) {
            return false;
        }
    }
    public static @NotNull ContentDisposition parse(@NotNull String string) throws ParseException {
        @NotNull Map<String, String> keys = KeyReader.read(string, '=', ';');

        try {
            // Type
            @NotNull Type type = Type.getById(keys.keySet().stream().findFirst().orElseThrow(() -> new ParseException("cannot find connection type", 0)));
            keys.remove(type.getId().toLowerCase());

            // Name
            @Nullable String name = keys.getOrDefault("name", null);

            // Property
            @Nullable Property property = null;

            @Nullable String filename = keys.getOrDefault("filename", null);
            @Nullable OffsetDateTime creationDate = keys.containsKey("creation-date") ? DateUtils.RFC822.convert(keys.get("creation-date")) : null;
            @Nullable OffsetDateTime modificationDate = keys.containsKey("modification-date") ? DateUtils.RFC822.convert(keys.get("modification-date")) : null;
            @Nullable OffsetDateTime readDate = keys.containsKey("read-date") ? DateUtils.RFC822.convert(keys.get("read-date")) : null;
            @Nullable Long size = keys.containsKey("size") ? Long.parseLong(keys.get("size")) : null;

            if (filename != null || creationDate != null || modificationDate != null || readDate != null || size != null) {
                property = new Property(filename, creationDate, modificationDate, readDate, size);
            }

            return new ContentDisposition(type, name, property);
        } catch (@NotNull Throwable throwable) {
            throw new ParseException("cannot parse '" + string + "' as a valid content disposition: " + throwable.getMessage(), 0);
        }
    }

    public static @NotNull ContentDisposition create(@NotNull Type type, @Nullable String name, @Nullable Property property) {
        return new ContentDisposition(type, name, property);
    }

    // Object

    private final @NotNull Type type;

    private @Nullable String name;
    private @Nullable Property property;

    private ContentDisposition(@NotNull Type type, @Nullable String name, @Nullable Property property) {
        this.type = type;
        this.name = name;
        this.property = property;

        if (name != null && name.contains("\"")) {
            throw new IllegalArgumentException("disposition name with illegal characters");
        }
    }

    // Getters

    public @NotNull Type getType() {
        return type;
    }

    public @Nullable String getName() {
        return name;
    }
    public void setName(@Nullable String name) {
        this.name = name;

        if (name != null && name.contains("\"")) {
            throw new IllegalArgumentException("disposition name with illegal characters");
        }
    }

    public @Nullable Property getProperty() {
        return property;
    }
    public void setProperty(@Nullable Property property) {
        this.property = property;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull ContentDisposition that = (ContentDisposition) object;
        return type == that.type && Objects.equals(name, that.name) && Objects.equals(property, that.property);
    }
    @Override
    public int hashCode() {
        return Objects.hash(type, name, property);
    }

    @Override
    public @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder(getType().getId());

        if (getName() != null) {
            builder.append("; name=\"").append(getName()).append("\"");
        } if (getProperty() != null) {
            builder.append(getProperty());
        }

        return builder.toString();
    }

    // Classes

    public enum Type {

        INLINE("inline"),
        ATTACHMENT("attachment"),
        FORM_DATA("form-data"),
        ;

        private final @NotNull String id;

        Type(@NotNull String id) {
            this.id = id;
        }

        // Getters

        public @NotNull String getId() {
            return id;
        }

        // Static initializers

        public static @NotNull Type getById(@NotNull String id) {
            @NotNull Optional<Type> optional = Arrays.stream(values()).filter(type -> type.getId().equalsIgnoreCase(id)).findFirst();
            return optional.orElseThrow(() -> new NullPointerException("there's no content disposition type with id '" + id + "'"));
        }

    }
    public static final class Property {

        // Static initializers

        public static @NotNull Builder builder() {
            return new Builder();
        }

        // Object

        private final @Nullable String name;
        private final @Nullable OffsetDateTime creation;
        private final @Nullable OffsetDateTime modification;
        private final @Nullable OffsetDateTime read;
        private final @Nullable Long size;

        private Property(
                @Nullable String name,
                @Nullable OffsetDateTime creation,
                @Nullable OffsetDateTime modification,
                @Nullable OffsetDateTime read,
                @Nullable Long size
        ) {
            this.name = name;
            this.creation = creation;
            this.modification = modification;
            this.read = read;
            this.size = size;

            if (name != null && name.contains("\"")) {
                throw new IllegalArgumentException("property name with illegal characters");
            } else if (size != null && size < 0) {
                throw new IllegalArgumentException("property size cannot be lower than zero");
            }
        }

        // Getters

        public @Nullable String getName() {
            return name;
        }
        public @Nullable OffsetDateTime getCreation() {
            return creation;
        }
        public @Nullable OffsetDateTime getModification() {
            return modification;
        }
        public @Nullable OffsetDateTime getRead() {
            return read;
        }
        public @Nullable Long getSize() {
            return size;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            @NotNull Property property = (Property) object;
            return Objects.equals(name, property.name) && Objects.equals(creation, property.creation) && Objects.equals(modification, property.modification) && Objects.equals(read, property.read) && Objects.equals(size, property.size);
        }
        @Override
        public int hashCode() {
            return Objects.hash(name, creation, modification, read, size);
        }

        @Override
        public @NotNull String toString() {
            @NotNull StringBuilder builder = new StringBuilder();

            if (getName() != null) {
                builder.append("; filename=\"").append(getName()).append("\"");
            } if (getCreation() != null) {
                builder.append("; creation-date=\"").append(DateUtils.RFC822.convert(getCreation())).append("\"");
            } if (getModification() != null) {
                builder.append("; modification-date=\"").append(DateUtils.RFC822.convert(getModification())).append("\"");
            } if (getRead() != null) {
                builder.append("; read-date=\"").append(DateUtils.RFC822.convert(getRead())).append("\"");
            } if (getSize() != null) {
                builder.append("; size=\"").append(getSize()).append("\"");
            }

            return builder.toString();
        }

        // Classes

        public static final class Builder {

            private @Nullable String name;
            private @Nullable OffsetDateTime creation;
            private @Nullable OffsetDateTime modification;
            private @Nullable OffsetDateTime read;
            private @Nullable Long size;

            private Builder() {
            }

            // Getters

            @Contract("_->this")
            public @NotNull Builder name(@Nullable String name) {
                this.name = name;
                return this;
            }
            @Contract("_->this")
            public @NotNull Builder creation(@Nullable OffsetDateTime creation) {
                this.creation = creation;
                return this;
            }
            @Contract("_->this")
            public @NotNull Builder modification(@Nullable OffsetDateTime modification) {
                this.modification = modification;
                return this;
            }
            @Contract("_->this")
            public @NotNull Builder read(@Nullable OffsetDateTime read) {
                this.read = read;
                return this;
            }
            @Contract("_->this")
            public @NotNull Builder size(@Nullable Long size) {
                this.size = size;
                return this;
            }

            // Build

            public @NotNull Property build() {
                return new Property(name, creation, modification, read, size);
            }

        }

    }

}
