package codes.laivy.jhttp.url.csp;

import codes.laivy.jhttp.exception.parser.FilesystemProtocolException;
import codes.laivy.jhttp.url.Blob;
import codes.laivy.jhttp.url.Data;
import codes.laivy.jhttp.url.FileSystem;
import codes.laivy.jhttp.url.MediaStream;
import codes.laivy.jhttp.url.domain.Domain;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

public class ContentSecurityPolicy {

    public interface Source {

        // Static initializers

        static @NotNull Source parse(@NotNull String string) throws ParseException, FilesystemProtocolException {
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
}
