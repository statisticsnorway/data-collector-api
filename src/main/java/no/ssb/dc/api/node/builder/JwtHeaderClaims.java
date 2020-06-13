package no.ssb.dc.api.node.builder;

import com.fasterxml.jackson.annotation.JsonProperty;


// TODO make as builder

public class JwtHeaderClaims {

    @JsonProperty String alg;

    @JsonProperty("x5c") String sslBundleName;

    public JwtHeaderClaims alg(String alg) {
        this.alg = alg;
        return this;
    }

    public JwtHeaderClaims x509CertChain(String sslBundleName) {
        this.sslBundleName = sslBundleName;
        return this;
    }

    public String alg() {
        return alg;
    }

    public String sslBundleName() {
        return sslBundleName;
    }
}
