package no.ssb.dc.api.node.builder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import no.ssb.dc.api.node.Base;
import no.ssb.dc.api.node.WhenVariableIsNull;

import java.util.Map;
import java.util.Objects;

@JsonDeserialize(using = NodeBuilderDeserializer.class)
public class WhenVariableIsNullBuilder extends ConditionBuilder {

    @JsonProperty String identifier;

    public WhenVariableIsNullBuilder() {
        super(BuilderType.ConditionWhenVariableIsNull);
    }

    public WhenVariableIsNullBuilder identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    <R extends Base> R build(Map<String, NodeBuilder> nodeBuilderById, Map<String, R> nodeInstanceById) {
        return (R) new WhenVariableIsNullNode(identifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WhenVariableIsNullBuilder)) return false;
        WhenVariableIsNullBuilder that = (WhenVariableIsNullBuilder) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "WhenVariableIsNullBuilder{" +
                "identifier='" + identifier + '\'' +
                '}';
    }


    static class WhenVariableIsNullNode extends ConditionNode implements WhenVariableIsNull {

        final String identifier;

        WhenVariableIsNullNode(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String identifier() {
            return identifier;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WhenVariableIsNullNode that = (WhenVariableIsNullNode) o;
            return Objects.equals(identifier, that.identifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifier);
        }

        @Override
        public String toString() {
            return "WhenVariableIsNullNode{" +
                    "identifier='" + identifier + '\'' +
                    '}';
        }
    }
}
