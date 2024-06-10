package codes.laivy.jhttp.url.csp;

import codes.laivy.jhttp.url.Blob;
import codes.laivy.jhttp.url.Data;
import codes.laivy.jhttp.url.domain.Domain;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

public class ContentSecurityPolicy {



    public interface Source {

        // Static initializers

        static @NotNull ContentSecurityPolicy.Source parse(@NotNull String string) throws ParseException {
            if (Data.validate(string)) {
                return Data.parse(string);
            } else if (Blob.validate(string)) {
                return Blob.parse(string);
            } else if (Domain.validate(string)) {
                return Domain.parse(string);
            } else {
                throw new ParseException("cannot parse '" + string + "' as a valid CSP source", 0);
            }
        }

        // Object

        // Getters

        @NotNull
        ContentSecurityPolicy.Source.Type getType();

        /**
         * Enumeration representing various types of sources for Content Security Policy (CSP).
         * These sources define the allowed locations from which resources can be loaded and executed.
         *
         * @author Daniel Richard (Laivy)
         * @since 1.0-SNAPSHOT
         */
        enum Type {

            /**
             * Internet host by name or IP address. The URL scheme, port number, and path are optional.
             * Wildcards ('*') can be used for subdomains, host address, and port number, indicating that all
             * legal values of each are valid. When matching schemes, secure upgrades are allowed (e.g.,
             * specifying <a href="http://example.com">.<a href="..</a>">will match h</a>ttps://example.com).
             * That type matches with WebSocket URLs also. These URLs use the ws: or wss: schemes for WebSocket connections.
             * <p>
             * Example: example.com, *.example.com:80, 192.168.0.1
             * </p>
             *
             * @see <a href="https://developer.mozilla.org/en-US/docs/Learn/Common_questions/Web_mechanics/What_is_a_URL">URL Scheme</a>
             */
            DOMAIN,

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
