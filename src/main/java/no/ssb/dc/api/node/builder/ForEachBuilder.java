package no.ssb.dc.api.node.builder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import no.ssb.dc.api.node.Base;
import no.ssb.dc.api.node.Configurations;
import no.ssb.dc.api.node.ForEach;
import no.ssb.dc.api.node.Node;
import no.ssb.dc.api.node.Query;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = NodeBuilderDeserializer.class)
public class ForEachBuilder extends NodeBuilder {

    @JsonProperty("splitQuery") QueryBuilder splitBuilder;
    @JsonProperty("pipes") List<NodeBuilder> pipes = new ArrayList<>();

    public ForEachBuilder(QueryBuilder splitBuilder) {
        super(BuilderType.ForEach);
        this.splitBuilder = splitBuilder;
    }

    public ForEachBuilder pipe(NodeBuilder builder) {
        pipes.add(builder);
        return this;
    }

    @Override
    <R extends Base> R build(BuildContext buildContext) {
        QueryBuilder.QueryNode splitToListQueryNode = splitBuilder.build(buildContext);

        List<Node> stepNodeList = new ArrayList<>();
        for (NodeBuilder stepBuilder : pipes) {
            Node stepNode = stepBuilder.build(buildContext);
            stepNodeList.add(stepNode);
        }

        return (R) new ForEachNode(buildContext.getInstance(SpecificationBuilder.GLOBAL_CONFIGURATION), splitToListQueryNode, stepNodeList);
    }

    static class ForEachNode extends FlowNode implements ForEach {

        final QueryBuilder.QueryNode splitNode;
        final List<Node> stepNodeList;

        public ForEachNode(Configurations configurations, QueryBuilder.QueryNode splitNode, List<Node> stepNodeList) {
            super(configurations);
            this.splitNode = splitNode;
            this.stepNodeList = stepNodeList;
        }

        @Override
        public Query splitToListQuery() {
            return splitNode;
        }

        @Override
        public List<? extends Node> steps() {
            return stepNodeList;
        }

        @Override
        public java.util.Iterator<? extends Node> iterator() {
            return createNodeList().iterator();
        }
    }
}
