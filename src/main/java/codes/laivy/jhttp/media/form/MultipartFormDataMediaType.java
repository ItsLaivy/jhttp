package codes.laivy.jhttp.media.form;

import codes.laivy.jhttp.body.HttpBody;
import codes.laivy.jhttp.element.FormData;
import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.exception.parser.HeaderFormatException;
import codes.laivy.jhttp.exception.parser.element.HttpBodyParseException;
import codes.laivy.jhttp.headers.HttpHeader;
import codes.laivy.jhttp.headers.HttpHeaderKey;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.media.MediaParser;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.module.content.ContentDisposition;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static codes.laivy.jhttp.Main.CRLF;

public final class MultipartFormDataMediaType extends MediaType<@NotNull FormData @NotNull []> {

    // Static initializers

    public static final @NotNull MediaType.Type TYPE = new MediaType.Type("multipart", "form-data");

    public static @NotNull MediaType<@NotNull FormData @NotNull []> getInstance() {
        //noinspection unchecked
        @Nullable MediaType<FormData[]> media = (MediaType<FormData[]>) MediaType.retrieve(TYPE).orElse(null);
        if (media == null) media = new MultipartFormDataMediaType();

        return media;
    }

    // Object

    public MultipartFormDataMediaType() {
        super(TYPE, new Parser(), new MediaType.Parameter[0]);
    }

    // Classes

    private static final class Parser implements MediaParser<@NotNull FormData @NotNull []> {

        @Override
        public @NotNull FormData @NotNull [] deserialize(@NotNull HttpVersion version, @NotNull InputStream stream, @NotNull Parameter @NotNull ... parameters) throws MediaParserException, IOException {
            @NotNull Boundary boundary = new Boundary(Arrays.stream(parameters).filter(parameter -> parameter.getKey().equalsIgnoreCase("boundary")).findFirst().orElseThrow(() -> new NullPointerException("you must specify the boundary!")).getValue());
            @NotNull Set<FormData> forms = new HashSet<>();

            // Temporary
            @NotNull HttpHeader<?>[] headers = new HttpHeader[0];
            @NotNull StringBuilder sequence = new StringBuilder();

            // Read
            int read;
            while ((read = stream.read()) != -1) {
                byte b = (byte) read;
                sequence.append(b);

                if (b == '\n' && sequence.toString().endsWith("--" + boundary.getName() + "\r\n")) { // End of the data
                    @NotNull String[] parts = sequence.toString().split("\r\n\r\n", 2);

                    // Headers
                    for (@NotNull String string : parts[0].split("\r\n")) {
                        try {
                            // Parse header
                            @NotNull HttpHeader<?> header = HttpVersion.HTTP1_1().getHeaderFactory().parse(string);

                            // Add to array
                            headers = Arrays.copyOfRange(headers, 0, headers.length + 1);
                            headers[headers.length - 1] = header;
                        } catch (@NotNull HeaderFormatException e) {
                            throw new MediaParserException("cannot parse header '" + string + "'", e);
                        }
                    }

                    // Body
                    @NotNull HttpBody body;

                    try {
                        body = version.getBodyFactory().parse(HttpHeaders.create(version, headers), parts[1]);
                    } catch (@NotNull HttpBodyParseException e) {
                        throw new MediaParserException("cannot parse http body from multipart form-data", e);
                    }

                    // Finish formulary data
                    @Nullable ContentDisposition disposition = (ContentDisposition) Arrays.stream(headers).filter(header -> header.getKey().equals(HttpHeaderKey.CONTENT_DISPOSITION)).findFirst().map(HttpHeader::getValue).orElse(null);

                    if (disposition == null) {
                        throw new MediaParserException("cannot parse form-data from multipart: missing content-disposition header");
                    } else if (disposition.getType() != ContentDisposition.Type.FORM_DATA || disposition.getName() == null) {
                        throw new MediaParserException("invalid form-data content disposition: '" + disposition + "'");
                    }

                    // Finish
                    @NotNull FormData data = FormData.create(disposition.getName(), body);
                    forms.add(data);

                    // Reset sequence
                    headers = new HttpHeader[0];
                    sequence = new StringBuilder();
                }
            }

            return forms.toArray(new FormData[0]);
        }
        @Override
        public @NotNull InputStream serialize(@NotNull HttpVersion version, @NotNull FormData @NotNull [] content, @NotNull Parameter @NotNull ... parameters) throws IOException, MediaParserException {
            @NotNull Boundary boundary = new Boundary(Arrays.stream(parameters).filter(parameter -> parameter.getKey().equalsIgnoreCase("boundary")).findFirst().orElseThrow(() -> new NullPointerException("you must specify the boundary!")).getValue());
            @NotNull ByteArrayOutputStream stream = new ByteArrayOutputStream();

            for (@NotNull FormData data : content) {
                // Checks
                @Nullable ContentDisposition disposition = (ContentDisposition) Arrays.stream(data.getHeaders()).filter(header -> header.getKey().equals(HttpHeaderKey.CONTENT_DISPOSITION)).findFirst().map(HttpHeader::getValue).orElse(null);

                if (disposition == null) {
                    throw new MediaParserException("cannot parse form-data from multipart: missing content-disposition header");
                } else if (disposition.getType() != ContentDisposition.Type.FORM_DATA || disposition.getName() == null) {
                    throw new MediaParserException("invalid form-data content disposition: '" + disposition + "'");
                }

                // Serialize
                stream.write(("--" + boundary.getName() + CRLF).getBytes());

                for (@NotNull HttpHeader<?> header : data.getHeaders()) {
                    stream.write(version.getHeaderFactory().serialize(header).getBytes());
                    stream.write(CRLF.getBytes());
                }
            }

            // Write boundary ending
            stream.write(("--" + boundary.getName() + "--").getBytes());

            // Finish
            return new ByteArrayInputStream(stream.toByteArray());
        }

    }

}
