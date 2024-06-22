package codes.laivy.jhttp.module.content;

import codes.laivy.jhttp.exception.parser.FilesystemProtocolException;
import codes.laivy.jhttp.url.Blob;
import codes.laivy.jhttp.url.Data;
import codes.laivy.jhttp.url.FileSystem;
import codes.laivy.jhttp.url.MediaStream;
import codes.laivy.jhttp.url.domain.Domain;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Stream;

import static codes.laivy.jhttp.module.content.ContentSecurityPolicy.Source;

public final class ContentSecurityPolicy implements Iterable<Source> {

    // Static initializers

    public static boolean validate(@NotNull String string) {
        // todo: CSP validator
        return true;
    }
    public static @NotNull ContentSecurityPolicy parse(@NotNull String string) throws ParseException, UnsupportedEncodingException, FilesystemProtocolException {
        if (validate(string)) {
            @NotNull String[] parts = string.split(" ");
            @NotNull List<Source> sources = new LinkedList<>();

            for (@NotNull String part : parts) {
                if (part.isEmpty()) continue;

                if (part.startsWith("'") && part.endsWith("'")) {
                    sources.add(Keyword.custom(part.substring(1, part.length() - 1)));
                } else {
                    sources.add(Source.parse(part));
                }
            }

            return create(sources.toArray(new Source[0]));
        } else {
            throw new ParseException("cannot parse '" + string + "' into a valid content security policy", 0);
        }
    }

    public static @NotNull ContentSecurityPolicy create(@NotNull Source @NotNull ... sources) {
        return new ContentSecurityPolicy(sources);
    }

    // Object

    private final @NotNull Source @NotNull [] sources;

    private ContentSecurityPolicy(@NotNull Source @NotNull [] sources) {
        this.sources = sources;
    }

    @Override
    public @NotNull Iterator<Source> iterator() {
        return stream().iterator();
    }
    public @NotNull Stream<Source> stream() {
        return Arrays.stream(sources);
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull ContentSecurityPolicy sources1 = (ContentSecurityPolicy) object;
        return Objects.deepEquals(sources, sources1.sources);
    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(sources);
    }

    @Override
    public @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder();

        for (@NotNull Source source : this) {
            if (builder.length() > 0) builder.append(" ");

            if (source instanceof Keyword) {
                builder.append("'").append(source).append("'");
            } else {
                builder.append(source);
            }
        }

        return builder.toString();
    }

    // Classes

    public interface Source {

        // Static initializers

        static @NotNull Source parse(@NotNull String string) throws ParseException, FilesystemProtocolException, UnsupportedEncodingException {
            if (Data.validate(string)) {
                return Data.parse(string);
            } else if (Blob.validate(string)) {
                return Blob.parse(string);
            } else if (FileSystem.validate(string)) {
                return FileSystem.parse(string);
            } else if (MediaStream.validate(string)) {
                return MediaStream.parse(string);
            } else if (Domain.validate(string)) {
                return Domain.parse(string);
            } else {
                throw new ParseException("cannot parse '" + string + "' as a valid CSP source", 0);
            }
        }

        /**
         * Enumeration representing various types of schemes for Content Security Policy (CSP).
         * These sources define the allowed locations from which resources can be loaded and executed.
         *
         * @author Daniel Richard (Laivy)
         * @since 1.0-SNAPSHOT
         */
        enum Scheme {

            /**
             * Data URLs. These URLs embed small data directly in the URL and use the data: scheme.
             * <p>
             * Example: data:image/png;base64,iVBORw0KGgoNAANSUhEUgAAA...
             * </p>
             */
            DATA,

            /**
             * MediaStream URLs. These URLs represent a media stream and use the mediastream: scheme.
             * <p>
             * Example: mediastream:myStream
             * </p>
             */
            MEDIASTREAM,

            /**
             * Blob URLs. These URLs represent binary large objects and use the blob: scheme.
             * <p>
             * Example: blob:<a href="https://example.com/550e8400-e29b-41d4-a716-446655440000">...</a>
             * </p>
             */
            BLOB,

            /**
             * FileSystem URLs. These URLs represent files in a sandboxed file system and use the filesystem: scheme.
             * <p>
             * Example: filesystem:<a href="https://example.com/temporary/myFile">...</a>
             * </p>
             */
            FILESYSTEM,

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

            if (name.contains(" ")) {
                throw new IllegalArgumentException("CSP source keyword names cannot have ' ' characters");
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
            return name;
        }

    }

}
