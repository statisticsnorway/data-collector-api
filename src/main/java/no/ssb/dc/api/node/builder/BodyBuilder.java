package no.ssb.dc.api.node.builder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import no.ssb.dc.api.node.Base;
import no.ssb.dc.api.node.Body;

@JsonDeserialize(using = NodeBuilderDeserializer.class)
public class BodyBuilder extends QueryBuilder {

    public BodyBuilder() {
        super(BuilderType.QueryBody);
    }

    @Override
    <R extends Base> R build(BuildContext buildContext) {
        return (R) new BodyNode();
    }

    class BodyNode extends QueryNode implements Body {

        public BodyNode() {
        }

        // NOP expression - the body query returns unaltered response body
        @Override
        public String expression() {
            return Void.TYPE.getName();
        }
    }

}
