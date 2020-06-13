package no.ssb.dc.api.node.builder;

abstract public class IdentityTokenBuilder extends BodyPublisherProducerBuilder {

    IdentityTokenBuilder(BuilderType type) {
        super(type);
    }


    static abstract class IdentityTokenNode extends LeafNode {

    }
}
