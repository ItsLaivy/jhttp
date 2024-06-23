package codes.laivy.jhttp.module.content;

import codes.laivy.jhttp.exception.parser.FilesystemProtocolException;
import codes.laivy.jhttp.module.Origin;
import codes.laivy.jhttp.module.content.ContentSecurityPolicy.Directive.Keyword;
import codes.laivy.jhttp.module.content.ContentSecurityPolicy.Directive.Name;
import codes.laivy.jhttp.url.*;
import codes.laivy.jhttp.url.domain.Domain;
import codes.laivy.jhttp.utilities.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Stream;

import static codes.laivy.jhttp.module.content.ContentSecurityPolicy.*;

public final class ContentSecurityPolicy implements Iterable<Directive> {

    // Static initializers

    public static boolean validate(@NotNull String string) {
        // todo: CSP validator
        return true;
    }
    public static @NotNull ContentSecurityPolicy parse(@NotNull String string) throws ParseException, UnsupportedEncodingException, FilesystemProtocolException, UnknownHostException, URISyntaxException {
        if (validate(string)) {
            @NotNull List<Directive> directives = new LinkedList<>();
            for (@NotNull String temp : string.split("\\s*;\\s*")) {
                @NotNull Name name = Name.getById(temp.split(" ", 2)[0]);
                @NotNull Directive.Builder builder = new Directive.Builder(name);

                for (@NotNull String part : temp.split(" ", 2)[1].split(" ")) {
                    if (part.isEmpty()) continue;

                    if (part.startsWith("'") && part.endsWith("'")) {
                        builder.add(Keyword.custom(part.substring(1, part.length() - 1)));
                    } else try {
                        builder.add(Scheme.getByName(part));
                    } catch (@NotNull NullPointerException ignore) {
                        builder.add(Source.parse(part));
                    }
                }

                directives.add(builder.build());
            }


            return create(directives.toArray(new Directive[0]));
        } else {
            throw new ParseException("cannot parse '" + string + "' into a valid content security policy", 0);
        }
    }

    public static @NotNull ContentSecurityPolicy create(@NotNull Directive @NotNull ... directives) {
        return new ContentSecurityPolicy(directives);
    }

    // Object

    private final @NotNull Directive @NotNull [] directives;

    private ContentSecurityPolicy(@NotNull Directive @NotNull [] directives) {
        this.directives = directives;
    }

    @Override
    public @NotNull Iterator<Directive> iterator() {
        return stream().iterator();
    }
    public @NotNull Stream<Directive> stream() {
        return Arrays.stream(directives);
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull ContentSecurityPolicy sources1 = (ContentSecurityPolicy) object;
        return Objects.deepEquals(directives, sources1.directives);
    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(directives);
    }

    @Override
    public @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder();

        for (@NotNull Directive directive : this) {
            if (builder.length() > 0) builder.append("; ");
            builder.append(directive);
        }

