package codes.laivy.jhttp.utilities;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ContentDisposition {

    // Static initializers

    public static final @NotNull Pattern PARSE_PATTERN = Pattern.compile("^(inline|attachment|form-data)"
            + "(?:\\s*;\\s*filename\\s*=\\s*\"([^\"]*)\""
            + "|\\s*;\\s*name\\s*=\\s*\"([^\"]*)\""
            + "|\\s*;\\s*creation-date\\s*=\\s*\"([^\"]*)\""
            + "|\\s*;\\s*modification-date\\s*=\\s*\"([^\"]*)\""
            + "|\\s*;\\s*read-date\\s*=\\s*\"([^\"]*)\""
            + "|\\s*;\\s*size\\s*=\\s*([0-9]+)"
            + ")*");

    public static boolean isContentDisposition(@NotNull String string) {
        return PARSE_PATTERN.matcher(string).matches();
    }
    public static @NotNull ContentDisposition parse(@NotNull String string) throws ParseException {
        @NotNull Matcher matcher = PARSE_PATTERN.matcher(string);

        if (matcher.find() && matcher.groupCount() == 7) {
            @NotNull Optional<Type> optional = Arrays.stream(Type.values()).filter(t -> t.getId().equalsIgnoreCase(matcher.group(1))).findFirst();
            @NotNull Type type = optional.orElseThrow(() -> new ParseException("unknown content disposition type '" + matcher.group(1) + "'", matcher.start(1)));
            @Nullable String name = matcher.group(3);

            // Property
            @Nullable Property property = null;

            @Nullable String filename = matcher.group(2);
            @Nullable Instant creationDate = matcher.group(4) != null ? DateUtils.RFC822.convert(matcher.group(4)) : null;
            @Nullable Instant modificationDate = matcher.group(5) != null ? DateUtils.RFC822.convert(matcher.group(5)) : null;
            @Nullable Instant readDate = matcher.group(6) != null ? DateUtils.RFC822.convert(matcher.group(6)) : null;
            @Nullable Long size = matcher.group(7) != null ? Long.parseLong(matcher.group(7)) : null;

            if (filename != null || creationDate != null || modificationDate != null || readDate != null || size != null) {
                property = new Property(filename, creationDate, modificationDate, readDate, size);
            }

            return new ContentDisposition(type, name, property);
        } else {
            throw new ParseException("cannot parse content disposition '" + string + "'", 0);
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
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentDisposition that = (ContentDisposition) o;
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
            builder.append("; ").append(getProperty());
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

    }
    public static final class Property {

        // Static initializers

        public static @NotNull Builder builder() {
            return new Builder();
        }

        // Object

        private final @Nullable String name;
        private final @Nullable Instant creation;
        private final @Nullable Instant modification;
        private final @Nullable Instant read;
        private final @Nullable Long size;

        private Property(
                @Nullable String name,
                @Nullable Instant creation,
                @Nullable Instant modification,
                @Nullable Instant read,
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
        public @Nullable Instant getCreation() {
            return creation;
        }
        public @Nullable Instant getModification() {
            return modification;
        }
        public @Nullable Instant getRead() {
            return read;
        }
        public @Nullable Long getSize() {
            return size;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Property property = (Property) o;
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
                builder.append("filename=\"").append(getName()).append("\"");
            } if (getCreation() != null) {
                if (builder.length() > 0) builder.append("; ");
                builder.append("creation-date=\"").append(DateUtils.RFC822.convert(getCreation())).append("\"");
            } if (getModification() != null) {
                if (builder.length() > 0) builder.append("; ");
                builder.append("modification-date=\"").append(DateUtils.RFC822.convert(getModification())).append("\"");
            } if (getRead() != null) {
                if (builder.length() > 0) builder.append("; ");
                builder.append("read-date=\"").append(DateUtils.RFC822.convert(getRead())).append("\"");
            } if (getSize() != null) {
                if (builder.length() > 0) builder.append("; ");
                builder.append("size-date=\"").append(getSize()).append("\"");
            }

            return builder.toString();
        }

        // Classes

        public static final class Builder {

            private @Nullable String name;
            private @Nullable Instant creation;
            private @Nullable Instant modification;
            private @Nullable Instant read;
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
            public @NotNull Builder creation(@Nullable Instant creation) {
                this.creation = creation;
                return this;
            }
            @Contract("_->this")
            public @NotNull Builder modification(@Nullable Instant modification) {
                this.modification = modification;
                return this;
            }
            @Contract("_->this")
            public @NotNull Builder read(@Nullable Instant read) {
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
