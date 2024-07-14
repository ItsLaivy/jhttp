package codes.laivy.jhttp.media.form;

import codes.laivy.jhttp.body.HttpBody;
import codes.laivy.jhttp.element.FormData;
import codes.laivy.jhttp.exception.encoding.EncodingException;
import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.media.MediaParser;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public final class FormUrlEncodedMediaType extends MediaType<@NotNull FormData @NotNull []> {

    // Static initializers

    public static final @NotNull MediaType.Type TYPE = new MediaType.Type("application", "x-www-form-urlencoded");

    public static @NotNull MediaType<@NotNull FormData @NotNull []> getInstance() {
        //noinspection unchecked
        @Nullable MediaType<FormData[]> media = (MediaType<FormData[]>) MediaType.retrieve(TYPE).orElse(null);
        if (media == null) media = new FormUrlEncodedMediaType();

        return media;
    }

    // Object

    public FormUrlEncodedMediaType() {
        super(TYPE, new Parser(), new MediaType.Parameter[0]);
    }

    // Classes

    private static final class Parser implements MediaParser<@NotNull FormData @NotNull []> {

        @Override
        public @NotNull FormData @NotNull [] deserialize(@NotNull HttpVersion version, @NotNull InputStream stream, @NotNull Parameter @NotNull ... parameters) throws MediaParserException, IOException {
            @NotNull Set<FormData> forms = new HashSet<>();
            @NotNull StringBuilder sequence = new StringBuilder();

            int read;
            while ((read = stream.read()) != -1) {
                byte b = (byte) read;

                if (b == '&') { // End of the sequence, starting other
                    @NotNull String[] parts = sequence.toString().split("=", 2);
                    @NotNull String key = URLDecoder.decode(parts[0], "UTF-8");
                    byte[] value = parts.length == 2 ? URLDecoder.decode(parts[1], "UTF-8").getBytes(StandardCharsets.UTF_8) : new byte[0];

                    forms.add(FormData.create(key, HttpBody.create(version, value)));
                    sequence = new StringBuilder();
                } else {
                    sequence.append(b);
                }
            }

            return forms.toArray(new FormData[0]);
        }
        @Override
        public @NotNull InputStream serialize(@NotNull HttpVersion version, @NotNull FormData @NotNull [] content, @NotNull Parameter @NotNull ... parameters) throws IOException {
            @NotNull StringBuilder builder = new StringBuilder();

            for (@NotNull FormData data : content) {
                if (builder.length() > 0) builder.append("&");
                builder.append(URLEncoder.encode(data.getName(), "UTF-8").replace("%20", "+"));

                if (data.getBody() != null) try {
                    builder.append("=");
                    builder.append(URLEncoder.encode(version.getBodyFactory().serialize(HttpHeaders.empty(), data.getBody()), "UTF-8").replace("%20", "+"));
                } catch (@NotNull EncodingException e) {
                    throw new RuntimeException("cannot serialize http body from form data", e);
                }
            }

            return new ByteArrayInputStream(builder.toString().getBytes());
        }

    }

}