package codes.laivy.jhttp.protocol;

import codes.laivy.jhttp.protocol.v1_1.HttpVersion1_1;
import org.jetbrains.annotations.*;

import java.util.*;

public abstract class HttpVersion {

    // Static initializers

    @ApiStatus.Internal
    private static final @NotNull Set<HttpVersion> versions = new TreeSet<>(Comparator.comparingInt(o -> (o.getMajor() + o.getMinor())));

    public static @NotNull HttpVersion[] getVersions() {
        if (versions.stream().noneMatch(version -> version.getMajor() == 1 && version.getMinor() == 1)) {
            new HttpVersion1_1().init();
        }

        return versions.toArray(new HttpVersion[0]);
    }
    public static @NotNull HttpVersion getVersion(@NotNull String string) throws NullPointerException {
        @NotNull Optional<HttpVersion> optional = Arrays.stream(getVersions()).filter(version -> version.toString().equalsIgnoreCase(string)).findFirst();
        return optional.orElseThrow(() -> new NullPointerException("cannot find the HTTP version '" + string + "'"));
    }

    public static @NotNull HttpVersion HTTP1_1() {
        return Arrays.stream(getVersions()).filter(version -> version.getMajor() == 1 && version.getMinor() == 1).findFirst().orElseThrow(NullPointerException::new);
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

    public abstract @NotNull HttpFactory getFactory();

    @Contract(pure = true)
    public final int getMajor() {
        return major;
    }
    @Contract(pure = true)
    public final int getMinor() {
        return minor;
    }

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
    public @NotNull String toString() {
        return "HTTP/" + getMajor() + "." + getMinor();
    }

}
