package codes.laivy.jhttp.element;

import codes.laivy.jhttp.headers.HttpHeaderKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public final class HttpStatus {

    // Static initializers

    public static @NotNull HttpStatus getByCode(int code) throws NullPointerException {
        try {
            for (@NotNull Field field : HttpStatus.class.getDeclaredFields()) {
                if (field.getType() == HttpStatus.class) {
                    field.setAccessible(true);

                    @NotNull HttpStatus status = (HttpStatus) field.get(null);

                    if (status.getCode() == code) {
                        return status;
                    }
                }
            }
        } catch (@NotNull IllegalAccessException ignore) {
        }

        throw new NullPointerException("cannot find a http status with code '" + code + "'");
    }

    // Static providers

    public static @NotNull HttpStatus CONTINUE = new HttpStatus(100, "Continue");
    public static @NotNull HttpStatus SWITCHING_PROTOCOLS = new HttpStatus(101, "Switching Protocols");
    @Deprecated
    public static @NotNull HttpStatus PROCESSING = new HttpStatus(102, "Processing");
    public static @NotNull HttpStatus EARLY_HINTS = new HttpStatus(103, "Early Hints");

    public static @NotNull HttpStatus OK = new HttpStatus(200, "OK");
    public static @NotNull HttpStatus CREATED = new HttpStatus(201, "Created");
    public static @NotNull HttpStatus ACCEPTED = new HttpStatus(202, "Accepted");
    public static @NotNull HttpStatus NON_AUTHORITATIVE_INFORMATION = new HttpStatus(203, "Non-Authoritative Information");
    public static @NotNull HttpStatus NO_CONTENT = new HttpStatus(204, "No Content");
    public static @NotNull HttpStatus RESET_CONTENT = new HttpStatus(205, "Reset Content");
    public static @NotNull HttpStatus PARTIAL_CONTENT = new HttpStatus(206, "Partial Content");
    public static @NotNull HttpStatus MULTI_STATUS = new HttpStatus(207, "Multi-Status");
    public static @NotNull HttpStatus ALREADY_REPORTED = new HttpStatus(208, "Already Reported");
    public static @NotNull HttpStatus IM_USED = new HttpStatus(226, "IM Used");

    public static @NotNull HttpStatus MULTIPLE_CHOICES = new HttpStatus(300, "Multiple Choices");
    public static @NotNull HttpStatus MOVED_PERMANENTLY = new HttpStatus(301, "Moved Permanently", HttpHeaderKey.LOCATION);
    public static @NotNull HttpStatus FOUND = new HttpStatus(302, "Found", HttpHeaderKey.LOCATION);
    public static @NotNull HttpStatus SEE_OTHER = new HttpStatus(303, "See Other");
    public static @NotNull HttpStatus NOT_MODIFIED = new HttpStatus(304, "Not Modified");
    public static @NotNull HttpStatus TEMPORARY_REDIRECT = new HttpStatus(307, "Temporary Redirect", HttpHeaderKey.LOCATION);
    public static @NotNull HttpStatus PERMANENT_REDIRECT = new HttpStatus(308, "Permanent Redirect", HttpHeaderKey.LOCATION);

    public static @NotNull HttpStatus BAD_REQUEST = new HttpStatus(400, "Bad Request");
    public static @NotNull HttpStatus UNAUTHORIZED = new HttpStatus(401, "Unauthorized");
    public static @NotNull HttpStatus PAYMENT_REQUIRED = new HttpStatus(402, "Payment Required");
    public static @NotNull HttpStatus FORBIDDEN = new HttpStatus(403, "Forbidden");
    public static @NotNull HttpStatus NOT_FOUND = new HttpStatus(404, "Not Found");
    public static @NotNull HttpStatus METHOD_NOT_ALLOWED = new HttpStatus(405, "Method Not Allowed", HttpHeaderKey.ALLOW);
    public static @NotNull HttpStatus NOT_ACCEPTABLE = new HttpStatus(406, "Not Acceptable", HttpHeaderKey.ALLOW);
    public static @NotNull HttpStatus PROXY_AUTHENTICATION_REQUIRED = new HttpStatus(407, "Proxy Authentication Required");
    public static @NotNull HttpStatus REQUEST_TIMEOUT = new HttpStatus(408, "Request Timeout", HttpHeaderKey.CONNECTION);
    public static @NotNull HttpStatus CONFLICT = new HttpStatus(409, "Conflict");
    public static @NotNull HttpStatus GONE = new HttpStatus(410, "Gone");
    public static @NotNull HttpStatus LENGTH_REQUIRED = new HttpStatus(411, "Length Required");
    public static @NotNull HttpStatus PRECONDITION_FAILED = new HttpStatus(412, "Precondition Failed");
    public static @NotNull HttpStatus CONTENT_TOO_LARGE = new HttpStatus(413, "Content Too Large");
    public static @NotNull HttpStatus URI_TOO_LONG = new HttpStatus(414, "URI Too Long");
    public static @NotNull HttpStatus UNSUPPORTED_MEDIA_TYPE = new HttpStatus(415, "Unsupported Media Type");
    public static @NotNull HttpStatus RANGE_NOT_SATISFIABLE = new HttpStatus(416, "Range Not Satisfiable", HttpHeaderKey.CONTENT_RANGE);
    public static @NotNull HttpStatus EXPECTATION_FAILED = new HttpStatus(417, "Expectation Failed");
    public static @NotNull HttpStatus IM_A_TEAPOT = new HttpStatus(418, "I'm a teapot");
    public static @NotNull HttpStatus MISDIRECTED_REQUEST = new HttpStatus(421, "Misdirected Request");
    public static @NotNull HttpStatus UNPROCESSABLE_CONTENT = new HttpStatus(422, "Unprocessable Content");
    public static @NotNull HttpStatus LOCKED = new HttpStatus(423, "Locked");
    public static @NotNull HttpStatus FAILED_DEPENDENCY = new HttpStatus(424, "Failed Dependency");
    public static @NotNull HttpStatus TOO_EARLY = new HttpStatus(425, "Too Early");
    public static @NotNull HttpStatus UPGRADE_REQUIRED = new HttpStatus(426, "Upgrade Required");
    public static @NotNull HttpStatus PRECONDITION_REQUIRED = new HttpStatus(428, "Precondition Required");
    public static @NotNull HttpStatus TOO_MANY_REQUESTS = new HttpStatus(429, "Too Many Requests");
    public static @NotNull HttpStatus REQUEST_HEADER_FIELDS_TOO_LARGE = new HttpStatus(431, "Request Header Fields Too Large");
    public static @NotNull HttpStatus UNAVAILABLE_FOR_LEGAL_REASONS = new HttpStatus(451, "Unavailable For Legal Reasons");

    public static @NotNull HttpStatus INTERNAL_SERVER_ERROR = new HttpStatus(500, "Internal Server Error");
    public static @NotNull HttpStatus NOT_IMPLEMENTED = new HttpStatus(501, "Not Implemented");
    public static @NotNull HttpStatus BAD_GATEWAY = new HttpStatus(502, "Bad Gateway");
    public static @NotNull HttpStatus SERVICE_UNAVAILABLE = new HttpStatus(503, "Service Unavailable");
    public static @NotNull HttpStatus GATEWAY_TIMEOUT = new HttpStatus(504, "Gateway Timeout");
    public static @NotNull HttpStatus HTTP_VERSION_NOT_SUPPORTED = new HttpStatus(505, "HTTP Version Not Supported");
    public static @NotNull HttpStatus VARIANT_ALSO_NEGOTIATES = new HttpStatus(506, "Variant Also Negotiates");
    public static @NotNull HttpStatus INSUFFICIENT_STORAGE = new HttpStatus(507, "Insufficient Storage");
    public static @NotNull HttpStatus LOOP_DETECTED = new HttpStatus(508, "Loop Detected");
    public static @NotNull HttpStatus NOT_EXTENDED = new HttpStatus(510, "Not Extended");
    public static @NotNull HttpStatus NETWORK_AUTHENTICATION_REQUIRED = new HttpStatus(511, "Network Authentication Required");

    // Object

    private final int code;
    private final @NotNull String message;

    private final @NotNull HttpHeaderKey<?> @NotNull [] headers;

    public HttpStatus(int code, @NotNull String message) {
        this(code, message, new HttpHeaderKey[0]);
    }
    public HttpStatus(int code, @NotNull String message, @NotNull HttpHeaderKey<?> @NotNull ... headers) {
        this.code = code;
        this.message = message;
        this.headers = Arrays.copyOf(headers, headers.length);

        if (message.length() > 8192) {
            throw new IllegalStateException("http status message too large");
        }
    }

    // Getters

    @Contract(pure = true)
    public int getCode() {
        return code;
    }
    @Contract(pure = true)
    public @NotNull String getMessage() {
        return message;
    }

    /**
     * Retrieves the list of headers required for this HTTP status.
     * If a response has this status and does not contain the headers in this array,
     * a {@link NullPointerException} will be thrown during response serialization.
     *
     * @return The list of headers required for this HTTP status
     */
    @Contract(pure = true)
    public @NotNull HttpHeaderKey<?> @NotNull [] getHeaders() {
        return headers;
    }

    @Contract(pure = true)
    public @NotNull Category getCategory() {
        return Category.getCategory(this);
    }

    // Utilities

    public boolean isInformational() {
        return getCategory() == Category.INFORMATIONAL;
    }
    public boolean isSuccessful() {
        return getCategory() == Category.SUCCESSFUL;
    }
    public boolean isRedirection() {
        return getCategory() == Category.REDIRECTION;
    }

    public boolean isClientError() {
        return getCategory() == Category.CLIENT_ERROR;
    }
    public boolean isServerError() {
        return getCategory() == Category.SERVER_ERROR;
    }
    public boolean isError() {
        return getCategory().isError();
    }

    // Implementations

    @Override
    public @NotNull String toString() {
        return String.valueOf(getCode());
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof HttpStatus)) return false;
        @NotNull HttpStatus that = (HttpStatus) object;
        return getCode() == that.getCode();
    }
    @Override
    public int hashCode() {
        return Objects.hash(getCode());
    }

    // Classes

    public enum Category {
        INFORMATIONAL(100, 199),
        SUCCESSFUL(200, 299),
        REDIRECTION(300, 399),
        CLIENT_ERROR(400, 499),
        SERVER_ERROR(500, 599),
        ;

        private final int minimum;
        private final int maximum;

        Category(int minimum, int maximum) {
            this.minimum = minimum;
            this.maximum = maximum;
        }

        public int getMinimum() {
            return minimum;
        }
        public int getMaximum() {
            return maximum;
        }

        public boolean isError() {
            return this == CLIENT_ERROR || this == SERVER_ERROR;
        }

        public static @NotNull Category getCategory(@NotNull HttpStatus status) throws IndexOutOfBoundsException {
            for (@NotNull Category category : values()) {
                if (status.getCode() >= category.getMinimum() && status.getCode() <= category.getMaximum()) {
                    return category;
                }
            }
            throw new IndexOutOfBoundsException("couldn't find a category for status code '" + status.getCode() + "'");
        }

    }

}
