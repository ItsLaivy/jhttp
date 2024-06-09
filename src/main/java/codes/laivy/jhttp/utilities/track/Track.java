package codes.laivy.jhttp.utilities.track;

import codes.laivy.jhttp.utilities.track.Track.Properties.Audio;
import codes.laivy.jhttp.utilities.track.Track.Properties.Screen;
import codes.laivy.jhttp.utilities.track.Track.Properties.Video;
import org.jetbrains.annotations.*;

import java.util.Objects;
import java.util.regex.Pattern;

import static codes.laivy.jhttp.utilities.track.Track.*;

@ApiStatus.Experimental
public final class Track<T extends Properties> implements Cloneable {

    // Static initializers

    public static @NotNull Track<Audio> audio(@NotNull String id, @NotNull String hint, @NotNull String label, boolean enabled, boolean muted, @NotNull Kind kind, @NotNull State state, @NotNull Audio settings, @NotNull Audio constraints) {
        return new Track<>(id, hint, label, enabled, muted, kind, state, settings, constraints);
    }
    public static @NotNull Track<Video> video(@NotNull String id, @NotNull String hint, @NotNull String label, boolean enabled, boolean muted, @NotNull Kind kind, @NotNull State state, @NotNull Video settings, @NotNull Video constraints) {
        return new Track<>(id, hint, label, enabled, muted, kind, state, settings, constraints);
    }
    public static @NotNull Track<Screen> screen(@NotNull String id, @NotNull String hint, @NotNull String label, boolean enabled, boolean muted, @NotNull Kind kind, @NotNull State state, @NotNull Screen settings, @NotNull Screen constraints) {
        return new Track<>(id, hint, label, enabled, muted, kind, state, settings, constraints);
    }

    // Object

    private final @NotNull String id;
    private final @NotNull String hint;

    private final @NotNull String label;

    private final boolean enabled;
    private final boolean muted;

    private final @NotNull Kind kind;
    private final @NotNull State state;

    private final @NotNull T settings;
    private final @NotNull T constraints;

    private Track(@NotNull String id, @NotNull String hint, @NotNull String label, boolean enabled, boolean muted, @NotNull Kind kind, @NotNull State state, @NotNull T settings, @NotNull T constraints) {
        this.id = id;
        this.hint = hint;
        this.label = label;
        this.enabled = enabled;
        this.muted = muted;
        this.kind = kind;
        this.state = state;
        this.settings = settings;
        this.constraints = constraints;

        if (getKind() == Kind.AUDIO && !(getSettings() instanceof Audio)) {
            throw new IllegalArgumentException("this settings isn't a audio settings");
        } else if (getKind() == Kind.VIDEO && !(getSettings() instanceof Video)) {
            throw new IllegalArgumentException("this settings isn't a video settings");
        } else {
            throw new UnsupportedOperationException("unknown setting class");
        }
    }

    // Getters

    @Contract(pure = true)
    public @NotNull String getId() {
        return id;
    }
    @Contract(pure = true)
    public @NotNull String getHint() {
        return hint;
    }
    @Contract(pure = true)
    public @NotNull String getLabel() {
        return label;
    }

    @Contract(pure = true)
    public boolean isEnabled() {
        return enabled;
    }
    @Contract(pure = true)
    public boolean isMuted() {
        return muted;
    }

    @Contract(pure = true)
    public @NotNull Kind getKind() {
        return kind;
    }
    @Contract(pure = true)
    public @NotNull State getState() {
        return state;
    }

    public @NotNull T getSettings() {
        return settings;
    }
    public @NotNull T getConstraints() {
        return constraints;
    }

    // Implementations

    @Override
    public @NotNull Track<T> clone() {
        try {
            //noinspection unchecked
            return (Track<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("cannot clone MediaStream track", e);
        }
    }

    // Classes

    public enum Kind {
        AUDIO("audio"),
        VIDEO("video"),
        ;

        private final @NotNull String id;

        Kind(@NotNull String id) {
            this.id = id;
        }

        // Getters

        public @NotNull String getId() {
            return id;
        }

    }
    public enum State {
        LIVE("live"),
        ENDED("ended"),
        ;

        private final @NotNull String id;

        State(@NotNull String id) {
            this.id = id;
        }

        // Getters

        public @NotNull String getId() {
            return id;
        }

    }

    public static abstract class Properties {

        // Static initializers

        public static final @NotNull Pattern DEVICE_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-_.]+$");
        public static final @NotNull Pattern GROUP_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-_.]+$");

