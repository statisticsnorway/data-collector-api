package no.ssb.dc.api.node.builder;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

public class JwtClaims {

    @JsonProperty String issuer;

    @JsonProperty String audience;

    @JsonProperty int timeToLiveInSeconds;

    @JsonProperty Map<String, String> claims = new LinkedHashMap<>();

    public JwtClaims issuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public JwtClaims audience(String audience) {
        this.audience = audience;
        return this;
    }

    public JwtClaims timeToLiveInSeconds(int timeToLiveInSeconds) {
        this.timeToLiveInSeconds = timeToLiveInSeconds;
        return this;
    }

    public JwtClaims claim(String key, String value) {
        this.claims.put(key, value);
        return this;
    }

    public String issuer() {
        return issuer;
    }

    public String audience() {
        return audience;
    }

    public int timeToLiveInSeconds() {
        return timeToLiveInSeconds;
    }

    public Map<String, String> getClaims() {
        return claims;
    }
}
