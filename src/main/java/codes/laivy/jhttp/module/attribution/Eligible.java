package codes.laivy.jhttp.module.attribution;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public enum Eligible {

    EVENT_SOURCE("event-source"),
    NAVIGATION_SOURCE("navigation-source"),
    TRIGGER("trigger"),
    ;

    private final @NotNull String id;

    Eligible(@NotNull String id) {
        this.id = id;
    }

    // Getters

    public @NotNull String getId() {
        return id;
    }

    // Static initializers

    public static @NotNull Eligible getById(@NotNull String id) {
        @NotNull Optional<Eligible> optional = Arrays.stream(values()).filter(type -> type.getId().equalsIgnoreCase(id)).findFirst();
        return optional.orElseThrow(() -> new NullPointerException("there's no attribution reporting eligible type with id '" + id + "'"));
    }

}
