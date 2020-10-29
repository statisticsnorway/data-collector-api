package no.ssb.dc.api.node.builder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import no.ssb.dc.api.http.HttpStatus;
import no.ssb.dc.api.node.Base;
import no.ssb.dc.api.node.HttpStatusRetryWhile;
import no.ssb.dc.api.node.ResponsePredicate;
import no.ssb.dc.api.util.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@JsonDeserialize(using = NodeBuilderDeserializer.class)
public class HttpStatusRetryWhileBuilder extends LeafNodeBuilder {

    @JsonProperty("is") @JsonSerialize(using = IsMapSerializer.class)
    Map<RetryWhileStatus, List<ResponsePredicateBuilder>> isMap = new LinkedHashMap<>();

    public HttpStatusRetryWhileBuilder() {
        super(BuilderType.HttpStatusRetryWhile);
    }

    public HttpStatusRetryWhileBuilder is(Integer statusCode, TimeUnit duration, Integer amount) {
        isMap.put(RetryWhileStatus.valueOf(statusCode, duration, amount), new ArrayList<>());
        return this;
    }

    public HttpStatusRetryWhileBuilder is(HttpStatus statusCode, TimeUnit duration, Integer amount) {
        isMap.put(RetryWhileStatus.valueOf(statusCode, duration, amount), new ArrayList<>());
        return this;
    }

    public HttpStatusRetryWhileBuilder is(Integer statusCode, TimeUnit duration, Integer amount, ResponsePredicateBuilder responsePredicateBuilder) {
        isMap.computeIfAbsent(RetryWhileStatus.valueOf(statusCode, duration, amount), list -> new ArrayList<>()).add(responsePredicateBuilder);
        return this;
    }

    @Override
    public <R extends Base> R build(BuildContext buildContext) {
        Map<RetryWhileStatus, List<ResponsePredicate>> isMap = new LinkedHashMap<>();
        for (Map.Entry<RetryWhileStatus, List<ResponsePredicateBuilder>> entry : this.isMap.entrySet()) {
            List<ResponsePredicate> responsePredicateList = entry.getValue().stream()
                    .map(builder -> (ResponsePredicate) builder.build(buildContext)).collect(Collectors.toList());
            isMap.computeIfAbsent(entry.getKey(), list -> new ArrayList<>()).addAll(responsePredicateList);
        }
        return (R) new HttpStatusRetryWhileNode(isMap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HttpStatusRetryWhileBuilder that = (HttpStatusRetryWhileBuilder) o;
        return Objects.equals(isMap, that.isMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isMap);
    }

    static class HttpStatusRetryWhileNode extends LeafNode implements HttpStatusRetryWhile {

        final Map<RetryWhileStatus, List<ResponsePredicate>> is;

        HttpStatusRetryWhileNode(Map<RetryWhileStatus, List<ResponsePredicate>> is) {
            this.is = is;
        }

        @Override
        public Map<RetryWhileStatus, List<ResponsePredicate>> is() {
            return is;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HttpStatusRetryWhileNode that = (HttpStatusRetryWhileNode) o;
            return Objects.equals(is, that.is);
        }

        @Override
        public int hashCode() {
            return Objects.hash(is);
        }

        @Override
        public String toString() {
            return "HttpStatusRetryWhileNode{" +
                    "is=" + is +
                    '}';
        }
    }

    /*
        "retryWhile": [
            {
                "type": "HttpStatusRetryWhile",
                "is": {
                    "status": "HTTP_NOT_FOUND",
                    "duration": "SECONDS",
                    "amount": "15",
                    "bodyContains": [
                        {
                            "type": "HttpResponseBodyContains",
                            "queryBuilder": {
                                "type": "QueryRegEx",
                                "expression": "^(Batch med id=\\d+ er enda ikke klar)$",
                                "query": {
                                    "type": "QueryJqPath",
                                    "expression": ".feilmelding"
                                }
                            },
                            "equalToStringLiteral": "foo"
                        }
                    ]
                }
            }
        ]
     */

    static class IsMapSerializer extends JsonSerializer<Map<RetryWhileStatus, List<ResponsePredicateBuilder>>> {

        @Override
        public void serialize(Map<RetryWhileStatus, List<ResponsePredicateBuilder>> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            for (Map.Entry<RetryWhileStatus, List<ResponsePredicateBuilder>> entry : value.entrySet()) {
                gen.writeStartObject();
                gen.writeStringField("status", entry.getKey().statusCode.name());
                gen.writeStringField("duration", entry.getKey().duration.name());
                gen.writeStringField("amount", entry.getKey().amount.toString());
                gen.writeArrayFieldStart("bodyContains");
                for (List<ResponsePredicateBuilder> item : value.values()) {
                    for (ResponsePredicateBuilder i : item) {
                        gen.writeRaw(JsonParser.createJsonParser().toPrettyJSON(i));
                    }
                }
                gen.writeEndArray();
                gen.writeEndObject();
            }

        }
    }
}
