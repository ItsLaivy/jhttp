package codes.laivy.jhttp.url;

import codes.laivy.jhttp.module.content.ContentSecurityPolicy;
import codes.laivy.jhttp.media.MediaType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Data implements ContentSecurityPolicy.Source {

    // Static initializers

    public static final @NotNull Pattern DATA_URL_PATTERN = Pattern.compile("^data:(?:(\\S*?)(;base64)?)?,(\\S*)$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(@NotNull String string) {
        return DATA_URL_PATTERN.matcher(string).matches();
    }
    public static @NotNull Data parse(@NotNull String string) throws ParseException, UnsupportedEncodingException {
        @NotNull Matcher matcher = DATA_URL_PATTERN.matcher(string);

        if (matcher.matches()) {
            @Nullable MediaType<?> type = null;
            @NotNull String encoding = "UTF-8";

            if (!matcher.group(1).isEmpty()) {
                type = matcher.group(1) != null ? MediaType.Parser.deserialize(matcher.group(1)) : null;
                encoding = type != null && type.getCharset() != null ? type.getCharset().raw() : "UTF-8";
            }

            boolean base64 = matcher.group(2) != null;

            byte[] data = URLDecoder.decode(matcher.group(3), encoding).getBytes();
            byte[] decoded = base64 ? URLDecoder.decode(new String(Base64.getDecoder().decode(data)), encoding).getBytes() : null;

            return new Data(type, base64, decoded, data);
        } else {
            throw new ParseException("cannot parse '" + string + "' as a valid CSP data url", 0);
        }
    }

    public static @NotNull Data create(@Nullable MediaType<?> type, byte @NotNull [] raw) {
        return new Data(type, false, null, raw);
    }
    public static @NotNull Data createBase64(@Nullable MediaType<?> type, byte @NotNull [] raw, byte @NotNull [] decoded) {
        return new Data(type, true, decoded, raw);
    }

    // Object

    private final @Nullable MediaType<?> mediaType;
    private final boolean base64;

    private final byte @Nullable [] decoded;
    private final byte @NotNull [] raw;

    private Data(@Nullable MediaType<?> mediaType, boolean base64, byte @Nullable [] decoded, byte @NotNull [] raw) {
        this.mediaType = mediaType;
        this.base64 = base64;
        this.decoded = decoded;
        this.raw = raw;
    }

    // Getters

    public @NotNull Scheme getType() {
        return Scheme.DATA;
    }

    public @Nullable MediaType<?> getMediaType() {
        return mediaType;
    }

    public boolean isBase64() {
        return base64;
    }

    /**
     * Retrieves the decoded data. If the data is base64 encoded, it will return the decoded bytes.
     * Otherwise, it returns the raw bytes.
     *
     * @return the decoded data as a byte array.
     * @author Daniel Richard (Laivy)
     * @since 1.0-SNAPSHOT
     */
    public byte @NotNull [] getData() {
        return isBase64() && decoded != null ? decoded : getRawData();
    }

    /**
     * Retrieves the raw data part of the data URL without decoding.
     * If the data is base64 encoded, this method will still return the encoded data as raw bytes.
     *
     * @return the raw data as a byte array.
     * @author Daniel Richard (Laivy)
     * @since 1.0-SNAPSHOT
     */
    public byte @NotNull [] getRawData() {
        return raw;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Data data = (Data) object;
        return base64 == data.base64 && Objects.equals(mediaType, data.mediaType) && Objects.deepEquals(decoded, data.decoded);
    }
    @Override
    public int hashCode() {
        return Objects.hash(mediaType, base64, Arrays.hashCode(decoded));
    }

    @Override
    public @NotNull String toString() {
        @NotNull StringBuilder builder = new StringBuilder("data:");

        if (getMediaType() != null) {
            builder.append(getMediaType().toString().replaceAll("\\s*;\\s*", ";"));
        }
        if (isBase64()) {
            builder.append(";base64");
        }

        try {
            @NotNull String encoding = getMediaType() != null && getMediaType().getCharset() != null ? getMediaType().getCharset().raw() : "UTF-8";
            builder.append(",").append(URLEncoder.encode(new String(getRawData()), encoding));
        } catch (@NotNull UnsupportedEncodingException e) {
            throw new RuntimeException("cannot encode url '" + new String(getRawData()) + "'", e);
        }

        return builder.toString();
    }
}
