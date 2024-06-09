package codes.laivy.jhttp.content;

import org.jetbrains.annotations.NotNull;

public enum SiteData {

    CACHE("cache"),
    CLIENT_HINTS("clientHints"),
    COOKIES("cookies"),
    STORAGE("storage"),
    EXECUTION_CONTEXTS("executionContexts"),
    ;

    private final @NotNull String id;

    SiteData(@NotNull String id) {
        this.id = id;
    }

    // Getters

    public @NotNull String getId() {
        return id;
    }

}
