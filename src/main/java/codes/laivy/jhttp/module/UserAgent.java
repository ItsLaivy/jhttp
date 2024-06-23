package codes.laivy.jhttp.module;

import codes.laivy.jhttp.utilities.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

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

        public static @NotNull Product create(
                @NotNull String name,
                @Nullable String version,

                @NotNull String @NotNull [] comments
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

        public static @NotNull UserAgent deserialize(@NotNull String string) throws IllegalStateException {
            @NotNull Map<String, String[]> map = new LinkedHashMap<>();

            for (@NotNull String part : string.split("\\s*(?![^()]*\\))")) {
                if (StringUtils.isBlank(part)) {
                    continue;
                }

                if (part.startsWith("(") && part.endsWith(")")) { // Comment
                    // Product and comment
                    @NotNull String product = map.keySet().stream().findFirst().orElseThrow(() -> new IllegalStateException("comment without products"));
                    @NotNull String[] comments = map.get(product);

                    // Remove parenthesis
                    part = part.substring(1, part.length() - 1);

                    // Add comment to array
                    comments = Arrays.copyOfRange(comments, 0, comments.length + 1);
                    comments[comments.length - 1] = part;
                    map.put(product, comments);
                } else { // Product
                    map.put(part, new String[0]);
                }
            }

            // Map into a product array
            @NotNull Product[] products = new Product[map.size()];

            int row = 0;
            for (@NotNull Entry<String, String[]> entry : map.entrySet()) {
                // Name and version
                @NotNull String[] split = entry.getKey().split("/", 2);

                @NotNull String name = split[0];
                @Nullable String version = split.length == 2 ? split[1] : null;

                // Comments
                @NotNull String[] comments = entry.getValue();

                // Instance
                products[row] = Product.create(name, version, comments);
                row++;
            }

            // Finish
            return create(products);
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