        public static @NotNull Audio.Builder audio() {
            return new Audio.Builder();
        }
        public static @NotNull Video.Builder video() {
            return new Video.Builder();
        }
        public static @NotNull Screen.Builder screen() {
            return new Screen.Builder();
        }

        // Object

        private final @Nullable String deviceId;
        private final @Nullable String groupId;

        private Properties(@Nullable String deviceId, @Nullable String groupId) {
            this.deviceId = deviceId;
            this.groupId = groupId;

            if (deviceId != null && !deviceId.matches(DEVICE_ID_PATTERN.pattern())) {
                throw new IllegalArgumentException("this device id is invalid");
            } else if (groupId != null && !groupId.matches(GROUP_ID_PATTERN.pattern())) {
                throw new IllegalArgumentException("this device group is invalid");
            }
        }

        // Getters

        public @Nullable String getDeviceId() {
            return deviceId;
        }
        public @Nullable String getGroupId() {
            return groupId;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            @NotNull Properties properties = (Properties) object;
            return Objects.equals(deviceId, properties.deviceId) && Objects.equals(groupId, properties.groupId);
        }
        @Override
        public int hashCode() {
            return Objects.hash(deviceId, groupId);
        }

        @Override
        public abstract @NotNull String toString();

        // Classes

        public static class Audio extends Properties {

            private final @Nullable Boolean autoGainControl;
            private final @Nullable Boolean echoCancellation;
            private final @Nullable Boolean noiseSuppression;

            @Range(from = 0, to = 1)
            private final @Nullable Float volume;
            private final @Nullable Double latency;
            private final @Nullable Integer channels;

            private final @Nullable Long sampleRate;
            private final @Nullable Long sampleSize;

            private Audio(
                    @Nullable String deviceId,
                    @Nullable String groupId,

                    @Nullable Long sampleRate,
                    @Nullable Long sampleSize,

                    @Nullable Boolean autoGainControl,
                    @Nullable Boolean echoCancellation,
                    @Nullable Boolean noiseSuppression,

                    @Range(from = 0, to = 1)
                    @Nullable Float volume,
                    @Nullable Double latency,
                    @Nullable Integer channels
            ) {
                super(deviceId, groupId);

                this.sampleRate = sampleRate;
                this.sampleSize = sampleSize;

                this.autoGainControl = autoGainControl;
                this.echoCancellation = echoCancellation;
                this.noiseSuppression = noiseSuppression;

                this.volume = volume;
                this.latency = latency;
                this.channels = channels;

                if (volume != null && (volume < 0 || volume > 1)) {
                    throw new IllegalArgumentException("invalid volume value '" + volume + "'");
                }
            }

            // Getters

            public @Nullable Boolean autoGainControl() {
                return autoGainControl;
            }
            public @Nullable Boolean echoCancellation() {
                return echoCancellation;
            }
            public @Nullable Boolean noiseSuppression() {
                return noiseSuppression;
            }

            @Deprecated
            @Range(from = 0, to = 1)
            public @Nullable Float getVolume() {
                return volume;
            }
            public @Nullable Double getLatency() {
                return latency;
            }
            public @Nullable Integer getChannels() {
                return channels;
            }

            public @Nullable Long getSampleRate() {
                return sampleRate;
            }
            public @Nullable Long getSampleSize() {
                return sampleSize;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                if (!super.equals(object)) return false;
                @NotNull Audio audio = (Audio) object;
                return Objects.equals(autoGainControl, audio.autoGainControl) && Objects.equals(echoCancellation, audio.echoCancellation) && Objects.equals(noiseSuppression, audio.noiseSuppression) && Objects.equals(volume, audio.volume) && Objects.equals(latency, audio.latency) && Objects.equals(channels, audio.channels) && Objects.equals(sampleRate, audio.sampleRate) && Objects.equals(sampleSize, audio.sampleSize);
            }
            @Override
            public int hashCode() {
                return Objects.hash(super.hashCode(), autoGainControl, echoCancellation, noiseSuppression, volume, latency, channels, sampleRate, sampleSize);
            }

