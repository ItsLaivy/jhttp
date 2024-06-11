package codes.laivy.jhttp.utilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

public enum EffectiveConnectionType {

    SLOW_2G("slow-2g", Duration.ofMillis(2000), NetworkSpeed.create(NetworkSpeed.Category.KILOBITS, 50)),
    _2G("2g", Duration.ofMillis(1400), NetworkSpeed.create(NetworkSpeed.Category.KILOBITS, 70)),
    _3G("3g", Duration.ofMillis(270), NetworkSpeed.create(NetworkSpeed.Category.KILOBITS, 700)),
    _4G("4g", Duration.ZERO, null),
    ;

    private final @NotNull String id;
    private final @NotNull Duration roundTripTime;
    private final @Nullable NetworkSpeed speed;

    EffectiveConnectionType(@NotNull String id, @NotNull Duration roundTripTime, @Nullable NetworkSpeed speed) {
        this.id = id;
        this.roundTripTime = roundTripTime;
        this.speed = speed;
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull Duration getRoundTripTime() {
        return roundTripTime;
    }
    public @Nullable NetworkSpeed getSpeed() {
        return speed;
    }

    // Static initializers

    public static @NotNull EffectiveConnectionType getById(@NotNull String id) {
        @NotNull Optional<EffectiveConnectionType> optional = Arrays.stream(values()).filter(type -> type.getId().equalsIgnoreCase(id)).findFirst();
        return optional.orElseThrow(() -> new NullPointerException("there's no effective connection type with id '" + id + "'"));
    }

}
