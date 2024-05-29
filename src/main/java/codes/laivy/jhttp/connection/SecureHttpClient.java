package codes.laivy.jhttp.connection;

import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLSession;

public interface SecureHttpClient extends HttpClient {
    @Nullable SSLSession getSession();
}
