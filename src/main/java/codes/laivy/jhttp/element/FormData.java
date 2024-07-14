package codes.laivy.jhttp.element;

import codes.laivy.jhttp.body.HttpBody;
import codes.laivy.jhttp.headers.HttpHeader;
import codes.laivy.jhttp.headers.HttpHeaderKey;
import codes.laivy.jhttp.module.content.ContentDisposition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Arrays;
import java.util.Objects;

public interface FormData {

    // Static initializers

    static @NotNull FormData create(@NotNull String key, @NotNull HttpBody body, @NotNull HttpHeader<?> @NotNull ... keys) {
        return new FormData() {

            // Object

            @Override
            public @NotNull String getKey() {
                return key;
            }
            @Override
            public @NotNull HttpBody getBody() {
                return body;
            }

            @Override
            public @NotNull HttpHeader<?> @NotNull [] getHeaders() {
                return keys;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                @NotNull FormData that = (FormData) object;
                return Objects.equals(getKey(), that.getKey()) && Objects.equals(getBody(), that.getBody()) && Arrays.equals(getHeaders(), that.getHeaders());
            }
            @Override
            public int hashCode() {
                return Objects.hash(getKey(), getBody(), Arrays.hashCode(getHeaders()));
            }

        };
    }

    // Object

    @NotNull String getKey();

    /**
     * O valor as vezes pode ser nulo pois nem sempre uma key possui um valor no "application/x-www-form-urlencoded"
     * @return
     */
    @Nullable HttpBody getBody();

    /**
     * Os headers só são disponíveis em form datas de `multipart/form-data`, pois os formdatas de `application/x-www-form-urlencoded`
     * @return
     */
    @NotNull HttpHeader<?> @NotNull [] getHeaders();

    /**
     * Também só disponível em `multipart/form-data`, caso contrário será nulo
     * @return
     */
    default @UnknownNullability ContentDisposition getDisposition() {
        return (ContentDisposition) Arrays.stream(getHeaders()).filter(header -> header.getKey().equals(HttpHeaderKey.CONTENT_DISPOSITION)).findFirst().map(HttpHeader::getValue).orElse(null);
    }

}
