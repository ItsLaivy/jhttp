package codes.laivy.jhttp.content;

import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.utilities.URIAuthority;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AlternativeService {

    // Static initializers

    @ApiStatus.Internal
    public static final @NotNull Pattern PARSE_PATTERN = Pattern.compile("(\\w+(-\\d+)?)\\s*=\\s*\"(:\\d+)\"(?:\\s*;\\s*ma\\s*=\\s*(\\d+))?(?:\\s*;\\s*persist\\s*=\\s*(\\d+))?");

    public static boolean isAlternativeService(@NotNull String string) {
        return PARSE_PATTERN.matcher(string).matches();
    }
    public static @NotNull AlternativeService parse(@NotNull String string) throws ParseException {
        @NotNull Matcher matcher = PARSE_PATTERN.matcher(string);

        if (matcher.groupCount() != 5) {
            throw new ParseException("cannot parse '" + string + "' into a valid alternative service", 0);
        }

        byte[] protocol = matcher.group(1).getBytes();
        @NotNull URIAuthority authority;
        @NotNull Duration age;

        try {
            authority = URIAuthority.parse(matcher.group(3));
        } catch (@NotNull URISyntaxException | UnknownHostException e) {
            throw new ParseException("cannot parse '" + string.substring(matcher.start(3), matcher.end(3)) + "' into a valid uri authority: " + e.getMessage(), matcher.start(3));
        }

        try {
            age = Duration.ofSeconds(matcher.group(4) == null ? 86400 : Integer.parseInt(matcher.group(4)));
        } catch (@NotNull IllegalArgumentException ignore) {
            throw new ParseException("cannot parse '" + string.substring(matcher.start(4), matcher.end(4)) + "' into a valid alternative service age integer", matcher.start(4));
        }

        boolean persist = matcher.group(5) != null && matcher.group(5).trim().equals("1");

        if (age.isNegative()) {
            throw new IllegalStateException("negative alternative service age");
        }

        return new AlternativeService(protocol, authority, age, persist);
    }

    public static @NotNull AlternativeService create(@NotNull HttpVersion version, @NotNull URIAuthority authority) {
        return new AlternativeService(version.getId(), authority, Duration.ofDays(1), false);
    }
    public static @NotNull AlternativeService create(@NotNull HttpVersion version, @NotNull URIAuthority authority, @NotNull Duration age, boolean persist) {
        return new AlternativeService(version.getId(), authority, age, persist);
    }

    public static @NotNull AlternativeService create(byte[] protocolId, @NotNull URIAuthority authority, @NotNull Duration age, boolean persist) {
        return new AlternativeService(protocolId, authority, age, persist);
    }

    // Object

    private final byte[] version;
    private final @NotNull URIAuthority authority;
    private final @NotNull Duration age;
    private final boolean persistent;

    private AlternativeService(byte[] version, @NotNull URIAuthority authority, @NotNull Duration age, boolean persistent) {
        this.version = version;
        this.authority = authority;
        this.age = age;
        this.persistent = persistent;
    }

    // Getters

    public byte[] getVersion() {
        return version;
    }
    public @NotNull URIAuthority getAuthority() {
        return authority;
    }
    public @NotNull Duration getAge() {
        return age;
    }

    public boolean isPersistent() {
        return persistent;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlternativeService that = (AlternativeService) o;
        return Objects.equals(age, that.age) && persistent == that.persistent && Arrays.equals(version, that.version) && Objects.equals(authority, that.authority);
    }
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(version), authority, age, persistent);
    }

    @Override
    public @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder(new String(getVersion()) + "=\"" + getAuthority() + "\"");

        builder.append("; ma=").append(getAge().getSeconds());
        if (isPersistent()) builder.append("; persist=1");

        return builder.toString();
    }

}
