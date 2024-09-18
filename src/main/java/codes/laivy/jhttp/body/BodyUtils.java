package codes.laivy.jhttp.body;

import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.headers.HttpHeader;
import codes.laivy.jhttp.headers.HttpHeaders;
import org.jetbrains.annotations.NotNull;

import static codes.laivy.jhttp.headers.HttpHeaderKey.CONTENT_ENCODING;
import static codes.laivy.jhttp.headers.HttpHeaderKey.TRANSFER_ENCODING;

public final class BodyUtils {

    // Static initializers

    @SuppressWarnings("unchecked")
    public static byte[] encode(@NotNull HttpHeaders headers, byte[] bytes) throws EncodingException {
        for (@NotNull Deferred<Encoding> deferred : headers.first(CONTENT_ENCODING).map(HttpHeader::getValue).orElse(new Deferred[0])) {
            @NotNull Encoding encoding = deferred.retrieve();

            bytes = encoding.compress(bytes);
        }
        for (@NotNull Deferred<Encoding> deferred : headers.first(TRANSFER_ENCODING).map(HttpHeader::getValue).orElse(new Deferred[0])) {
            @NotNull Encoding encoding = deferred.retrieve();

            // The chunked transfer encoding should always be the last
            if (deferred.toString().equalsIgnoreCase("chunked")) {
                continue;
            }

            bytes = encoding.compress(bytes);
        }

        return bytes;
    }
    public static byte[] decode(@NotNull Encoding[] content, @NotNull Encoding[] transfer, byte[] bytes) throws EncodingException {
        for (@NotNull Encoding encoding : content) {
            bytes = encoding.compress(bytes);
        }
        for (@NotNull Encoding encoding : transfer) {
            // The chunked transfer encoding should always be the last
            if (encoding.getName().equalsIgnoreCase("chunked")) {
                continue;
            }

            bytes = encoding.compress(bytes);
        }

        return bytes;
    }

    // Object

    private BodyUtils() {
        throw new UnsupportedOperationException("this class cannot be instantiated");
    }

}
