package codes.laivy.jhttp.content;

import codes.laivy.jhttp.utilities.pseudo.provided.PseudoCharset;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MediaType {

    // Static initializers

    @ApiStatus.Internal
    private static final @NotNull Pattern PARSE_PATTERN = Pattern.compile("([^;\\s]+)(?:;\\s*charset=([^;\\s]+))?(?:;\\s*(.*))?");

    public static @NotNull MediaType parse(@NotNull String string) throws ParseException {
        @NotNull Pattern pattern = Pattern.compile("([\\w-]+\\s*=\\s*[^;]+)|(^[^;]+)");
        @NotNull Matcher matcher = pattern.matcher(string);

        @Nullable Type type = null;
        @NotNull List<Parameter> parameters = new LinkedList<>();

        try {
            while (matcher.find()) {
                if (matcher.group(2) != null) {
                    type = Type.parse(matcher.group(2));
                } else {
                    @NotNull String[] split = matcher.group(1).split("\\s*=\\s*");
                    parameters.add(new Parameter(split[0], split[1]));
                }
            }
        } catch (@NotNull Throwable throwable) {
            throw new ParseException("cannot parse media type '" + string + "': " + throwable.getMessage(), -1);
        }

        if (type == null) {
            throw new ParseException("cannot obtain name in media type text '" + string + "'", -1);
        }

        return new MediaType(type, parameters.toArray(new Parameter[0]));
    }
    public static @NotNull MediaType create(@NotNull Type type, @NotNull Parameter @NotNull ... parameters) {
        return new MediaType(type, parameters);
    }

    // Object

    private final @NotNull Type type;
    private final @NotNull Parameter[] parameters;

    private MediaType(
            @NotNull Type type,
            @NotNull Parameter @NotNull [] parameters
    ) {
        this.type = type;
        this.parameters = parameters;
    }

    // Getters

    public @NotNull Type getType() {
        return type;
    }

    public @NotNull Parameter[] getParameters() {
        return parameters;
    }
    public @NotNull Optional<Parameter> getParameter(@NotNull String key) {
        return Arrays.stream(parameters).filter(p -> p.getKey().equalsIgnoreCase(key)).findFirst();
    }

    public @Nullable PseudoCharset getCharset() {
        @Nullable Parameter parameter = getParameter("charset").orElse(null);
        return parameter != null ? PseudoCharset.create(parameter.getValue()) : null;
    }
    public @Nullable Boundary getBoundary() {
        @Nullable Parameter parameter = getParameter("boundary").orElse(null);

        if (parameter != null) {
            return new Boundary(parameter.getValue());
        } else {
            return null;
        }
    }

    // Implementations

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        @NotNull MediaType that = (MediaType) o;
        return Objects.equals(type, that.type) && Arrays.equals(parameters, that.parameters);
    }
    @Override
    public int hashCode() {
        return Objects.hash(type, Arrays.hashCode(getParameters()));
    }

    @Override
    public @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder();
        builder.append(getType());

        for (@NotNull Parameter parameter : getParameters()) {
            builder.append("; ").append(parameter);
        }

        return builder.toString();
    }

    // Classes

    public static final class Type implements CharSequence {

        // Static initializers

        public static @NotNull Type parse(@NotNull String string) {
            @NotNull String[] split = string.split("/", 2);

            if (split.length > 1) {
                return new Type(split[0], split[1]);
            } else {
                return new Type(split[0], null);
            }
        }

        // Object

        private final @NotNull String type;
        private final @Nullable String subtype;

        public Type(@NotNull String type, @Nullable String subtype) {
            this.type = type;
            this.subtype = subtype;

            if ((type.contains(";") || type.contains(",")) || (subtype != null && (subtype.contains(";") || subtype.contains(",")))) {
                throw new IllegalArgumentException("type or subtype with illegal characters");
            }
        }

        // Getters

        public @NotNull String getType() {
            return type;
        }
        public @Nullable String getSubType() {
            return subtype;
        }

        public boolean isMultipart() {
            return getType().equalsIgnoreCase("multipart");
        }

        // Implementations

        @Override
        public int length() {
            return toString().length();
        }
        @Override
        public char charAt(int index) {
            return toString().charAt(index);
        }

        @Override
        public @NotNull CharSequence subSequence(int start, int end) {
            return toString().subSequence(start, end);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Type type = (Type) o;
            return this.type.equalsIgnoreCase(type.type) && (subtype != null && type.subtype != null && subtype.equalsIgnoreCase(type.subtype));
        }
        @Override
        public int hashCode() {
            return Objects.hash(type.toLowerCase(), (subtype != null ? subtype.toLowerCase() : null));
        }
        @Override
        public @NotNull String toString() {
            return type + (subtype != null ? "/" + subtype : "");
        }

    }

    public static final class Parameter {

        private final @NotNull String key;
        private final @NotNull String value;

        public Parameter(@NotNull String key, @NotNull String value) {
            this.key = key;
            this.value = value;

            if (key.contains(";") || key.contains(",") || value.contains(";") || value.contains(",")) {
                throw new IllegalArgumentException("content type parameter key or value with illegal characters");
            }
        }

        // Getters

        public @NotNull String getKey() {
            return key;
        }
        public @NotNull String getValue() {
            return value;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            @NotNull Parameter parameter = (Parameter) o;
            return Objects.equals(getKey(), parameter.getKey()) && Objects.equals(getValue(), parameter.getValue());
        }
        @Override
        public int hashCode() {
            return Objects.hash(getKey(), getValue());
        }

        @Override
        public @NotNull String toString() {
            return getKey() + "=" + getValue();
        }

    }

    public static final class Boundary implements CharSequence {

        private final @NotNull String name;

        public Boundary(@NotNull String name) {
            this.name = name;
        }

        public @NotNull String getName() {
            return name;
        }

        // Implementations

        @Override
        public int length() {
            return name.length();
        }
        @Override
        public char charAt(int index) {
            return name.charAt(index);
        }

        @Override
        public @NotNull CharSequence subSequence(int start, int end) {
            return name.subSequence(start, end);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Boundary boundary = (Boundary) o;
            return Objects.equals(name, boundary.name);
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        @Override
        public @NotNull String toString() {
            return name;
        }
    }

}
