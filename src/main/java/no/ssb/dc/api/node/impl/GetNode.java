package no.ssb.dc.api.node.impl;

import no.ssb.dc.api.PositionProducer;
import no.ssb.dc.api.node.Get;
import no.ssb.dc.api.node.Node;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GetNode extends OperationNode implements Get {

    final String url;
    final List<Node> steps;
    final Class<? extends PositionProducer> positionProducerClass;
    final List<String> returnVariables;

    public GetNode(String id, String url, List<Node> steps, Class<? extends PositionProducer> positionProducerClass, List<String> returnVariables) {
        super(id);
        this.url = url;
        this.steps = steps;
        this.positionProducerClass = positionProducerClass;
        this.returnVariables = returnVariables;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public List<? extends Node> steps() {
        return steps;
    }

    @Override
    public Class<? extends PositionProducer> positionProducerClass() {
        return positionProducerClass;
    }

    @Override
    public List<String> returnVariables() {
        return returnVariables;
    }

    @Override
    public Iterator<? extends Node> iterator() {
        return steps.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetNode getNode = (GetNode) o;
        return url.equals(getNode.url) &&
                Objects.equals(steps, getNode.steps) &&
                Objects.equals(positionProducerClass, getNode.positionProducerClass) &&
                Objects.equals(returnVariables, getNode.returnVariables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, steps, positionProducerClass, returnVariables);
    }

    @Override
    public String toString() {
        return "GetNode{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", steps=" + steps +
                ", positionProducerClass=" + positionProducerClass +
                ", returnVariables=" + returnVariables +
                '}';
    }
}
