package no.ssb.dc.api.node.builder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import no.ssb.dc.api.node.Base;
import no.ssb.dc.api.node.JwtIdentity;

@JsonDeserialize(using = NodeBuilderDeserializer.class)
public class JwtBuilder extends IdentityBuilder {

    @JsonProperty("headerClaims") JwtHeaderClaims headerClaims;
    @JsonProperty("claims") JwtClaims claims;

    public JwtBuilder(String id, JwtHeaderClaims headerClaims, JwtClaims claims) {
        super(BuilderType.JwtIdentity, id);
        this.headerClaims = headerClaims;
        this.claims = claims;
    }

    @Override
    public <R extends Base> R build(BuildContext buildContext) {
        return (R) new JwtIdentityNode(id, headerClaims, claims);
    }

    static class JwtIdentityNode extends LeafNode implements JwtIdentity {

        private final String id;
        private final JwtHeaderClaims headerClaims;
        private final JwtClaims claims;

        public JwtIdentityNode(String id, JwtHeaderClaims headerClaims, JwtClaims claims) {
            this.id = id;
            this.headerClaims = headerClaims;
            this.claims = claims;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public JwtHeaderClaims headerClaims() {
            return headerClaims;
        }

        @Override
        public JwtClaims claims() {
            return claims;
        }
    }
}
