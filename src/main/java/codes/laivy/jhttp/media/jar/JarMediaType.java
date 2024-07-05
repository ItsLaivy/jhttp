package codes.laivy.jhttp.media.jar;

import codes.laivy.jhttp.exception.media.MediaParserException;
import codes.laivy.jhttp.media.MediaParser;
import codes.laivy.jhttp.media.MediaType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class JarMediaType extends MediaType<JarFile> {

    // Static initializers

    public static final @NotNull Type TYPE = new Type("application", "java-archive");

    public static @NotNull MediaType<JarFile> getInstance() {
        //noinspection unchecked
        return (MediaType<JarFile>) MediaType.retrieve(TYPE).orElseThrow(() -> new NullPointerException("there's no media type '" + TYPE + "' registered on media type collections"));
    }

    // Object

    public JarMediaType() {
        super(TYPE, new Parser(), new Parameter[0]);
    }

    // Classes

    private static final class Parser implements MediaParser<JarFile> {

        @Override
        public @NotNull JarFile deserialize(@NotNull InputStream stream, @NotNull Parameter @NotNull ... parameters) throws MediaParserException, IOException {
            @NotNull File file = File.createTempFile("jhttp-", "-jar_media_type");
            file.deleteOnExit();

            try (@NotNull FileOutputStream write = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];

                int bytesRead;
                while ((bytesRead = stream.read(buffer)) != -1) {
                    write.write(buffer, 0, bytesRead);
                }

                write.flush();
            }

            return new JarFile(file, true);
        }
        @Override
        public @NotNull InputStream serialize(@NotNull JarFile jar, @NotNull Parameter @NotNull ... parameters) throws IOException {
            @NotNull File file = File.createTempFile("jhttp-", "-jar_media_type");
            file.deleteOnExit();

            try (@NotNull JarOutputStream write = new JarOutputStream(Files.newOutputStream(file.toPath()))) {
                @NotNull Enumeration<JarEntry> enumeration = jar.entries();
                while (enumeration.hasMoreElements()) {
                    @NotNull JarEntry entry = enumeration.nextElement();
                    write.putNextEntry(entry);

                    @NotNull InputStream inputStream = jar.getInputStream(entry);
                    byte[] buffer = new byte[1024];

                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        write.write(buffer, 0, bytesRead);
                    }

                    write.closeEntry();
                    inputStream.close();
                }
            }

            return Files.newInputStream(file.toPath());
        }

    }

}
