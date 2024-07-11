package codes.laivy.jhttp.client;

import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * Interface representing an HTTPS client that extends the {@link HttpClient}.
 * This interface provides methods to retrieve SSL context and engine details
 * necessary for establishing and managing secure connections.
 *
 * @author Daniel Richard (Laivy)
 * @version 1.0-SNAPSHOT
 */
public interface HttpsClient extends HttpClient {

    /**
     * Retrieves the SSL context used by the HTTPS client.
     * The SSL context is responsible for managing the SSL/TLS protocols
     * and associated cryptographic operations.
     *
     * @return the {@link SSLContext} used by the HTTPS client; never null.
     */
    @NotNull SSLContext getContext();

    /**
     * Retrieves the SSL engine used by the HTTPS client.
     * The SSL engine handles the encryption and decryption of data as it is
     * transmitted and received over the secure connection.
     *
     * @return the {@link SSLEngine} used by the HTTPS client; never null.
     */
    @NotNull SSLEngine getEngine();

}