        return builder.toString();
    }

    // Classes

    public static final class Directive {

        // Static initializers

        public static @NotNull Builder builder(@NotNull Name name) {
            return new Builder(name);
        }
        public static @NotNull Directive create(@NotNull Name name, @NotNull Source @NotNull ... sources) {
            return new Directive(name, sources);
        }

        // Object

        private final @NotNull Name name;
        private final @NotNull Source @NotNull [] values;

        private Directive(@NotNull Name name, @NotNull Source @NotNull [] values) {
            this.name = name;
            this.values = values;
        }

        // Getters

        public @NotNull Name getName() {
            return name;
        }
        public @NotNull Source @NotNull [] getValues() {
            return values;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            @NotNull Directive directive = (Directive) object;
            return Objects.equals(getName(), directive.getName()) && Objects.deepEquals(getValues(), directive.getValues());
        }
        @Override
        public int hashCode() {
            return Objects.hash(getName(), Arrays.hashCode(getValues()));
        }

        @Override
        public @NotNull String toString() {
            @NotNull StringBuilder builder = new StringBuilder(getName().getId());

            for (@NotNull Source value : getValues()) {
                builder.append(" ").append(value);
            }

            return builder.toString();
        }

        // Classes

        public enum Name {
            CONNECT_SRC("connect-src"),
            DEFAULT_SRC("default-src"),
            FENCED_FRAME_SRC("fenced-frame-src"),
            FONT_SRC("font-src"),
            FRAME_SRC("frame-src"),
            IMG_SRC("img-src"),
            MANIFEST_SRC("manifest-src"),
            MEDIA_SRC("media-src"),
            OBJECT_SRC("object-src"),
            PREFETCH_SRC("prefetch-src"),
            SCRIPT_SRC("script-src"),
            SCRIPT_SRC_ELEM("script-src-elem"),
            SCRIPT_SRC_ATTR("script-src-attr"),
            STYLE_SRC("style-src"),
            STYLE_SRC_ELEM("style-src-elem"),
            STYLE_SRC_ATTR("style-src-attr"),
            WORKER_SRC("worker-src"),
            BASE_URI("base-uri"),
            SANDBOX("sandbox"),
            FORM_ACTION("form-action"),
            FRAME_ANCESTORS("frame-ancestors"),
            REPORT_URI("report-uri"),
            REPORT_TO("report-to"),
            REQUIRE_TRUSTED_TYPES_FOR("require-trusted-types-for"),
            TRUSTED_TYPES("trusted-types"),
            UPGRADE_INSECURE_REQUESTS("upgrade-insecure-requests"),
            @Deprecated
            BLOCK_ALL_MIXED_CONTENT("block-all-mixed-content"),
            @Deprecated
            PLUGIN_TYPES("plugin-types"),
            @Deprecated
            REFERRER("referrer"),
            ;

            private final @NotNull String id;

            Name(@NotNull String id) {
                this.id = id;
            }

            public @NotNull String getId() {
                return id;
            }

            // Static initializers

            public static @NotNull Name getById(@NotNull String id) {
                @NotNull Optional<Name> optional = Arrays.stream(values()).filter(name -> name.getId().equalsIgnoreCase(id)).findFirst();
                return optional.orElseThrow(() -> new NullPointerException("there's no directive name with id '" + id + "'"));
            }

        }
        public static final class Keyword implements Source {

            // Static initializers

            public static @NotNull Keyword self() {
                return new Keyword("self");
            }
            public static @NotNull Keyword unsafe_eval() {
                return new Keyword("unsafe_eval");
            }
            public static @NotNull Keyword wasm_unsafe_eval() {
                return new Keyword("wasm-unsafe-eval");
            }
            public static @NotNull Keyword unsafe_hashes() {
                return new Keyword("unsafe-hashes");
            }
            public static @NotNull Keyword unsafe_inline() {
                return new Keyword("unsafe-inline");
            }
            public static @NotNull Keyword none() {
                return new Keyword("none");
            }
            public static @NotNull Keyword nonce(@NotNull String value) {
                return new Keyword("nonce-" + value);
            }
            public static @NotNull Keyword inline_speculation_rules() {
                return new Keyword("inline-speculation-rules");
            }
            public static @NotNull Keyword report_sample() {
                return new Keyword("report-sample");
            }
            public static @NotNull Keyword strict_dynamic() {
                return new Keyword("strict-dynamic");
            }

            public static @NotNull Keyword sha256(@NotNull String value) {
                if (value.length() != 64) {
                    throw new IllegalArgumentException("the sha256 value length should have 64 characters");
                }

                return new Keyword("sha256-" + value);
            }
            public static @NotNull Keyword sha384(@NotNull String value) {
                if (value.length() != 96) {
                    throw new IllegalArgumentException("the sha384 value length should have 96 characters");
                }

                return new Keyword("sha384-" + value);
            }
            public static @NotNull Keyword sha512(@NotNull String value) {
                if (value.length() != 128) {
                    throw new IllegalArgumentException("the sha512 value length should have 128 characters");
                }

                return new Keyword("sha512-" + value);
            }

            @ApiStatus.Experimental
            public static @NotNull Keyword custom(@NotNull String key) {
                return new Keyword(key);
            }

            // Object

            private final @NotNull String name;

            private Keyword(@NotNull String name) {
                this.name = name;

                if (name.contains(" ") || name.contains("'") || name.contains("\"") || StringUtils.isBlank(name)) {
                    throw new IllegalArgumentException("illegal CSP directive value keyword name");
                }
            }

            // Getters

            public @NotNull String getName() {
                return name;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                @NotNull Keyword keyword = (Keyword) object;
                return Objects.equals(name, keyword.name);
            }
            @Override
            public int hashCode() {
                return Objects.hashCode(name);
            }

            @Override
            public @NotNull String toString() {
                return "'" + name + "'";
            }

        }
        public static final class Builder {

            private final @NotNull Name name;
            private final @NotNull List<Source> sources = new ArrayList<>();

            private Builder(@NotNull Name name) {
                this.name = name;
            }

            // Modules

            @Contract("_->this")
            public @NotNull Builder add(@NotNull Scheme scheme) {
                sources.add(scheme);
                return this;
            }
            @Contract("_->this")
            public @NotNull Builder add(@NotNull Keyword keyword) {
                sources.add(keyword);
                return this;
            }
            @Contract("_->this")
            public @NotNull Builder add(@NotNull Source source) {
                sources.add(source);
                return this;
            }

            // Builder

            public @NotNull Directive build() {
                if (sources.isEmpty()) sources.add(Keyword.none());
                return new Directive(name, sources.toArray(new Source[0]));
            }

        }
    }

    public interface Source {

        // Static initializers

        static @NotNull Source parse(@NotNull String string) throws ParseException, FilesystemProtocolException, UnsupportedEncodingException, UnknownHostException, URISyntaxException {
            if (Data.validate(string)) {
                return Data.parse(string);
            } else if (Blob.validate(string)) {
                return Blob.parse(string);
            } else if (FileSystem.validate(string)) {
                return FileSystem.parse(string);
            } else if (MediaStream.validate(string)) {
                return MediaStream.parse(string);
            }else if (Origin.Parser.validate(string)) {
                return Origin.Parser.deserialize(string);
            } else if (Domain.validate(string)) {
                return Domain.parse(string);
            } else {
                throw new ParseException("cannot parse '" + string + "' as a valid CSP source", 0);
            }
        }

    }

    /**
     * Enumeration representing various types of schemes for Content Security Policy (CSP).
     * These sources define the allowed locations from which resources can be loaded and executed.
     *
     * @author Daniel Richard (Laivy)
     * @since 1.0-SNAPSHOT
     */
    public enum Scheme implements Source {

        /**
         * Data URLs. These URLs embed small data directly in the URL and use the data: scheme.
         * <p>
         * Example: data:image/png;base64,iVBORw0KGgoNAANSUhEUgAAA...
         * </p>
         */
        DATA("data:"),

        /**
         * MediaStream URLs. These URLs represent a media stream and use the mediastream: scheme.
         * <p>
         * Example: mediastream:myStream
         * </p>
         */
        MEDIASTREAM("mediastream:"),

        /**
         * Blob URLs. These URLs represent binary large objects and use the blob: scheme.
         * <p>
         * Example: blob:https://example.com/550e8400-e29b-41d4-a716-446655440000
         * </p>
         */
        @SuppressWarnings("JavadocLinkAsPlainText")
        BLOB("blob:"),

        /**
         * FileSystem URLs. These URLs represent files in a sandboxed file system and use the filesystem: scheme.
         * <p>
         * Example: filesystem:<a href="https://example.com/temporary/myFile">...</a>
         * </p>
         */
        FILESYSTEM("filesystem:"),

        HTTP("http:"),
        HTTPS("https:"),

        ;

        private final @NotNull String name;

        Scheme(@NotNull String name) {
            this.name = name;
        }

        public @NotNull String getName() {
            return name;
        }

        // Implementations

        @Override
        public @NotNull String toString() {
            return getName();
        }

        // Static initializers

        public static @NotNull Scheme getByName(@NotNull String name) {
            @NotNull Optional<Scheme> optional = Arrays.stream(values()).filter(scheme -> scheme.getName().equalsIgnoreCase(name)).findFirst();
            return optional.orElseThrow(() -> new NullPointerException("there's no scheme with name '" + name + "'"));
        }

    }
}
