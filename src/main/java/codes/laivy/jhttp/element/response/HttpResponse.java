package codes.laivy.jhttp.element.response;

import codes.laivy.jhttp.authorization.Credentials;
import codes.laivy.jhttp.body.HttpBody;
import codes.laivy.jhttp.client.HttpClient;
import codes.laivy.jhttp.deferred.Deferred;
import codes.laivy.jhttp.element.HttpElement;
import codes.laivy.jhttp.element.HttpStatus;
import codes.laivy.jhttp.element.Method;
import codes.laivy.jhttp.element.Target;
import codes.laivy.jhttp.encoding.Encoding;
import codes.laivy.jhttp.headers.HttpHeader;
import codes.laivy.jhttp.headers.HttpHeaderKey;
import codes.laivy.jhttp.headers.HttpHeaders;
import codes.laivy.jhttp.headers.Wildcard;
import codes.laivy.jhttp.media.MediaType;
import codes.laivy.jhttp.module.*;
import codes.laivy.jhttp.module.CrossOrigin.EmbedderPolicy;
import codes.laivy.jhttp.module.CrossOrigin.OpenerPolicy;
import codes.laivy.jhttp.module.CrossOrigin.ResourcePolicy;
import codes.laivy.jhttp.module.UserAgent.Product;
import codes.laivy.jhttp.module.connection.Connection;
import codes.laivy.jhttp.module.content.AcceptRange;
import codes.laivy.jhttp.module.content.ContentDisposition;
import codes.laivy.jhttp.module.content.ContentRange;
import codes.laivy.jhttp.module.content.ContentSecurityPolicy;
import codes.laivy.jhttp.network.BitMeasure;
import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.url.URIAuthority;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

/**
 * This interface represents an HTTP response.
 *
 * @author Daniel Richard (Laivy)
 * @version 1.0-SNAPSHOT
 */
@SuppressWarnings("unchecked")
public interface HttpResponse extends HttpElement {

    // Static initializers

    static @NotNull HttpResponse create(
            @NotNull HttpVersion version,
            @NotNull HttpStatus status,
            @NotNull HttpBody body
    ) {
        return create(version, status, version.getHeaderFactory().createMutable(Target.RESPONSE), body);
    }

    static @NotNull HttpResponse create(
            @NotNull HttpVersion version,
            @NotNull HttpStatus status,
            @NotNull HttpHeaders headers,
            @NotNull HttpBody body
    ) {
        return version.getResponseFactory().create(status, headers, body);
    }

    // Object

    /**
     * Retrieves the status of this HTTP response
     *
     * @return the version of this response
     */
    @NotNull
    HttpStatus getStatus();

    // Modules

    default @Nullable Wildcard<@Nullable URIAuthority> getAccessControlAllowOrigin() {
        return getHeaders().first(HttpHeaderKey.ACCESS_CONTROL_ALLOW_ORIGIN).map(HttpHeader::getValue).orElse(null);
    }

    default void setAccessControlAllowOrigin(@Nullable Wildcard<@Nullable URIAuthority> value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ACCESS_CONTROL_ALLOW_ORIGIN.create(value));
        else getHeaders().remove(HttpHeaderKey.ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    default @Nullable AcceptRange getAcceptRanges() {
        return getHeaders().first(HttpHeaderKey.ACCEPT_RANGES).map(HttpHeader::getValue).orElse(null);
    }

    default void setAcceptRanges(@Nullable AcceptRange value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ACCEPT_RANGES.create(value));
        else getHeaders().remove(HttpHeaderKey.ACCEPT_RANGES);
    }

    default @Nullable Duration getAge() {
        return getHeaders().first(HttpHeaderKey.AGE).map(HttpHeader::getValue).orElse(null);
    }

    default void setAge(@Nullable Duration value) {
        if (value != null) getHeaders().put(HttpHeaderKey.AGE.create(value));
        else getHeaders().remove(HttpHeaderKey.AGE);
    }

    default @NotNull Method @Nullable [] getAllow() {
        return getHeaders().first(HttpHeaderKey.ALLOW).map(HttpHeader::getValue).orElse(null);
    }

