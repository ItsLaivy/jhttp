package codes.laivy.jhttp.content;

import codes.laivy.jhttp.url.Host;
import codes.laivy.jhttp.utilities.HttpProtocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Forwarded {

    // Static initializers

    static @NotNull Forwarded create(@Nullable Target by, @Nullable Target for_, @Nullable Host host, @Nullable HttpProtocol protocol) {
        return new Forwarded() {

            // Object

            @Override
            public @Nullable Target getBy() {
                return by;
            }
            @Override
            public @Nullable Target getFor() {
                return for_;
            }
            @Override
            public @Nullable Host getHost() {
                return host;
            }
            @Override
            public @Nullable HttpProtocol getProtocol() {
                return protocol;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                @NotNull Forwarded forwarded = (Forwarded) object;
                return Objects.equals(getBy(), forwarded.getBy()) && Objects.equals(getFor(), forwarded.getFor()) && Objects.equals(getHost(), forwarded.getHost()) && getProtocol() == forwarded.getProtocol();
            }
            @Override
            public int hashCode() {
                return Objects.hash(by, for_, host, protocol);
            }
            @Override
            public @NotNull String toString() {
                return Parser.serialize(this);
            }

        };
    }

    // Getters

    @Nullable Target getBy();
    @Nullable Target getFor();
    @Nullable Host getHost();
    @Nullable HttpProtocol getProtocol();

    // Classes

    interface Target {

        // Static initializers

        static @NotNull Target obfuscated(@NotNull String string) {
            if (!string.startsWith("_")) {
                throw new IllegalArgumentException("obfuscated forwarded targets must start with '_'");
            }

            return new Target() {
                @Override
                public boolean equals(@Nullable Object object) {
                    if (this == object) return true;
                    if (!(object instanceof Forwarded)) return false;
                    @NotNull Forwarded that = (Forwarded) object;
                    return Objects.equals(that.toString(), string);
                }
                @Override
                public int hashCode() {
                    return Objects.hash(string);
                }
                @Override
                public @NotNull String toString() {
                    return string;
                }
            };
        }
        static @NotNull Target host(@NotNull Host host) {
            return new Target() {
                @Override
                public boolean equals(@Nullable Object object) {
                    if (this == object) return true;
                    if (!(object instanceof Forwarded)) return false;
                    @NotNull Forwarded that = (Forwarded) object;
                    return Objects.equals(that.toString(), toString());
                }
                @Override
                public int hashCode() {
                    return Objects.hash(host);
                }
                @Override
                public @NotNull String toString() {
                    return host.toString();
                }
            };
        }
        static @NotNull Target unknown() {
            final @NotNull String unknown = "unknown";

            return new Target() {
                @Override
                public boolean equals(@Nullable Object object) {
                    if (this == object) return true;
                    if (!(object instanceof Forwarded)) return false;
                    @NotNull Forwarded that = (Forwarded) object;
                    return Objects.equals(that.toString(), toString());
                }
                @Override
                public int hashCode() {
                    return Objects.hash(unknown);
                }
                @Override
                public @NotNull String toString() {
                    return unknown;
                }
            };
        }

        // Object

        default boolean isObfuscated() {
            return toString().startsWith("_");
        }

        @NotNull String toString();

    }

    final class Parser {
        private Parser() {
            throw new UnsupportedOperationException();
        }

        // Serializers

        private static final @NotNull Pattern TARGET_PARSE_PATTERN = Pattern.compile("^_(unknown|^(^(localhost|(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}|(?:\\d{1,3}\\.){3}\\d{1,3}|\\[(?:[a-fA-F0-9]{0,4}:){2,7}(?::[a-fA-F0-9]{0,4}){1,7})])(?::\\d{1,5})?$)$");
        private static final @NotNull Pattern FORWARDED_PATTERN = Pattern.compile(
                "(?i)(?:for\\s*=\\s*(?<for>" + TARGET_PARSE_PATTERN.pattern() + "|[^\\s*;\\s*]+)\\s*;?\\s*|" +
                        "proto\\s*=\\s*(?<protocol>https?)\\s*;?\\s*|" +
                        "by\\s*=\\s*(?<by>" + TARGET_PARSE_PATTERN.pattern() + "|[^\\s*;\\s*]+)\\s*;?\\s*|" +
                        "host\\s*=\\s*(?<host>[^\\s*;\\s*]+)\\s*;?\\s*)*\\s*"
        );

        public static @NotNull String serialize(@NotNull Forwarded forwarded) {
            @NotNull StringBuilder builder = new StringBuilder();

            if (forwarded.getBy() != null) {
                builder.append("by=").append(forwarded.getBy());
            } if (forwarded.getFor() != null) {
                builder.append("for=").append(forwarded.getFor());
            } if (forwarded.getHost() != null) {
                builder.append("host=").append(forwarded.getHost());
            } if (forwarded.getProtocol() != null) {
                builder.append("proto=").append(forwarded.getProtocol().name().toLowerCase());
            }

            return builder.toString();
        }
        public static @NotNull Forwarded deserialize(@NotNull String string) throws ParseException {
            @NotNull Matcher matcher = FORWARDED_PATTERN.matcher(string);

            if (matcher.matches()) {
                @Nullable Target by = null;
                @Nullable Target for_ = null;
                @Nullable Host host = null;
                @Nullable HttpProtocol protocol = null;

                // By
                {
                    @Nullable String value = matcher.group("by");

                    if (value != null) {
                        if (value.equals("unknown")) {
                            by = Target.unknown();
                        } else if (value.startsWith("_")) {
                            by = Target.obfuscated(value);
                        } else if (Host.validate(value)) {
                            by = Target.host(Host.parse(value));
                        } else {
                            throw new ParseException("the value '" + value + "' cannot be parsed as a valid 'by' forwarded target", matcher.start("by"));
                        }
                    }
                }

                // For
                {
                    @Nullable String value = matcher.group("for");

                    if (value != null) {
                        if (value.equals("unknown")) {
                            for_ = Target.unknown();
                        } else if (value.startsWith("_")) {
                            for_ = Target.obfuscated(value);
                        } else if (Host.validate(value)) {
                            for_ = Target.host(Host.parse(value));
                        } else {
                            throw new ParseException("the value '" + value + "' cannot be parsed as a valid 'for' forwarded target", matcher.start("for"));
                        }
                    }
                }

                // Host
                {
                    @Nullable String value = matcher.group("host");

                    if (value != null) {
                        if (Host.validate(value)) {
                            host = Host.parse(value);
                        } else {
                            throw new ParseException("cannot parse '" + value + "' into a valid host to forwarded object", matcher.start(0));
                        }
                    }
                }

                // Protocol
                {
                    @Nullable String value = matcher.group("protocol");

                    if (value != null) {
                        if (value.equalsIgnoreCase("http")) {
                            protocol = HttpProtocol.HTTP;
                        } else if (value.equalsIgnoreCase("https")) {
                            protocol = HttpProtocol.HTTPS;
                        } else {
                            throw new ParseException("unknown http protocol type '" + value + "'", matcher.start("protocol"));
                        }
                    }
                }

                // Finish
                return create(by, for_, host, protocol);
            } else {
                throw new ParseException("cannot parse '" + string + "' as a valid forwarded object", 0);
            }
        }

        public static boolean validate(@NotNull String string) {
            return FORWARDED_PATTERN.matcher(string).matches();
        }

    }

}
