package no.ssb.dc.api.node.builder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import no.ssb.dc.api.node.Base;
import no.ssb.dc.api.node.Execute;
import no.ssb.dc.api.node.Node;
import no.ssb.dc.api.node.Operation;
import no.ssb.dc.api.node.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@JsonDeserialize(using = NodeBuilderDeserializer.class)
public class ExecuteBuilder extends NodeBuilder {

    @JsonProperty String executeId;
    @JsonProperty List<String> requiredInputs = new ArrayList<>();
    @JsonProperty Map<String, QueryBuilder> inputVariables = new LinkedHashMap<>();

    public ExecuteBuilder(String executeId) {
        super(BuilderType.Execute);
        this.executeId = executeId;
    }

    public ExecuteBuilder requiredInput(String identifier) {
        requiredInputs.add(identifier);
        return this;
    }

    public ExecuteBuilder inputVariable(String identifier, QueryBuilder queryBuilder) {
        inputVariables.put(identifier, queryBuilder);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    <R extends Base> R build(Map<String, NodeBuilder> nodeBuilderById, Map<String, R> nodeInstanceById) {
        Map<String, QueryBuilder.QueryNode> inputVariableMap = inputVariables.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (QueryBuilder.QueryNode) e.getValue().build(nodeBuilderById, nodeInstanceById)));

        if (!nodeBuilderById.containsKey(executeId)) {
            throw new RuntimeException("Builder" + this.getClass() + " points to an undefined node: " + this.executeId);
        }

        OperationBuilder.OperationNode targetExecuteNode = (OperationBuilder.OperationNode) (nodeInstanceById.containsKey(executeId) ?
                nodeInstanceById.get(executeId) :
                nodeBuilderById.get(executeId).build(nodeBuilderById, nodeInstanceById));

        nodeInstanceById.computeIfAbsent(executeId, node -> (R) targetExecuteNode);

        return (R) new ExecuteNode(executeId, requiredInputs, inputVariableMap, targetExecuteNode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecuteBuilder)) return false;
        ExecuteBuilder that = (ExecuteBuilder) o;
        return Objects.equals(executeId, that.executeId) &&
                Objects.equals(requiredInputs, that.requiredInputs) &&
                Objects.equals(inputVariables, that.inputVariables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executeId, requiredInputs, inputVariables);
    }

    @Override
    public String toString() {
        return "ExecuteBuilder{" +
                "executeId='" + executeId + '\'' +
                ", requiredInputs=" + requiredInputs +
                ", inputVariables=" + inputVariables +
                '}';
    }

    static class ExecuteNode extends FlowNode implements Execute {

        final String executeId;
        final List<String> requiredInputs;
        final Map<String, QueryBuilder.QueryNode> inputVariables;
        final OperationBuilder.OperationNode targetNode;

        ExecuteNode(String executeId, List<String> requiredInputs, Map<String, QueryBuilder.QueryNode> inputVariables, OperationBuilder.OperationNode targetNode) {
            if (targetNode == null) {
                throw new IllegalArgumentException("adjacent executeNode is null");
            }
            this.executeId = executeId;
            this.requiredInputs = requiredInputs;
            this.inputVariables = inputVariables;
            this.targetNode = targetNode;
        }

        @Override
        public String executeId() {
            return executeId;
        }

        @Override
        public List<String> requiredInputs() {
            return requiredInputs;
        }

        @Override
        public Map<String, Query> inputVariable() {
            return inputVariables.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> (Query) y, LinkedHashMap::new));
        }

        @Override
        public Operation target() {
            return targetNode;
        }

        @Override
        public Iterator<? extends Node> iterator() {
            List<Node> nodeList = createNodeList();
            nodeList.add(targetNode);
            return nodeList.iterator();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExecuteNode that = (ExecuteNode) o;
            return Objects.equals(executeId, that.executeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(executeId);
        }

        @Override
        public String toString() {
            return "ExecuteNode{" +
                    "executeId='" + executeId + '\'' +
                    ", requiredInputs=" + requiredInputs +
                    ", inputVariables=" + inputVariables +
                    ", executeNode=" + targetNode +
                    '}';
        }
    }
}