    default void setAllow(@NotNull Method @Nullable ... value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ALLOW.create(value));
        else getHeaders().remove(HttpHeaderKey.ALLOW);
    }

    default @Nullable CacheControl getCacheControl() {
        return getHeaders().first(HttpHeaderKey.CACHE_CONTROL).map(HttpHeader::getValue).orElse(null);
    }

    default void setCacheControl(@Nullable CacheControl value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CACHE_CONTROL.create(value));
        else getHeaders().remove(HttpHeaderKey.CACHE_CONTROL);
    }

    default @Nullable Connection getConnection() {
        return getHeaders().first(HttpHeaderKey.CONNECTION).map(HttpHeader::getValue).orElse(null);
    }

    default void setConnection(@Nullable Connection value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CONNECTION.create(value));
        else getHeaders().remove(HttpHeaderKey.CONNECTION);
    }

    default @NotNull Deferred<Encoding> @Nullable [] getContentEncoding() {
        return getHeaders().first(HttpHeaderKey.CONTENT_ENCODING).map(HttpHeader::getValue).orElse(null);
    }

    default void setContentEncoding(@NotNull Deferred<Encoding> @Nullable ... value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CONTENT_ENCODING.create(value));
        else getHeaders().remove(HttpHeaderKey.CONTENT_ENCODING);
    }

    default @NotNull Locale @Nullable [] getContentLanguage() {
        return getHeaders().first(HttpHeaderKey.CONTENT_LANGUAGE).map(HttpHeader::getValue).orElse(null);
    }

    default void setContentLanguage(@NotNull Locale @Nullable ... value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CONTENT_LANGUAGE.create(value));
        else getHeaders().remove(HttpHeaderKey.CONTENT_LANGUAGE);
    }

    default @Nullable BitMeasure getContentLength() {
        return getHeaders().first(HttpHeaderKey.CONTENT_LENGTH).map(HttpHeader::getValue).orElse(null);
    }

    default void setContentLength(@Nullable BitMeasure value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CONTENT_LENGTH.create(value));
        else getHeaders().remove(HttpHeaderKey.CONTENT_LENGTH);
    }

    default @Nullable Location getContentLocation() {
        return getHeaders().first(HttpHeaderKey.CONTENT_LOCATION).map(HttpHeader::getValue).orElse(null);
    }

    default void setContentLocation(@Nullable Location value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CONTENT_LOCATION.create(value));
        else getHeaders().remove(HttpHeaderKey.CONTENT_LOCATION);
    }

    default @Nullable ContentDisposition getContentDisposition() {
        return getHeaders().first(HttpHeaderKey.CONTENT_DISPOSITION).map(HttpHeader::getValue).orElse(null);
    }

    default void setContentDisposition(@Nullable ContentDisposition value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CONTENT_DISPOSITION.create(value));
        else getHeaders().remove(HttpHeaderKey.CONTENT_DISPOSITION);
    }

    default @Nullable ContentRange getContentRange() {
        return getHeaders().first(HttpHeaderKey.CONTENT_RANGE).map(HttpHeader::getValue).orElse(null);
    }

    default void setContentRange(@Nullable ContentRange value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CONTENT_RANGE.create(value));
        else getHeaders().remove(HttpHeaderKey.CONTENT_RANGE);
    }

    default @Nullable MediaType<?> getContentType() {
        return getHeaders().first(HttpHeaderKey.CONTENT_TYPE).map(HttpHeader::getValue).orElse(null);
    }

    default void setContentType(@Nullable MediaType<?> value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CONTENT_TYPE.create(value));
        else getHeaders().remove(HttpHeaderKey.CONTENT_TYPE);
    }

    default @Nullable OffsetDateTime getDate() {
        return getHeaders().first(HttpHeaderKey.DATE).map(HttpHeader::getValue).orElse(null);
    }

    default void setDate(@Nullable OffsetDateTime value) {
        if (value != null) getHeaders().put(HttpHeaderKey.DATE.create(value));
        else getHeaders().remove(HttpHeaderKey.DATE);
    }

    default @Nullable EntityTag getEntityTag() {
        return getHeaders().first(HttpHeaderKey.ETAG).map(HttpHeader::getValue).orElse(null);
    }

    default void setEntityTag(@Nullable EntityTag value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ETAG.create(value));
        else getHeaders().remove(HttpHeaderKey.ETAG);
    }

    default @Nullable OffsetDateTime getExpires() {
        return getHeaders().first(HttpHeaderKey.EXPIRES).map(HttpHeader::getValue).orElse(null);
    }

    default void setExpires(@Nullable OffsetDateTime value) {
        if (value != null) getHeaders().put(HttpHeaderKey.EXPIRES.create(value));
        else getHeaders().remove(HttpHeaderKey.EXPIRES);
    }

    default @Nullable OffsetDateTime getLastModified() {
        return getHeaders().first(HttpHeaderKey.LAST_MODIFIED).map(HttpHeader::getValue).orElse(null);
    }

    default void setLastModified(@Nullable OffsetDateTime value) {
        if (value != null) getHeaders().put(HttpHeaderKey.LAST_MODIFIED.create(value));
        else getHeaders().remove(HttpHeaderKey.LAST_MODIFIED);
    }

    default @Nullable Location getLocation() {
        return getHeaders().first(HttpHeaderKey.LOCATION).map(HttpHeader::getValue).orElse(null);
    }

    default void setLocation(@Nullable Location value) {
        if (value != null) getHeaders().put(HttpHeaderKey.LOCATION.create(value));
        else getHeaders().remove(HttpHeaderKey.LOCATION);
    }

    @Deprecated
    default boolean isPragma() {
        return getHeaders().contains(HttpHeaderKey.PRAGMA);
    }

    @Deprecated
    default void setPragma(boolean pragma) {
        if (pragma) getHeaders().put(HttpHeaderKey.PRAGMA.create(null));
        else getHeaders().remove(HttpHeaderKey.PRAGMA);
    }

    @ApiStatus.Experimental
    default @Nullable String getRetryAfter() {
        return getHeaders().first(HttpHeaderKey.RETRY_AFTER).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setRetryAfter(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.RETRY_AFTER.create(value));
        else getHeaders().remove(HttpHeaderKey.RETRY_AFTER);
    }

    default @Nullable Product getServer() {
        return getHeaders().first(HttpHeaderKey.SERVER).map(HttpHeader::getValue).orElse(null);
    }

    default void setServer(@Nullable Product value) {
        if (value != null) getHeaders().put(HttpHeaderKey.SERVER.create(value));
        else getHeaders().remove(HttpHeaderKey.SERVER);
    }

    @ApiStatus.Experimental
    default @Nullable String getStrictTransportSecurity() {
        return getHeaders().first(HttpHeaderKey.STRICT_TRANSPORT_SECURITY).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setStrictTransportSecurity(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.STRICT_TRANSPORT_SECURITY.create(value));
        else getHeaders().remove(HttpHeaderKey.STRICT_TRANSPORT_SECURITY);
    }

    default @NotNull HttpHeaderKey<?> @Nullable [] getTrailer() {
        return getHeaders().first(HttpHeaderKey.TRAILER).map(HttpHeader::getValue).orElse(null);
    }

    default void setTrailer(@NotNull HttpHeaderKey<?> @Nullable ... value) {
        if (value != null) getHeaders().put(HttpHeaderKey.TRAILER.create(value));
        else getHeaders().remove(HttpHeaderKey.TRAILER);
    }

    default @NotNull Deferred<Encoding> @Nullable [] getTransferEncoding() {
        return getHeaders().first(HttpHeaderKey.TRANSFER_ENCODING).map(HttpHeader::getValue).orElse(null);
    }

    default void setTransferEncoding(@NotNull Deferred<Encoding> @Nullable ... value) {
        if (value != null) getHeaders().put(HttpHeaderKey.TRANSFER_ENCODING.create(value));
        else getHeaders().remove(HttpHeaderKey.TRANSFER_ENCODING);
    }

    default @NotNull Upgrade @Nullable [] getUpgrade() {
        return getHeaders().first(HttpHeaderKey.UPGRADE).map(HttpHeader::getValue).orElse(null);
    }

    default void setUpgrade(@NotNull Upgrade @Nullable ... value) {
        if (value != null) getHeaders().put(HttpHeaderKey.UPGRADE.create(value));
        else getHeaders().remove(HttpHeaderKey.UPGRADE);
    }

    default @Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> getVary() {
        return getHeaders().first(HttpHeaderKey.VARY).map(HttpHeader::getValue).orElse(null);
    }

    default void setVary(@Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> value) {
        if (value != null) getHeaders().put(HttpHeaderKey.VARY.create(value));
        else getHeaders().remove(HttpHeaderKey.VARY);
    }

    @ApiStatus.Experimental
    default @Nullable String getVia() {
        return getHeaders().first(HttpHeaderKey.VIA).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setVia(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.VIA.create(value));
        else getHeaders().remove(HttpHeaderKey.VIA);
    }

    @ApiStatus.Experimental
    default @Nullable String getWarning() {
        return getHeaders().first(HttpHeaderKey.WARNING).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setWarning(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.WARNING.create(value));
        else getHeaders().remove(HttpHeaderKey.WARNING);
    }

    @ApiStatus.Experimental
    default @Nullable String getAuthenticate() {
        return getHeaders().first(HttpHeaderKey.WWW_AUTHENTICATE).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setAuthenticate(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.WWW_AUTHENTICATE.create(value));
        else getHeaders().remove(HttpHeaderKey.WWW_AUTHENTICATE);
    }

    @ApiStatus.Experimental
    default @Nullable String getFrameOptions() {
        return getHeaders().first(HttpHeaderKey.X_FRAME_OPTIONS).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setFrameOptions(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.X_FRAME_OPTIONS.create(value));
        else getHeaders().remove(HttpHeaderKey.X_FRAME_OPTIONS);
    }

    default @NotNull HttpHeaderKey<?> @Nullable [] getAcceptClientHints() {
        return getHeaders().first(HttpHeaderKey.ACCEPT_CH).map(HttpHeader::getValue).orElse(null);
    }

    default void setAcceptClientHints(@NotNull HttpHeaderKey<?> @Nullable ... value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ACCEPT_CH.create(value));
        else getHeaders().remove(HttpHeaderKey.ACCEPT_CH);
    }

    @Deprecated
    default void setAcceptClientHints(@Nullable Duration lifetime, @NotNull HttpHeaderKey<?> @Nullable ... value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ACCEPT_CH.create(value));
        else getHeaders().remove(HttpHeaderKey.ACCEPT_CH);

        if (lifetime != null) getHeaders().put(HttpHeaderKey.ACCEPT_CH_LIFETIME.create(lifetime));
        else getHeaders().remove(HttpHeaderKey.ACCEPT_CH_LIFETIME);
    }

    default @NotNull MediaType<?> @Nullable [] getAcceptPatch() {
        return getHeaders().first(HttpHeaderKey.ACCEPT_PATCH).map(HttpHeader::getValue).orElse(null);
    }

    default void setAcceptPatch(@NotNull MediaType<?> @Nullable [] value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ACCEPT_PATCH.create(value));
        else getHeaders().remove(HttpHeaderKey.ACCEPT_PATCH);
    }

    default MediaType.@NotNull Type @Nullable [] getAcceptPost() {
        return getHeaders().first(HttpHeaderKey.ACCEPT_POST).map(HttpHeader::getValue).orElse(null);
    }

    default void setAcceptPost(MediaType.@NotNull Type @Nullable ... value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ACCEPT_POST.create(value));
        else getHeaders().remove(HttpHeaderKey.ACCEPT_POST);
    }

    default @Nullable Boolean getAccessControlAllowCredentials() {
        return getHeaders().first(HttpHeaderKey.ACCESS_CONTROL_ALLOW_CREDENTIALS).map(HttpHeader::getValue).orElse(null);
    }

    default void setAccessControlAllowCredentials(@Nullable Boolean value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ACCESS_CONTROL_ALLOW_CREDENTIALS.create(value));
        else getHeaders().remove(HttpHeaderKey.ACCESS_CONTROL_ALLOW_CREDENTIALS);
    }

    default @Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> getAccessControlAllowHeaders() {
        return getHeaders().first(HttpHeaderKey.ACCESS_CONTROL_ALLOW_HEADERS).map(HttpHeader::getValue).orElse(null);
    }

    default void setAccessControlAllowHeaders(@Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ACCESS_CONTROL_ALLOW_HEADERS.create(value));
        else getHeaders().remove(HttpHeaderKey.ACCESS_CONTROL_ALLOW_HEADERS);
    }

    default @Nullable Wildcard<@NotNull Method @NotNull []> getAccessControlAllowMethods() {
        return getHeaders().first(HttpHeaderKey.ACCESS_CONTROL_ALLOW_METHODS).map(HttpHeader::getValue).orElse(null);
    }

    default void setAccessControlAllowMethods(@Nullable Wildcard<@NotNull Method @NotNull []> value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ACCESS_CONTROL_ALLOW_METHODS.create(value));
        else getHeaders().remove(HttpHeaderKey.ACCESS_CONTROL_ALLOW_METHODS);
    }

    default @Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> getAccessControlExposeHeaders() {
        return getHeaders().first(HttpHeaderKey.ACCESS_CONTROL_EXPOSE_HEADERS).map(HttpHeader::getValue).orElse(null);
    }

    default void setAccessControlExposeHeaders(@Nullable Wildcard<@NotNull HttpHeaderKey<?> @NotNull []> value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ACCESS_CONTROL_EXPOSE_HEADERS.create(value));
        else getHeaders().remove(HttpHeaderKey.ACCESS_CONTROL_EXPOSE_HEADERS);
    }

    // todo: create an object (builder) that represents the Access Control things
    default @Nullable Duration getAccessControlMaxAge() {
        return getHeaders().first(HttpHeaderKey.ACCESS_CONTROL_MAX_AGE).map(HttpHeader::getValue).orElse(null);
    }

    default void setAccessControlMaxAge(@Nullable Duration value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ACCESS_CONTROL_MAX_AGE.create(value));
        else getHeaders().remove(HttpHeaderKey.ACCESS_CONTROL_MAX_AGE);
    }

    default @NotNull AlternativeService @Nullable [] getAlternativeService() {
        @Nullable Optional<@NotNull AlternativeService @NotNull []> optional = getHeaders().first(HttpHeaderKey.ALT_SVC).map(HttpHeader::getValue).orElse(null);
        return optional != null ? optional.orElse(new AlternativeService[0]) : null;
    }

    default void setAlternativeService(@NotNull AlternativeService @Nullable ... value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ALT_SVC.create(Optional.of(value)));
        else getHeaders().remove(HttpHeaderKey.ALT_SVC);
    }

    default @Nullable URIAuthority getAlternativeUsed() {
        return getHeaders().first(HttpHeaderKey.ALT_USED).map(HttpHeader::getValue).orElse(null);
    }

    default void set(@Nullable URIAuthority value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ALT_USED.create(value));
        else getHeaders().remove(HttpHeaderKey.ALT_USED);
    }

    @ApiStatus.Experimental
    default @Nullable JsonObject getAttributionReportingRegisterSource() {
        return getHeaders().first(HttpHeaderKey.ATTRIBUTION_REPORTING_REGISTER_SOURCE).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setAttributionReportingRegisterSource(@Nullable JsonObject value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ATTRIBUTION_REPORTING_REGISTER_SOURCE.create(value));
        else getHeaders().remove(HttpHeaderKey.ATTRIBUTION_REPORTING_REGISTER_SOURCE);
    }

    @ApiStatus.Experimental
    default @Nullable JsonObject getAttributionReportingRegisterTrigger() {
        return getHeaders().first(HttpHeaderKey.ATTRIBUTION_REPORTING_REGISTER_TRIGGER).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setAttributionReportingRegisterTrigger(@Nullable JsonObject value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ATTRIBUTION_REPORTING_REGISTER_TRIGGER.create(value));
        else getHeaders().remove(HttpHeaderKey.ATTRIBUTION_REPORTING_REGISTER_TRIGGER);
    }

    default @Nullable Credentials getAuthorization() {
        return getHeaders().first(HttpHeaderKey.AUTHORIZATION).map(HttpHeader::getValue).orElse(null);
    }

    default void setAuthorization(@Nullable Credentials value) {
        if (value != null) getHeaders().put(HttpHeaderKey.AUTHORIZATION.create(value));
        else getHeaders().remove(HttpHeaderKey.AUTHORIZATION);
    }

    default @Nullable Wildcard<@NotNull SiteData @NotNull []> getClearSiteData() {
        return getHeaders().first(HttpHeaderKey.CLEAR_SITE_DATA).map(HttpHeader::getValue).orElse(null);
    }

    default void setClearSiteData(@Nullable Wildcard<@NotNull SiteData @NotNull []> value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CLEAR_SITE_DATA.create(value));
        else getHeaders().remove(HttpHeaderKey.CLEAR_SITE_DATA);
    }

    @ApiStatus.Experimental
    default @Nullable String getContentDigest() {
        return getHeaders().first(HttpHeaderKey.CONTENT_DIGEST).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setContentDigest(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CONTENT_DIGEST.create(value));
        else getHeaders().remove(HttpHeaderKey.CONTENT_DIGEST);
    }

    default @Nullable ContentSecurityPolicy getContentSecurityPolicy() {
        return getHeaders().first(HttpHeaderKey.CONTENT_SECURITY_POLICY).map(HttpHeader::getValue).orElse(null);
    }

    default void setContentSecurityPolicy(@Nullable ContentSecurityPolicy value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CONTENT_SECURITY_POLICY.create(value));
        else getHeaders().remove(HttpHeaderKey.CONTENT_SECURITY_POLICY);
    }

    default @Nullable ContentSecurityPolicy getContentSecurityPolicyReportOnly() {
        return getHeaders().first(HttpHeaderKey.CONTENT_SECURITY_POLICY_REPORT_ONLY).map(HttpHeader::getValue).orElse(null);
    }

    default void setContentSecurityPolicyReportOnly(@Nullable ContentSecurityPolicy value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CONTENT_SECURITY_POLICY_REPORT_ONLY.create(value));
        else getHeaders().remove(HttpHeaderKey.CONTENT_SECURITY_POLICY_REPORT_ONLY);
    }

    default @NotNull HttpHeaderKey<?> @Nullable [] getCriticalClientHints() {
        return getHeaders().first(HttpHeaderKey.CRITICAL_CH).map(HttpHeader::getValue).orElse(null);
    }

    default void setCriticalClientHints(@NotNull HttpHeaderKey<?> @Nullable [] value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CRITICAL_CH.create(value));
        else getHeaders().remove(HttpHeaderKey.CRITICAL_CH);
    }

    default @Nullable EmbedderPolicy getCrossOriginEmbedderPolicy() {
        return getHeaders().first(HttpHeaderKey.CROSS_ORIGIN_EMBEDDER_POLICY).map(HttpHeader::getValue).orElse(null);
    }

    default void setCrossOriginEmbedderPolicy(@Nullable EmbedderPolicy value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CROSS_ORIGIN_EMBEDDER_POLICY.create(value));
        else getHeaders().remove(HttpHeaderKey.CROSS_ORIGIN_EMBEDDER_POLICY);
    }

    default @Nullable OpenerPolicy getCrossOriginOpenerPolicy() {
        return getHeaders().first(HttpHeaderKey.CROSS_ORIGIN_OPENER_POLICY).map(HttpHeader::getValue).orElse(null);
    }

    default void setCrossOriginOpenerPolicy(@Nullable OpenerPolicy value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CROSS_ORIGIN_OPENER_POLICY.create(value));
        else getHeaders().remove(HttpHeaderKey.CROSS_ORIGIN_OPENER_POLICY);
    }

    default @Nullable ResourcePolicy getCrossOriginResourcePolicy() {
        return getHeaders().first(HttpHeaderKey.CROSS_ORIGIN_RESOURCE_POLICY).map(HttpHeader::getValue).orElse(null);
    }

    default void setCrossOriginResourcePolicy(@Nullable ResourcePolicy value) {
        if (value != null) getHeaders().put(HttpHeaderKey.CROSS_ORIGIN_RESOURCE_POLICY.create(value));
        else getHeaders().remove(HttpHeaderKey.CROSS_ORIGIN_RESOURCE_POLICY);
    }

    default @Nullable KeepAlive getKeepAlive() {
        return getHeaders().first(HttpHeaderKey.KEEP_ALIVE).map(HttpHeader::getValue).orElse(null);
    }

    default void setKeepAlive(@Nullable KeepAlive value) {
        if (value != null) getHeaders().put(HttpHeaderKey.KEEP_ALIVE.create(value));
        else getHeaders().remove(HttpHeaderKey.KEEP_ALIVE);
    }

    @ApiStatus.Experimental
    default @Nullable String getLink() {
        return getHeaders().first(HttpHeaderKey.LINK).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setLink(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.LINK.create(value));
        else getHeaders().remove(HttpHeaderKey.LINK);
    }

    default @Nullable NetworkErrorLogging getNetworkErrorLogging() {
        return getHeaders().first(HttpHeaderKey.NEL).map(HttpHeader::getValue).orElse(null);
    }

    default void setNetworkErrorLogging(@Nullable NetworkErrorLogging value) {
        if (value != null) getHeaders().put(HttpHeaderKey.NEL.create(value));
        else getHeaders().remove(HttpHeaderKey.NEL);
    }

    @ApiStatus.Experimental
    default @Nullable String getNoVarySearch() {
        return getHeaders().first(HttpHeaderKey.NO_VARY_SEARCH).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setNoVarySearch(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.NO_VARY_SEARCH.create(value));
        else getHeaders().remove(HttpHeaderKey.NO_VARY_SEARCH);
    }

    @ApiStatus.Experimental
    default @Nullable String getObserveBrowsingTopics() {
        return getHeaders().first(HttpHeaderKey.OBSERVE_BROWSING_TOPICS).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setObserveBrowsingTopics(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.OBSERVE_BROWSING_TOPICS.create(value));
        else getHeaders().remove(HttpHeaderKey.OBSERVE_BROWSING_TOPICS);
    }

    @ApiStatus.Experimental
    default @Nullable Boolean getOriginAgentCluster() {
        return getHeaders().first(HttpHeaderKey.ORIGIN_AGENT_CLUSTER).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setOriginAgentCluster(@Nullable Boolean value) {
        if (value != null) getHeaders().put(HttpHeaderKey.ORIGIN_AGENT_CLUSTER.create(value));
        else getHeaders().remove(HttpHeaderKey.ORIGIN_AGENT_CLUSTER);
    }

    @ApiStatus.Experimental
    default @Nullable String getPermissionsPolicy() {
        return getHeaders().first(HttpHeaderKey.PERMISSIONS_POLICY).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setPermissionsPolicy(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.PERMISSIONS_POLICY.create(value));
        else getHeaders().remove(HttpHeaderKey.PERMISSIONS_POLICY);
    }

    default @Nullable String getProxyAuthenticate() {
        return getHeaders().first(HttpHeaderKey.PROXY_AUTHENTICATE).map(HttpHeader::getValue).orElse(null);
    }

    default void setProxyAuthenticate(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.PROXY_AUTHENTICATE.create(value));
        else getHeaders().remove(HttpHeaderKey.PROXY_AUTHENTICATE);
    }

    @ApiStatus.Experimental
    default @Nullable String getReferrerPolicy() {
        return getHeaders().first(HttpHeaderKey.REFERER_POLICY).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setReferrerPolicy(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.REFERER_POLICY.create(value));
        else getHeaders().remove(HttpHeaderKey.REFERER_POLICY);
    }

    @ApiStatus.Experimental
    default @Nullable String getReportingEndpoints() {
        return getHeaders().first(HttpHeaderKey.REPORTING_ENDPOINTS).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setReportingEndpoints(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.REPORTING_ENDPOINTS.create(value));
        else getHeaders().remove(HttpHeaderKey.REPORTING_ENDPOINTS);
    }

    @ApiStatus.Experimental
    default @Nullable String getReprDigest() {
        return getHeaders().first(HttpHeaderKey.REPR_DIGEST).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setReprDigest(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.REPR_DIGEST.create(value));
        else getHeaders().remove(HttpHeaderKey.REPR_DIGEST);
    }

    @ApiStatus.Experimental
    default @Nullable String getWebSocketAccept() {
        return getHeaders().first(HttpHeaderKey.SEC_WEBSOCKET_ACCEPT).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setWebSocketAccept(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.SEC_WEBSOCKET_ACCEPT.create(value));
        else getHeaders().remove(HttpHeaderKey.SEC_WEBSOCKET_ACCEPT);
    }

    @ApiStatus.Experimental
    default @Nullable String getServerTiming() {
        return getHeaders().first(HttpHeaderKey.SERVER_TIMING).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setServerTiming(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.SERVER_TIMING.create(value));
        else getHeaders().remove(HttpHeaderKey.SERVER_TIMING);
    }

    @ApiStatus.Experimental
    default @Nullable String getSetLogin() {
        return getHeaders().first(HttpHeaderKey.SET_LOGIN).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setSetLogin(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.SET_LOGIN.create(value));
        else getHeaders().remove(HttpHeaderKey.SET_LOGIN);
    }

    default @Nullable Location getSourceMap() {
        return getHeaders().first(HttpHeaderKey.SOURCEMAP).map(HttpHeader::getValue).orElse(null);
    }

    default void setSourceMap(@Nullable Location value) {
        if (value != null) getHeaders().put(HttpHeaderKey.SOURCEMAP.create(value));
        else getHeaders().remove(HttpHeaderKey.SOURCEMAP);
    }

    @ApiStatus.Experimental
    default @Nullable String getSpeculationRules() {
        return getHeaders().first(HttpHeaderKey.SPECULATION_RULES).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setSpeculationRules(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.SPECULATION_RULES.create(value));
        else getHeaders().remove(HttpHeaderKey.SPECULATION_RULES);
    }

    @ApiStatus.Experimental
    default @Nullable String getSupportsLoadingMode() {
        return getHeaders().first(HttpHeaderKey.SUPPORTS_LOADING_MODE).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setSupportsLoadingMode(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.SUPPORTS_LOADING_MODE.create(value));
        else getHeaders().remove(HttpHeaderKey.SUPPORTS_LOADING_MODE);
    }

    @ApiStatus.Experimental
    default @Nullable String getTimingAllowOrigin() {
        return getHeaders().first(HttpHeaderKey.TIMING_ALLOW_ORIGIN).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setTimingAllowOrigin(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.TIMING_ALLOW_ORIGIN.create(value));
        else getHeaders().remove(HttpHeaderKey.TIMING_ALLOW_ORIGIN);
    }

    @ApiStatus.Experimental
    default @Nullable String getWantContentDigest() {
        return getHeaders().first(HttpHeaderKey.WANT_CONTENT_DIGEST).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setWantContentDigest(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.WANT_CONTENT_DIGEST.create(value));
        else getHeaders().remove(HttpHeaderKey.WANT_CONTENT_DIGEST);
    }

    @ApiStatus.Experimental
    default @Nullable String getWantReprDigest() {
        return getHeaders().first(HttpHeaderKey.WANT_REPR_DIGEST).map(HttpHeader::getValue).orElse(null);
    }

    @ApiStatus.Experimental
    default void setWantReprDigest(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.WANT_REPR_DIGEST.create(value));
        else getHeaders().remove(HttpHeaderKey.WANT_REPR_DIGEST);
    }

    @ApiStatus.Experimental
    default @Nullable String getContentTypeOptions() {
        return getHeaders().first(HttpHeaderKey.X_CONTENT_TYPE_OPTIONS).map(HttpHeader::getValue).orElse(null);
    }
    @ApiStatus.Experimental
    default void setContentTypeOptions(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.X_CONTENT_TYPE_OPTIONS.create(value));
        else getHeaders().remove(HttpHeaderKey.X_CONTENT_TYPE_OPTIONS);
    }

    @ApiStatus.Experimental
    default @Nullable String getDNSPrefetchControl() {
        return getHeaders().first(HttpHeaderKey.X_DNS_PREFETCH_CONTROL).map(HttpHeader::getValue).orElse(null);
    }
    @ApiStatus.Experimental
    default void setDNSPrefetchControl(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.X_DNS_PREFETCH_CONTROL.create(value));
        else getHeaders().remove(HttpHeaderKey.X_DNS_PREFETCH_CONTROL);
    }

    @ApiStatus.Experimental
    default @Nullable String getXSSProtection() {
        return getHeaders().first(HttpHeaderKey.X_XSS_PROTECTION).map(HttpHeader::getValue).orElse(null);
    }
    @ApiStatus.Experimental
    default void setXSSProtection(@Nullable String value) {
        if (value != null) getHeaders().put(HttpHeaderKey.X_XSS_PROTECTION.create(value));
        else getHeaders().remove(HttpHeaderKey.X_XSS_PROTECTION);
    }

    // Classes

    /**
     * A future representing the response of an HTTP response.
     * This interface provides methods
     * to access various details of the HTTP response.
     *
     * @author Daniel Richard (Laivy)
     * @since 1.0-SNAPSHOT
     */
    interface Future extends java.util.concurrent.Future<@NotNull HttpResponse> {

        /**
         * Retrieves the HttpClient associated with this response future.
         *
         * @return The HttpClient associated with this response. Never null.
         */
        @NotNull HttpClient getClient();

        /**
         * Retrieves the version of this response future.
         *
         * @return The {@link HttpVersion} representing the version of this future. Never null.
         */
        @NotNull HttpVersion getVersion();

        /**
         * Retrieves the status of this HTTP response future.
         *
         * @return The {@link HttpStatus} representing the status of this future. Never null.
         */
        @NotNull HttpStatus getStatus();

        /**
         * Gets the headers from this HTTP response future.
         *
         * @return The headers of this HTTP response future. Never null.
         */
        @NotNull
        HttpHeaders getHeaders();

        /**
         * Returns the raw HTTP response as a string. The difference between this method and {@link #toString()}
         * is that {@link #toString()} represents the string representation of the Future itself, while this method
         * returns the actual HTTP response.
         * <p>
         * If the Future is not completed, this method will return a fragmented HTTP response.
         *
         * @return The raw HTTP response as a string. Never null.
         */
        @NotNull String getAsString();

        @Override
        @NotNull String toString();

        // Future

        /**
         * Attaches the given action to be invoked when this future completes.
         * The action is executed with the result of the HTTP response and any throwable
         * that was thrown during the response execution. The action will be run after
         * the HTTP response completes, either successfully or with an error.
         *
         * <p>The action is provided with two parameters:
         * <ul>
         *   <li>The {@link HttpResponse} object representing the completed HTTP response.</li>
         *   <li>A {@link Throwable} object representing any error that occurred, or {@code null} if the response completed successfully.</li>
         * </ul>
         *
         * <p>This method is useful for performing additional operations or cleanup
         * after the response is complete, regardless of the outcome.
         *
         * @param action the action to be executed when the future completes
         * @return a {@link Future} that represents the result of the action
         * @throws NullPointerException if the specified action is {@code null}
         */
        @NotNull Future whenComplete(@NotNull BiConsumer<? super HttpResponse, ? super Throwable> action);

        /**
         * Specifies a timeout for the HTTP response. If the response does not complete
         * within the given duration, the future will be completed exceptionally with
         * a {@link TimeoutException}.
         *
         * <p>The duration parameter defines the maximum time to wait for the response to complete.
         * If the duration elapses before the response completes, the future is automatically
         * canceled and a {@link TimeoutException} is thrown.
         *
         * <p>This method is useful for ensuring that the response does not the app froze indefinitely
         * and provides a way to handle long-running requests.
         *
         * @param duration the maximum time to wait for the response to complete
         * @return a {@link Future} that represents the result of the timeout operation
         * @throws NullPointerException if the specified duration is {@code null}
         */
        @NotNull Future orTimeout(@NotNull Duration duration);

    }


}