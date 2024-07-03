package codes.laivy.jhttp.protocol;

import codes.laivy.jhttp.element.response.HttpResponse;
import codes.laivy.jhttp.protocol.factory.HttpHeaderFactory;
import codes.laivy.jhttp.protocol.factory.HttpRequestFactory;
import codes.laivy.jhttp.protocol.factory.HttpResponseFactory;
import org.jetbrains.annotations.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class HttpVersion {
    
    // Static initializers

    private static @NotNull HttpVersion version(@NotNull String name) {
        try {
            //noinspection unchecked
            @NotNull Class<HttpVersion> clazz = (Class<HttpVersion>) Class.forName(name);
            @NotNull Constructor<HttpVersion> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("cannot obtain class/constructor '" + name + "' for http version", e);
        }
    }

    @ApiStatus.Internal
    private static final @NotNull Set<HttpVersion> versions = new TreeSet<>(Comparator.comparingInt(o -> (o.getMajor() + o.getMinor())));

    public static @NotNull HttpVersion[] getVersions() {
        return versions.toArray(new HttpVersion[0]);
    }
    public static @NotNull HttpVersion getVersion(@NotNull String string) throws NullPointerException {
        @NotNull Optional<HttpVersion> optional = Arrays.stream(getVersions()).filter(version -> version.toString().equalsIgnoreCase(string)).findFirst();
        return optional.orElseThrow(() -> new NullPointerException("cannot find the HTTP version '" + string + "'"));
    }

    public static @NotNull HttpVersion HTTP1_0() {
        return Arrays.stream(getVersions()).filter(version -> version.getMajor() == 1 && version.getMinor() == 0).findFirst().orElseThrow(NullPointerException::new);
    }
    public static @NotNull HttpVersion HTTP1_1() {
        return Arrays.stream(getVersions()).filter(version -> version.getMajor() == 1 && version.getMinor() == 1).findFirst().orElseThrow(NullPointerException::new);
    }

    // Initialization

    static {
        for (@NotNull String name : new String[] {
                "codes.laivy.jhttp.protocol.v1_0.HttpVersion1_0",
                "codes.laivy.jhttp.protocol.v1_1.HttpVersion1_1"
        }) {
            try {
                //noinspection unchecked
                @NotNull Class<HttpVersion> clazz = (Class<HttpVersion>) Class.forName(name);

                @NotNull Constructor<HttpVersion> constructor = clazz.getConstructor();
                constructor.setAccessible(true);

                @NotNull HttpVersion version = constructor.newInstance();

                // Initialize
                version.init();
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("cannot load provided http version '" + name + "'", e);
            }
        }
    }

    // Object

    private final byte[] id;
    private final int major;
    private final int minor;

    protected HttpVersion(byte[] id, int major, int minor) {
        this.id = id;
        this.major = major;
        this.minor = minor;
    }

    // Modules

    /**
     * Retrieves the byte identification sequence used in the Application-Layer Protocol Negotiation (ALPN).
     * <p>
     * The byte sequence returned by this method corresponds to the ALPN protocol ID sequence, which is used to
     * identify the protocol to be used for communication over a given connection. This method plays a crucial
     * role in ensuring that the correct protocol is negotiated between a client and a server during the
     * establishment of a secure connection.
     * </p>
     * <p>
     * For more details, refer to the <a href="https://developer.mozilla.org/en-US/docs/Glossary/ALPN">Mozilla ALPN Protocol ID Sequence</a> documentation.
     * </p>
     *
     * @return a byte array representing the ALPN protocol identification sequence.
     * @author Daniel Richard (Laivy)
     * @since 1.0-SNAPSHOT
     */
    public byte[] getId() {
        return id;
    }

    @MustBeInvokedByOverriders
    public synchronized boolean init() {
        return versions.add(this);
    }
    @MustBeInvokedByOverriders
    public synchronized void close() {
        versions.remove(this);
    }

    // Getters

    public abstract @NotNull HttpRequestFactory getRequestFactory();
    public abstract @NotNull HttpResponseFactory getResponseFactory();
    public abstract @NotNull HttpHeaderFactory getHeaderFactory();

    @Contract(pure = true)
    public final int getMajor() {
        return major;
    }
    @Contract(pure = true)
    public final int getMinor() {
        return minor;
    }

    // Modules

    /**
     * Determines whether the connection should be closed or not based on the HTTP version and headers of the response.
     * <p>
     * This method addresses the differences in connection management between HTTP/1.0 and HTTP/1.1:
     * <ul>
     *   <li>In HTTP/1.0, connections are closed by default unless the {@code Connection: keep-alive} header is present.</li>
     *   <li>In HTTP/1.1, connections are kept alive by default unless the {@code Connection: close} header is present.</li>
     * </ul>
     * Implementations of this method should analyze the given {@link HttpResponse} and determine if the connection
     * should be closed based on the presence and value of the {@code Connection} header, as well as the HTTP version.
     * <p>
     *
     * @param response the HTTP response to evaluate; must not be null
     * @return {@code true} if the connection should be closed, {@code false} otherwise
     * @throws NullPointerException if {@code response} is null
     */
    public abstract boolean shouldClose(@NotNull HttpResponse response);

    // Implementations

    @Override
    public final boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof HttpVersion)) return false;
        HttpVersion that = (HttpVersion) object;
        return getMinor() == that.getMinor() && getMajor() == that.getMajor();
    }
    @Override
    public final int hashCode() {
        return Objects.hash(getMinor(), getMajor());
    }

    @Override
    public final @NotNull String toString() {
        return "HTTP/" + getMajor() + "." + getMinor();
    }

}
