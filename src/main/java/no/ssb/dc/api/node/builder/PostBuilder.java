package no.ssb.dc.api.node.builder;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.ssb.dc.api.http.Headers;
import no.ssb.dc.api.node.Base;
import no.ssb.dc.api.node.Configurations;
import no.ssb.dc.api.node.Node;
import no.ssb.dc.api.node.Post;
import no.ssb.dc.api.node.PostData;
import no.ssb.dc.api.node.Validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static no.ssb.dc.api.Builders.status;

public class PostBuilder extends OperationBuilder {

    @JsonProperty Headers requestHeaders = new Headers();
    @JsonProperty("content") PostData data = new PostData();
    @JsonProperty("responseValidators") List<LeafNodeBuilder> validators = new ArrayList<>();
    @JsonProperty("pipes") List<NodeBuilder> pipes = new ArrayList<>();
    @JsonProperty List<String> returnVariables = new ArrayList<>();

    PostBuilder() {
        super(BuilderType.Post);
    }

    public PostBuilder(String id) {
        super(BuilderType.Post);
        setId(id);
    }

    public PostBuilder id(String id) {
        setId(id);
        return this;
    }

    public PostBuilder url(String urlString) {
        this.url = urlString;
        return this;
    }

    public PostBuilder header(String name, String value) {
        requestHeaders.put(name, value);
        return this;
    }

    public PostBuilder data(String text) {
        return this;
    }

    public PostBuilder validate(LeafNodeBuilder validationBuilder) {
        validators.add(validationBuilder);
        return this;
    }

    public PostBuilder pipe(NodeBuilder builder) {
        pipes.add(builder);
        return this;
    }

    public PostBuilder returnVariables(String... variableKeys) {
        for (String variableKey : variableKeys) {
            returnVariables.add(variableKey);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    <R extends Base> R build(BuildContext buildContext) {
        List<Validator> validators = new ArrayList<>();

        // add default http status validator if unassigned
        if (this.validators.isEmpty()) {
            this.validate(status().success(200));
        }

        for (LeafNodeBuilder validatorBuilder : this.validators) {
            Validator validator = validatorBuilder.build(buildContext);
            validators.add(validator);
        }

        List<Node> stepNodeList = new ArrayList<>();
        for (NodeBuilder stepBuilder : pipes) {
            Node stepNode = stepBuilder.build(buildContext);
            stepNodeList.add(stepNode);
        }

        return (R) new PostBuilder.PostNode(getId(), buildContext.getInstance(SpecificationBuilder.GLOBAL_CONFIGURATION), url, requestHeaders, validators, stepNodeList, returnVariables);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PostBuilder that = (PostBuilder) o;
        return Objects.equals(requestHeaders, that.requestHeaders) &&
                Objects.equals(validators, that.validators) &&
                Objects.equals(pipes, that.pipes) &&
                Objects.equals(returnVariables, that.returnVariables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requestHeaders, validators, pipes, returnVariables);
    }

    @Override
    public String toString() {
        return "PostBuilder{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", requestHeaders=" + requestHeaders +
                ", validators=" + validators +
                ", pipes=" + pipes +
                ", returnVariables=" + returnVariables +
                '}';
    }

    static class PostNode extends OperationNode implements Post {

        final String url;
        final Headers headers;
        final List<Node> pipes;
        final List<String> returnVariables;
        private final List<Validator> validateResponse;

        PostNode(String id, Configurations configurations, String url, Headers headers, List<Validator> validateResponse, List<Node> pipes, List<String> returnVariables) {
            super(configurations, id);
            this.url = url;
            this.headers = headers;
            this.validateResponse = validateResponse;
            this.pipes = pipes;
            this.returnVariables = returnVariables;
        }

        @Override
        public String url() {
            return url;
        }

        @Override
        public Headers headers() {
            return headers;
        }

        @Override
        public void data(String text) {

        }

        @Override
        public void data(byte[] bytes) {

        }

        @Override
        public List<Validator> responseValidators() {
            return validateResponse;
        }

        @Override
        public List<? extends Node> steps() {
            return pipes;
        }

        @Override
        public List<String> returnVariables() {
            return returnVariables;
        }

        @Override
        public Iterator<? extends Node> iterator() {
            return pipes.iterator();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PostBuilder.PostNode postNode = (PostBuilder.PostNode) o;
            return Objects.equals(url, postNode.url) &&
                    Objects.equals(headers, postNode.headers) &&
                    Objects.equals(validateResponse, postNode.validateResponse) &&
                    Objects.equals(pipes, postNode.pipes) &&
                    Objects.equals(returnVariables, postNode.returnVariables);
        }

        @Override
        public int hashCode() {
            return Objects.hash(url, headers, validateResponse, pipes, returnVariables);
        }

        @Override
        public String toString() {
            return "PostNode{" +
                    "id='" + id + '\'' +
                    ", url='" + url + '\'' +
                    ", headers=" + headers +
                    ", validateResponse=" + validateResponse +
                    ", steps=" + pipes +
                    ", returnVariables=" + returnVariables +
                    '}';
        }


    }
}
