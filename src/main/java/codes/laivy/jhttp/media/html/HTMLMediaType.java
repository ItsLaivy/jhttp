package codes.laivy.jhttp.media.html;

import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.media.MediaParser;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;

@ApiStatus.Experimental
public class HTMLMediaType extends MediaType<@NotNull Element> {

    // Static initializers

    public static final @NotNull Type TYPE = new Type("text", "html");

    public static @NotNull MediaType<@NotNull Element> getInstance() {
        //noinspection unchecked
        @Nullable MediaType<Element> media = (MediaType<Element>) MediaType.retrieve(TYPE).orElse(null);
        if (media == null) media = new HTMLMediaType();

        return media;
    }

    // Object

    public HTMLMediaType(@NotNull Parameter @NotNull ... parameters) {
        super(TYPE, new Parser(), parameters);
    }

    // Modules

    @Override
    public @NotNull MediaType<Element> clone(@NotNull Parameter @NotNull ... parameters) {
        return new HTMLMediaType(parameters);
    }

    // Classes

    private static final class Parser implements MediaParser<@NotNull Element> {
        @Override
        public @NotNull Element deserialize(@NotNull HttpVersion version, @NotNull InputStream stream, @NotNull Parameter @NotNull ... parameters) throws MediaParserException, IOException {
            // Retrieve charset
            @Nullable Parameter parameter = Arrays.stream(parameters).filter(p -> p.getKey().equalsIgnoreCase("charset")).findFirst().orElse(null);
            @Nullable Charset charset = parameter != null ? Deferred.charset(parameter.getValue()).orElse(null) : null;

            // todo: retrieve charset from html file

            @NotNull StringBuilder builder = new StringBuilder();
            try (@NotNull InputStreamReader reader = (charset != null ? new InputStreamReader(stream, charset) : new InputStreamReader(stream))) {
                while (reader.ready()) builder.append((char) reader.read());
            }

            return Jsoup.parse(builder.toString());
        }
        @Override
        public @NotNull InputStream serialize(@NotNull HttpVersion version, @NotNull Element content, @NotNull Parameter @NotNull ... parameters) throws IOException, MediaParserException {
            // Retrieve charset
            @Nullable Parameter parameter = Arrays.stream(parameters).filter(p -> p.getKey().equalsIgnoreCase("charset")).findFirst().orElse(null);
            @Nullable Charset charset = parameter != null ? Deferred.charset(parameter.getValue()).orElse(null) : null;

            // todo: retrieve charset from html file

            // Finish
            return new ByteArrayInputStream(charset != null ? content.toString().getBytes(charset) : null);
        }
    }

}
