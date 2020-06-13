package no.ssb.dc.api.node.builder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import no.ssb.dc.api.node.Base;
import no.ssb.dc.api.node.Configurations;
import no.ssb.dc.api.node.Identity;
import no.ssb.dc.api.node.JwtTokenBodyPublisherProducer;

import java.util.Optional;

import static no.ssb.dc.api.node.builder.SpecificationBuilder.GLOBAL_CONFIGURATION;

@JsonDeserialize(using = NodeBuilderDeserializer.class)
public class JwtTokenBodyPublisherProducerBuilder extends IdentityTokenBuilder {

    @JsonProperty String identityId;
    @JsonProperty String bindTo;
    @JsonProperty String token;

    public JwtTokenBodyPublisherProducerBuilder() {
        super(BuilderType.JwtToken);
    }

    public JwtTokenBodyPublisherProducerBuilder identityId(String identityId) {
        this.identityId = identityId;
        return this;
    }

    public JwtTokenBodyPublisherProducerBuilder bindTo(String bindTo) {
        this.bindTo = bindTo;
        return this;
    }

    public JwtTokenBodyPublisherProducerBuilder token(String token) {
        this.token = token;
        return this;
    }

    @Override
    public <R extends Base> R build(BuildContext buildContext) {
        Configurations configurations = (Configurations) buildContext.nodeInstanceById().get(GLOBAL_CONFIGURATION);
        Optional<Identity> identity = Optional.ofNullable(configurations).stream().flatMap(conf -> conf.security().identities().stream().filter(i -> i.id().equals(identityId))).findFirst();
        return (R) new JwtTokenBodyPublisherProducerNode(identity.orElseThrow(), bindTo, token);
    }

    static class JwtTokenBodyPublisherProducerNode extends LeafNode implements JwtTokenBodyPublisherProducer {

        private final Identity identity;
        private final String bindTo;
        private final String token;

        public JwtTokenBodyPublisherProducerNode(Identity identity, String bindTo, String token) {
            this.identity = identity;
            this.bindTo = bindTo;
            this.token = token;
        }

        @Override
        public Identity identity() {
            return identity;
        }

        @Override
        public String bindTo() {
            return bindTo;
        }

        @Override
        public String token() {
            return token;
        }
    }
}
