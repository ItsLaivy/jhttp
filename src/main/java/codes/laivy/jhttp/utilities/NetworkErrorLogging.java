package codes.laivy.jhttp.utilities;

import codes.laivy.jhttp.headers.HeaderKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

// todo: 13/06/2024 - https://w3c.github.io/network-error-logging/#nel-response-header
public interface NetworkErrorLogging {

    // Static initializers

    static @NotNull NetworkErrorLogging create(
            @NotNull String group,
            @NotNull Duration age,

            @NotNull HeaderKey<?>[] requests,
            @NotNull HeaderKey<?>[] responses,

            boolean hasSubdomains,
            boolean successFraction,
            boolean failureFraction
    ) {
        return new NetworkErrorLogging() {
            @Override
            public @NotNull String getGroup() {
                return group;
            }
            @Override
            public @NotNull Duration getAge() {
                return age;
            }
            @Override
            public @NotNull HeaderKey<?>[] getRequests() {
                return requests;
            }
            @Override
            public @NotNull HeaderKey<?>[] getResponses() {
                return responses;
            }
            @Override
            public boolean hasSubdomains() {
                return hasSubdomains;
            }
            @Override
            public boolean successFraction() {
                return successFraction;
            }
            @Override
            public boolean failureFraction() {
                return failureFraction;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (!(object instanceof NetworkErrorLogging)) return false;
                NetworkErrorLogging that = (NetworkErrorLogging) object;
                return Objects.equals(getGroup(), that.getGroup()) && Objects.equals(getAge(), that.getAge()) && Arrays.equals(getRequests(), that.getRequests()) && Arrays.equals(getResponses(), that.getResponses()) && hasSubdomains() == that.hasSubdomains() && successFraction() == that.successFraction() && failureFraction() == that.failureFraction();
            }
            @Override
            public int hashCode() {
                return Objects.hash(getGroup(), getAge(), Arrays.hashCode(getRequests()), Arrays.hashCode(getResponses()), hasSubdomains(), successFraction(), failureFraction());
            }

        };
    }

    // Object

    @NotNull String getGroup();
    @NotNull Duration getAge();

    @NotNull HeaderKey<?>[] getRequests();
    @NotNull HeaderKey<?>[] getResponses();

    boolean hasSubdomains();
    boolean successFraction();
    boolean failureFraction();

    // Classes

    final class Parser {

        private Parser() {
            throw new UnsupportedOperationException();
        }

        public static boolean validate(@NotNull String string) {

        }
        public static @NotNull NetworkErrorLogging parse(@NotNull String string) throws ParseException {

        }

        public static @NotNull String serialize(@NotNull NetworkErrorLogging string) {

        }

    }

}
