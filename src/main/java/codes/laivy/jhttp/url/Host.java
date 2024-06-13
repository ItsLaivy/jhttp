package codes.laivy.jhttp.url;

import codes.laivy.jhttp.url.domain.Subdomain;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.ipv4.IPv4Address;
import inet.ipaddr.ipv6.IPv6Address;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Host {

    // Static initializers

    static boolean validate(@NotNull String string) {
        return IPv4.validate(string) || IPv6.validate(string) || Name.validate(string);
    }
    static @NotNull Host parse(@NotNull String string) throws ParseException {
        if (IPv4.validate(string)) {
            return IPv4.parse(string);
        } else if (IPv6.validate(string)) {
            return IPv6.parse(string);
        } else if (Name.validate(string)) {
            return Name.parse(string);
        } else {
            throw new ParseException("the value '" + string + "' isn't a valid host", 0);
        }
    }

    // Object

    @NotNull
    String getName();

    @Range(from = 0, to = 65535)
    @Nullable
    Integer getPort();

    // Classes

    final class Name implements Host {

        // Static initializers

        public static final @NotNull Pattern NAME_PARSE_PATTERN = Pattern.compile("^(?:https?://)?(?:[^@/\\n]+@)?(?:(?:\\*|[a-zA-Z0-9-]+)\\.)*(?<host>(localhost|[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}))(?::(?<port>\\d{1,5}))?$");

        public static boolean validate(@NotNull String string) {
            return NAME_PARSE_PATTERN.matcher(string).matches();
        }
        public static @NotNull Name parse(@NotNull String string) throws ParseException {
            @NotNull Matcher matcher = NAME_PARSE_PATTERN.matcher(string);

            if (matcher.matches()) {
                @NotNull String name = matcher.group("host");
                @Nullable Integer port = matcher.group("port") != null ? Integer.parseInt(matcher.group("port")) : null;

                return new Name(name, port);
            } else {
                throw new ParseException("cannot parse '" + string + "' as a valid host name", 0);
            }
        }

        // Object

        private final @NotNull String name;

        @Range(from = 0, to = 65535)
        private final @Nullable Integer port;

        private Name(@NotNull String name, @Range(from = 0, to = 65535) @Nullable Integer port) {
            this.name = name;
            this.port = port;
        }

        public @NotNull Subdomain[] getSubdomains() {
            @NotNull String[] split = getName().split("\\.");
            @NotNull List<Subdomain> list = new LinkedList<>();

            if (split.length > 1) {
                for (int row = 0; row + 2 < split.length; row++) {
                    list.add(Subdomain.create(split[row]));
                }
            }

            return list.toArray(new Subdomain[0]);
        }

        @Override
        public @NotNull String getName() {
            return name;
        }
        @Override
        public @Range(from = 0, to = 65535) @Nullable Integer getPort() {
            return port;
        }

        // Implementations

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            Name name1 = (Name) object;
            return Objects.equals(name, name1.name) && Objects.equals(port, name1.port);
        }
        @Override
        public int hashCode() {
            return Objects.hash(name, port);
        }

        @Override
        public @NotNull String toString() {
            return getName() + (getPort() != null ? ":" + getPort() : "");
        }

    }
    final class IPv4 implements Host {

        private static final @NotNull Pattern IPV4_PATTERN = Pattern.compile("^\\[?(?<bytes>[^]]{2,39})]?(:(?<port>\\d{1,5}))?$");

        public static boolean validate(@NotNull String string) {
            @NotNull Matcher matcher = IPV4_PATTERN.matcher(string);
            return matcher.matches() && new IPAddressString(matcher.group("bytes")).isIPv4();
        }
        public static @NotNull IPv4 parse(@NotNull String string) throws ParseException {
            @NotNull Matcher matcher = IPV4_PATTERN.matcher(string);

            if (matcher.matches() && validate(string)) {
                @NotNull IPv4Address address = (IPv4Address) new IPAddressString(matcher.group("bytes")).getAddress();
                @Nullable Integer port = matcher.group("port") != null ? Integer.parseInt(matcher.group("port")) : null;

                return new IPv4(address, port);
            } else {
                throw new ParseException("cannot parse '" + string + "' into a valid ipv6", 0);
            }
        }

        // Object

        private final @NotNull IPv4Address address;

        @Range(from = 0, to = 65535)
        private final @Nullable Integer port;

        private IPv4(
                @NotNull IPv4Address address,

                @Range(from = 0, to = 65535)
                @Nullable Integer port
        ) {
            this.address = address;
            this.port = port;
        }

        // Getters

        @Override
        public @NotNull String getName() {
            return address.toString();
        }
        @Override
        public @Range(from = 0, to = 65535) @Nullable Integer getPort() {
            return port;
        }

        // Implementations

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            IPv4 iPv4 = (IPv4) object;
            return Objects.equals(address, iPv4.address) && Objects.equals(port, iPv4.port);
        }
        @Override
        public int hashCode() {
            return Objects.hash(address, port);
        }

        @Override
        public @NotNull String toString() {
            return getName() + (getPort() != null ? ":" + getPort() : "");
        }

    }
    final class IPv6 implements Host {

        // Static initializers

        private static final @NotNull Pattern IPV6_PATTERN = Pattern.compile("^\\[?(?<bytes>[^]]{2,39})]?(:(?<port>\\d{1,5}))?$");

        public static boolean validate(@NotNull String string) {
            @NotNull Matcher matcher = IPV6_PATTERN.matcher(string);
            return matcher.matches() && new IPAddressString(matcher.group("bytes")).isIPv6();
        }
        public static @NotNull IPv6 parse(@NotNull String string) throws ParseException {
            @NotNull Matcher matcher = IPV6_PATTERN.matcher(string);

            if (matcher.matches() && validate(string)) {
                @NotNull IPv6Address address = (IPv6Address) new IPAddressString(matcher.group("bytes")).getAddress();
                @Nullable Integer port = matcher.group("port") != null ? Integer.parseInt(matcher.group("port")) : null;

                return new IPv6(address, port);
            } else {
                throw new ParseException("cannot parse '" + string + "' into a valid ipv6", 0);
            }
        }

        // Object

        private final @NotNull IPv6Address address;

        @Range(from = 0, to = 65535)
        private final @Nullable Integer port;

        private IPv6(
                @NotNull IPv6Address address,

                @Range(from = 0, to = 65535)
                @Nullable Integer port
        ) {
            this.address = address;
            this.port = port;
        }

        @Override
        public @NotNull String getName() {
            if (getPort() != null) return "[" + address + "]";
            else return address.toString();
        }
        @Override
        @Range(from = 0, to = 65535)
        public @Nullable Integer getPort() {
            return port;
        }

        // Implementations

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            IPv6 iPv6 = (IPv6) object;
            return Objects.equals(address, iPv6.address) && Objects.equals(port, iPv6.port);
        }
        @Override
        public int hashCode() {
            return Objects.hash(address, port);
        }

        @Override
        public @NotNull String toString() {
            return getName() + (getPort() != null ? ":" + getPort() : "");
        }

    }

}
