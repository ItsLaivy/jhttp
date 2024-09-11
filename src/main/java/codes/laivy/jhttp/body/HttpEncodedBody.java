package codes.laivy.jhttp.body;

import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.encoding.Encoding;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents an HTTP body that has been encoded with transfer and content encodings that may not be
 * supported or decoded by the current JHTTP environment.
 *
 * <p>This class is used to handle HTTP bodies when the encodings applied during transmission or storage
 * are not available for decoding in the JHTTP library. The body content provided by this class remains
 * encoded and unprocessed. The input stream or byte array provided to this class will be the raw encoded
 * data without any form of decoding, including the available encodings within JHTTP.</p>
 *
 * <p>Transfer encodings are applied during the transmission of the HTTP body, while content encodings
 * are used to compress or otherwise modify the body content. This class does not perform any decoding on
 * the provided data; instead, it allows handling of the encoded data until an appropriate decoding method
 * is available.</p>
 *
 * <p>Usage Scenarios:
 * <ul>
 * <li>When an HTTP body is received with encodings (some or all) that are not supported by JHTTP, an instance of {@link HttpEncodedBody}
 * is created to hold the raw encoded data.</li>
 * <li>This class is useful in cases where you need to store or transmit encoded HTTP bodies without processing
 * them until the appropriate decoders are available.</li>
 * </ul></p>
 *
 * @author Daniel Meinicke (Laivy)
 * @since 1.0-SNAPSHOT
 */
public class HttpEncodedBody extends HttpBigBody {

    private final @NotNull Deferred<Encoding> @NotNull [] transferEncodings;
    private final @NotNull Deferred<Encoding> @NotNull [] contentEncodings;

    /**
     * Constructs an {@link HttpEncodedBody} with the specified byte array and encoding information.
     *
     * @param bytes the raw encoded body data as a byte array
     * @param transferEncodings an array of {@link Deferred<Encoding>} objects for the transfer encodings
     * @param contentEncodings an array of {@link Deferred<Encoding>} objects for the content encodings
     * @throws IOException if an I/O error occurs while initializing the body
     */
    public HttpEncodedBody(byte @NotNull [] bytes, @NotNull Deferred<Encoding> @NotNull [] transferEncodings, @NotNull Deferred<Encoding> @NotNull [] contentEncodings) throws IOException {
        super(bytes);
        this.transferEncodings = transferEncodings;
        this.contentEncodings = contentEncodings;
    }

    /**
     * Constructs an {@link HttpEncodedBody} with the specified input stream and encoding information.
     *
     * @param stream the raw encoded body data as an input stream
     * @param transferEncodings an array of {@link Deferred<Encoding>} objects for the transfer encodings
     * @param contentEncodings an array of {@link Deferred<Encoding>} objects for the content encodings
     * @throws IOException if an I/O error occurs while initializing the body
     */
    public HttpEncodedBody(@NotNull InputStream stream, @NotNull Deferred<Encoding> @NotNull [] transferEncodings, @NotNull Deferred<Encoding> @NotNull [] contentEncodings) throws IOException {
        super(stream);
        this.transferEncodings = transferEncodings;
        this.contentEncodings = contentEncodings;
    }

    // Getters

    /**
     * Returns the array of {@link Deferred<Encoding>} objects representing the transfer encodings applied to the HTTP body.
     *
     * @return an array of transfer encodings
     */
    public @NotNull Deferred<Encoding> @NotNull [] getTransferEncodings() {
        return transferEncodings;
    }
    /**
     * Returns the array of {@link Deferred<Encoding>} objects representing the content encodings applied to the HTTP body content.
     *
     * @return an array of content encodings
     */
    public @NotNull Deferred<Encoding> @NotNull [] getContentEncodings() {
        return contentEncodings;
    }

}