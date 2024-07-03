package codes.laivy.jhttp.module;

import codes.laivy.jhttp.utilities.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface UserAgent {

    // Static initializers

    static @NotNull UserAgent create(
            final @NotNull Product @NotNull [] products
    ) {
        return new UserAgent() {

            // Object

            @Override
            public @NotNull Product @NotNull [] getProducts() {
                return products;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                @NotNull UserAgent agent = (UserAgent) object;
                return Arrays.equals(getProducts(), agent.getProducts());
            }
            @Override
            public int hashCode() {
                return Arrays.hashCode(getProducts());
            }
            @Override
            public @NotNull String toString() {
                return Parser.serialize(this);
            }

        };
    }

    // Getters

    @NotNull Product @NotNull [] getProducts();

    // Classes

    final class Product {

        // Static initializers

        public static @NotNull Product parse(@NotNull String string) throws ParseException {
            @NotNull Matcher matcher = Pattern.compile("^(?<name>[^/()]+)(?:/(?<version>[^ ()]+))? *(?<comments>\\(.*?\\))*").matcher(string);

            if (matcher.find()) {
                @NotNull String name = matcher.group("name").trim();
                @Nullable String version = matcher.group("version") != null && !matcher.group("version").isEmpty() ? matcher.group("version") : null;
                @NotNull String @NotNull [] comments = matcher.group("comments") != null ? Arrays.stream(matcher.group("comments").split("\\s+(?![^(]*\\))")).map(str -> str.substring(1, str.length() - 1).trim()).toArray(String[]::new) : new String[0];

                // Finish
                return new Product(name, version, comments);
            } else {
                throw new ParseException("cannot parse '" + string + "' as a valid user agent product", 0);
            }
        }

        public static @NotNull Product create(
                @NotNull String name,
                @Nullable String version,

                @NotNull String @NotNull ... comments
        ) {
            return new Product(name, version, comments);
        }

        // Object

        private final @NotNull String name;
        private final @Nullable String version;

        private final @NotNull String @NotNull [] comments;

        private Product(@NotNull String name, @Nullable String version, @NotNull String @NotNull [] comments) {
            this.name = name;
            this.version = version;
            this.comments = comments;

            // Validate name and version
            if ((name.contains("\\") || name.contains("/")) || (version != null && (version.contains("\\") || version.contains("/")))) {
                throw new IllegalArgumentException("illegal product/version names");
            }

            // Validate comments
            for (@NotNull String comment : getComments()) {
                if (comment.contains("(") || comment.contains(")")) {
                    throw new IllegalArgumentException("comment '" + comment + "' with illegal characters");
                }
            }
        }

        // Getters

        public @NotNull String getName() {
            return this.name;
        }
        public @Nullable String getVersion() {
            return this.version;
        }

        public @NotNull String @NotNull [] getComments() {
            return comments;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            @NotNull Product product = (Product) object;
            return Objects.equals(getName(), product.getName()) && Objects.equals(getVersion(), product.getVersion());
        }
        @Override
        public int hashCode() {
            return Objects.hash(getName(), getVersion());
        }

        @Override
        public @NotNull String toString() {
            @NotNull StringBuilder builder = new StringBuilder();
            builder.append(getName()).append(getVersion() != null ? "/" + getVersion() : "");

            for (@NotNull String comment : getComments()) {
                if (StringUtils.isBlank(comment)) {
                    continue;
                }

                builder.append(" (").append(comment).append(")");
            }

            return builder.toString();
        }

    }
    final class Parser {
        private Parser() {
            throw new UnsupportedOperationException();
        }

        // Serializers

        public static @NotNull String serialize(@NotNull UserAgent agent) {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull Product product : agent.getProducts()) {
                if (builder.length() > 0) builder.append(" ");
                builder.append(product);
            }

            return builder.toString();
        }

        public static @NotNull UserAgent deserialize(@NotNull String string) throws IllegalStateException, ParseException {
            @NotNull List<Product> products = new LinkedList<>();

            for (@NotNull String part : string.split("\\s(?!\\([^)]*\\)|[^()]*\\))")) {
                products.add(Product.parse(part));
            }

            // Finish
            return create(products.toArray(new Product[0]));
        }

        public static boolean validate(@NotNull String string) {
            try {
                deserialize(string);
                return true;
            } catch (@NotNull Throwable throwable) {
                return false;
            }
        }

    }

}