            @Override
            public @NotNull String toString() {
                @NotNull StringBuilder sb = new StringBuilder();

                if (getDeviceId() != null) {
                    sb.append("deviceId=").append(getDeviceId()).append(";");
                } if (getGroupId() != null) {
                    sb.append("groupId=").append(getGroupId()).append(";");
                } if (getSampleRate() != null) {
                    sb.append("sampleRate=").append(getSampleRate()).append(";");
                } if (getSampleSize() != null) {
                    sb.append("sampleSize=").append(getSampleSize()).append(";");
                } if (autoGainControl != null) {
                    sb.append("autoGainControl=").append(autoGainControl).append(";");
                } if (echoCancellation != null) {
                    sb.append("echoCancellation=").append(echoCancellation).append(";");
                } if (noiseSuppression != null) {
                    sb.append("noiseSuppression=").append(noiseSuppression).append(";");
                } if (volume != null) {
                    sb.append("volume=").append(volume).append(";");
                } if (latency != null) {
                    sb.append("latency=").append(latency).append(";");
                } if (channels != null) {
                    sb.append("channels=").append(channels).append(";");
                }

                if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ';') {
                    sb.setLength(sb.length() - 1);
                }

                return sb.toString();
            }

            // Classes

            public static final class Builder {

                private @Nullable String deviceId;
                private @Nullable String groupId;

                private @Nullable Boolean autoGainControl;
                private @Nullable Boolean echoCancellation;
                private @Nullable Boolean noiseSuppression;

                @Range(from = 0, to = 1)
                private @Nullable Float volume;
                private @Nullable Double latency;
                private @Nullable Integer channels;

                private @Nullable Long sampleRate;
                private @Nullable Long sampleSize;

                private Builder() {
                }

                // Getters

                @Contract("_->this")
                public @NotNull Builder deviceId(@NotNull String deviceId) {
                    this.deviceId = deviceId;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder groupId(@NotNull String groupId) {
                    this.groupId = groupId;
                    return this;
                }

                @Contract("_->this")
                public @NotNull Builder autoGainControl(boolean autoGainControl) {
                    this.autoGainControl = autoGainControl;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder echoCancellation(boolean echoCancellation) {
                    this.echoCancellation = echoCancellation;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder noiseSuppression(boolean noiseSuppression) {
                    this.noiseSuppression = noiseSuppression;
                    return this;
                }

                @Contract("_->this")
                public @NotNull Builder noiseSuppression(float volume) {
                    this.volume = volume;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder latency(double latency) {
                    this.latency = latency;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder channels(int channels) {
                    this.channels = channels;
                    return this;
                }

                @Contract("_->this")
                public @NotNull Builder sampleRate(long sampleRate) {
                    this.sampleRate = sampleRate;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder sampleSize(long sampleSize) {
                    this.sampleSize = sampleSize;
                    return this;
                }

                // Builder

                public @NotNull Audio build() {
                    return new Audio(deviceId, groupId, sampleRate, sampleSize, autoGainControl, echoCancellation, noiseSuppression, volume, latency, channels);
                }

            }

        }
        public static class Video extends Properties {

            private final @Nullable Double aspectRatio;
            private final @Nullable Facing facingMode;
            private final @Nullable Double frameRate;
            private final @Nullable Long height;
            private final @Nullable Long width;
            private final @Nullable Resize resize;

            private Video(
                    @Nullable String deviceId,
                    @Nullable String groupId,

                    @Nullable Double aspectRatio,
                    @Nullable Double frameRate,

                    @Nullable Long height,
                    @Nullable Long width,

                    @Nullable Facing facingMode,
                    @Nullable Resize resize
            ) {
                super(deviceId, groupId);
                this.aspectRatio = aspectRatio;
                this.facingMode = facingMode;
                this.frameRate = frameRate;
                this.height = height;
                this.width = width;
                this.resize = resize;
            }

            // Getters

            public @Nullable Double getAspectRatio() {
                return aspectRatio;
            }
            public @Nullable Double getFrameRate() {
                return frameRate;
            }

            public @Nullable Long getHeight() {
                return height;
            }
            public @Nullable Long getWidth() {
                return width;
            }

            public @Nullable Resize getResize() {
                return resize;
            }
            public @Nullable Facing getFacingMode() {
                return facingMode;
            }

            // Implementations

            @Override
            public @NotNull String toString() {
                @NotNull StringBuilder sb = new StringBuilder();

                if (getDeviceId() != null) {
                    sb.append("deviceId=").append(getDeviceId()).append(";");
                } if (getGroupId() != null) {
                    sb.append("groupId=").append(getGroupId()).append(";");
                } if (getAspectRatio() != null) {
                    sb.append("aspectRatio=").append(getAspectRatio()).append(";");
                } if (getFacingMode() != null) {
                    sb.append("facingMode=").append(getFacingMode().getId()).append(";");
                } if (getFrameRate() != null) {
                    sb.append("frameRate=").append(getFrameRate()).append(";");
                } if (getHeight() != null) {
                    sb.append("height=").append(getHeight()).append(";");
                } if (getWidth() != null) {
                    sb.append("width=").append(getWidth()).append(";");
                } if (getResize() != null) {
                    sb.append("resizeMode=").append(getResize().getId()).append(";");
                }

                if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ';') {
                    sb.setLength(sb.length() - 1);
                }

                return sb.toString();
            }

            // Classes

            public enum Facing {

                USER("user"),
                ENVIRONMENT("environment"),
                LEFT("left"),
                RIGHT("right"),
                ;

                private final @NotNull String id;

                Facing(@NotNull String id) {
                    this.id = id;
                }

                // Getters

                public @NotNull String getId() {
                    return id;
                }

            }
            public enum Resize {

                NONE("none"),
                CROP_AND_SCALE("crop-and-scale"),
                ;

                private final @NotNull String id;

                Resize(@NotNull String id) {
                    this.id = id;
                }

                // Getters

                public @NotNull String getId() {
                    return id;
                }

            }

            public static final class Builder {

                private @Nullable String deviceId;
                private @Nullable String groupId;

                private @Nullable Double aspectRatio;
                private @Nullable Double frameRate;

                private @Nullable Long height;
                private @Nullable Long width;

                private @Nullable Facing facing;
                private @Nullable Resize resize;

                private Builder() {
                }

                // Getters

                @Contract("_->this")
                public @NotNull Builder deviceId(@NotNull String deviceId) {
                    this.deviceId = deviceId;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder groupId(@NotNull String groupId) {
                    this.groupId = groupId;
                    return this;
                }

                @Contract("_->this")
                public @NotNull Builder aspectRatio(double aspectRatio) {
                    this.aspectRatio = aspectRatio;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder frameRate(double frameRate) {
                    this.frameRate = frameRate;
                    return this;
                }

                @Contract("_->this")
                public @NotNull Builder height(long height) {
                    this.height = height;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder width(long width) {
                    this.width = width;
                    return this;
                }

                @Contract("_->this")
                public @NotNull Builder facing(@NotNull Facing facing) {
                    this.facing = facing;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder resize(@NotNull Resize resize) {
                    this.resize = resize;
                    return this;
                }

                // Builder

                public @NotNull Video build() {
                    return new Video(deviceId, groupId, aspectRatio, frameRate, height, width, facing, resize);
                }

            }

        }
        public static class Screen extends Video {

            private final @Nullable Cursor cursor;
            private final @Nullable DisplaySurface displaySurface;
            private final @Nullable Boolean logicalSurface;

            private Screen(@Nullable String deviceId, @Nullable String groupId, @Nullable Double aspectRatio, @Nullable Double frameRate, @Nullable Long height, @Nullable Long width, @Nullable Facing facingMode, @Nullable Resize resize, @Nullable Cursor cursor, @Nullable DisplaySurface displaySurface, boolean logicalSurface) {
                super(deviceId, groupId, aspectRatio, frameRate, height, width, facingMode, resize);
                this.cursor = cursor;
                this.displaySurface = displaySurface;
                this.logicalSurface = logicalSurface;
            }

            // Getters

            public @Nullable Cursor getCursor() {
                return cursor;
            }
            public @Nullable DisplaySurface getDisplaySurface() {
                return displaySurface;
            }
            public @Nullable Boolean isLogicalSurface() {
                return logicalSurface;
            }

            // Implementations

            @Override
            public @NotNull String toString() {
                @NotNull StringBuilder sb = new StringBuilder();

                if (getDeviceId() != null) {
                    sb.append("deviceId=").append(getDeviceId()).append(";");
                } if (getGroupId() != null) {
                    sb.append("groupId=").append(getGroupId()).append(";");
                } if (getAspectRatio() != null) {
                    sb.append("aspectRatio=").append(getAspectRatio()).append(";");
                } if (getFacingMode() != null) {
                    sb.append("facingMode=").append(getFacingMode().getId()).append(";");
                } if (getFrameRate() != null) {
                    sb.append("frameRate=").append(getFrameRate()).append(";");
                } if (getHeight() != null) {
                    sb.append("height=").append(getHeight()).append(";");
                } if (getWidth() != null) {
                    sb.append("width=").append(getWidth()).append(";");
                } if (getResize() != null) {
                    sb.append("resizeMode=").append(getResize().getId()).append(";");
                } if (getCursor() != null) {
                    sb.append("cursor=").append(getCursor().getId()).append(";");
                } if (getDisplaySurface() != null) {
                    sb.append("displaySurface=").append(getDisplaySurface().getId()).append(";");
                } if (isLogicalSurface() != null) {
                    sb.append("logicalSurface=").append(isLogicalSurface()).append(";");
                }

                if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ';') {
                    sb.setLength(sb.length() - 1);
                }

                return sb.toString();
            }

            // Classes

            public enum Cursor {

                ALWAYS("always"),
                MOTION("motion"),
                NEVER("never"),
                ;

                private final @NotNull String id;

                Cursor(@NotNull String id) {
                    this.id = id;
                }

                // Getters

                public @NotNull String getId() {
                    return id;
                }

            }
            public enum DisplaySurface {

                ALWAYS("application"),
                BROWSER("browser"),
                MONITOR("monitor"),
                WINDOW("window"),
                ;

                private final @NotNull String id;

                DisplaySurface(@NotNull String id) {
                    this.id = id;
                }

                // Getters

                public @NotNull String getId() {
                    return id;
                }

            }

            public static final class Builder {

                private @Nullable String deviceId;
                private @Nullable String groupId;

                private @Nullable Double aspectRatio;
                private @Nullable Double frameRate;

                private @Nullable Long height;
                private @Nullable Long width;

                private @Nullable Facing facing;
                private @Nullable Resize resize;
                private @Nullable Cursor cursor;
                private @Nullable DisplaySurface displaySurface;

                private boolean logicalSurface;
                
                private Builder() {
                }
                
                // Getters

                @Contract("_->this")
                public @NotNull Builder deviceId(@NotNull String deviceId) {
                    this.deviceId = deviceId;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder groupId(@NotNull String groupId) {
                    this.groupId = groupId;
                    return this;
                }

                @Contract("_->this")
                public @NotNull Builder aspectRatio(double aspectRatio) {
                    this.aspectRatio = aspectRatio;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder frameRate(double frameRate) {
                    this.frameRate = frameRate;
                    return this;
                }

                @Contract("_->this")
                public @NotNull Builder height(long height) {
                    this.height = height;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder width(long width) {
                    this.width = width;
                    return this;
                }

                @Contract("_->this")
                public @NotNull Builder facing(@NotNull Facing facing) {
                    this.facing = facing;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder resize(@NotNull Resize resize) {
                    this.resize = resize;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder cursor(@NotNull Cursor cursor) {
                    this.cursor = cursor;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder displaySurface(@NotNull DisplaySurface displaySurface) {
                    this.displaySurface = displaySurface;
                    return this;
                }
                @Contract("_->this")
                public @NotNull Builder logicalSurface(boolean logicalSurface) {
                    this.logicalSurface = logicalSurface;
                    return this;
                }

                // Builder

                public @NotNull Screen build() {
                    return new Screen(deviceId, groupId, aspectRatio, frameRate, height, width, facing, resize, cursor, displaySurface, logicalSurface);
                }

            }

        }

    }

}