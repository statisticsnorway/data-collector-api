package no.ssb.dc.api.node.builder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// https://github.com/statisticsnorway/json-stat.java/blob/master/src/main/java/no/ssb/jsonstat/v2/deser/DimensionDeserializer.java
public class GenericNodeBuilderDeserializer extends StdDeserializer<GenericNodeBuilderDeserializer.Node.Builder> {

    protected GenericNodeBuilderDeserializer() {
        super(AbstractBuilder.class);
    }

    static String indent(int depth) {
        return Arrays.stream(new String[depth]).map(element -> " ").collect(Collectors.joining());
    }

    StringBuilder builder(int depth, StringBuilder builder) {
        return builder.append(indent(depth)).append(String.format("(%s)", depth));
    }

    @Override
    public Node.Builder deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        Node.Builder elementBuilder = new Node.Builder();

        StringBuilder builder = new StringBuilder();

        handleNode(0, parser, context, elementBuilder, builder);

        System.out.printf("builder:%n%s%n", builder.toString());

        return elementBuilder;
    }

    void handleNode(int depth, JsonParser parser, DeserializationContext context, Node.Builder elementBuilder, StringBuilder builder) throws IOException {

        JsonToken currentToken = parser.currentToken();
        // check CurrentToken -> START_OBJECT
        Node.Builder currentElementBuilder;

        // new named property
        if (currentToken == JsonToken.START_OBJECT && parser.currentName() != null) {
            currentElementBuilder = new Node.Builder();
            currentElementBuilder.name(parser.currentName());
//            elementBuilder.addChild(currentElementBuilder);
            // already have a named start-object (root is always unnamed)
        } else {
            currentElementBuilder = elementBuilder;
        }

        // check NextToken -> START_OBJECT
        // check NextToken -> FIELD_NAME

        JsonToken fieldToken = parser.nextToken();
        if (fieldToken == JsonToken.START_OBJECT) {
            // do nothing

        } else if (fieldToken == JsonToken.FIELD_NAME) {
            JsonToken nextValue;
            while (PropertyType.isValueToken((nextValue = parser.nextValue()))) {
                Property.Builder propertyBuilder = new Property.Builder();
                propertyBuilder.name(parser.currentName());
                propertyBuilder.value(parser.getCurrentValue(), PropertyType.of(nextValue));
//                currentElementBuilder.addProperty(propertyBuilder);
            }

            if (nextValue == JsonToken.START_ARRAY) {
                Array.Builder arrayBuilder = new Array.Builder();
//                elementBuilder.addChild(arrayBuilder);
            }

        } else if (fieldToken == JsonToken.END_OBJECT || fieldToken == JsonToken.END_ARRAY) {


        } else {
            if (fieldToken != null) {
                throw new IllegalStateException();
            }
        }

    }

    void handleNode_(int depth, JsonParser parser, DeserializationContext context, Node.Builder elementBuilder, StringBuilder builder) throws IOException {

        JsonToken currentToken = parser.currentToken();

        if (currentToken != null) {
            builder(depth, builder).append(String.format("BEGIN %s %s ", currentToken.name(), currentToken.asString())).append(parser.currentName()).append(" ").append(parser.getValueAsString()).append("\n");
        }

        JsonToken fieldToken = parser.nextToken();
        if (fieldToken == JsonToken.START_OBJECT) {
            builder(depth, builder).append(String.format("--> START_OBJECT: %s %s ", fieldToken.name(), fieldToken.asString())).append(" ").append(parser.currentName()).append(" ").append(parser.getValueAsString()).append("\n");

        } else if (fieldToken == JsonToken.FIELD_NAME) {

            builder(depth + 1, builder).append(String.format("%s %s ", fieldToken.name(), fieldToken.asString()))
                    .append(" => ").append(parser.currentName()).append(": ").append(parser.getValueAsString())
                    .append("\n");

            JsonToken nextValue;
            int n = 0;
            while ((nextValue = parser.nextValue()) == JsonToken.VALUE_STRING) {
                Property.Builder propertyBuilder = new Property.Builder();
                propertyBuilder.name(parser.currentName());
                propertyBuilder.value(parser.getValueAsString(), PropertyType.of(nextValue));

                builder(depth, builder).append(" fieldValue: ").append(nextValue.name()).append(" -> ").append(parser.currentName()).append(": ").append(parser.getValueAsString()).append("\n");
                n++;
            }
            builder.append(" <--- ").append(n).append("\n");

            if (nextValue == JsonToken.START_ARRAY) {
                builder(depth, builder).append("START array: ").append(nextValue.name()).append(" ").append(nextValue.asString()).append(" ").append(currentToken.asString()).append(" ").append(parser.currentName()).append(" ").append(parser.getValueAsString()).append(" ");
                JsonToken nextToken = parser.nextToken();
                builder.append(nextToken).append(" ").append(nextToken.asString()).append(" ").append(parser.getValueAsString()).append("\n");
                handleNode(depth + 1, parser, context, elementBuilder, builder);
            }

            builder(depth, builder).append("END fieldValue: ").append(nextValue.name()).append(" -> ").append(parser.currentName()).append(": ").append(parser.getValueAsString()).append("\n");

            handleNode(depth + 1, parser, context, elementBuilder, builder);
        } else {
            //System.out.printf("-------------> %s%n", fieldToken);
        }

        if (fieldToken != null) {
            builder(depth, builder).append("end ").append(fieldToken.name()).append(" ").append(fieldToken.asString()).append("\n");
            handleNode(depth, parser, context, elementBuilder, builder);
        }
    }

    /*
        Object, Array, Values
     */

    enum PropertyType {
        STRING(JsonToken.VALUE_STRING),
        INTEGER(JsonToken.VALUE_NUMBER_INT),
        FLOAT(JsonToken.VALUE_NUMBER_FLOAT),
        BOOLEAN(JsonToken.VALUE_TRUE, JsonToken.VALUE_FALSE);

        private final List<JsonToken> jsonTokenList;

        PropertyType(JsonToken... jsonToken) {
            jsonTokenList = List.of(jsonToken);
        }

        static PropertyType of(JsonToken jsonToken) {
            for (PropertyType type : values()) {
                if (type.jsonTokenList.contains(jsonToken)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("PropertyType doesn't support JsonToken: " + jsonToken);
        }

        static boolean isValueToken(JsonToken jsonToken) {
            for (PropertyType type : values()) {
                if (type.jsonTokenList.contains(jsonToken)) {
                    return true;
                }
            }
            return false;
        }

        boolean isString() {
            return this == STRING;
        }

        boolean isInteger() {
            return this == INTEGER;
        }

        boolean isFloat() {
            return this == FLOAT;
        }

        boolean isBoolean() {
            return this == BOOLEAN;
        }
    }

    static abstract class Base {
        static abstract class Builder {
        }
    }

    static class Array extends Base {

        static class Builder extends Base.Builder {
            private final List<Node> nodeList = new ArrayList<>();

            public Builder() {
            }

            Builder node(Node node) {
                nodeList.add(node);
                return this;
            }

            Array build() {
                return new Array();
            }
        }
    }

    static class Node {
        public final String name;
        public final Map<String, ? extends Base> children;

        public Node(String name, Map<String, ? extends Base> children) {
            this.name = name;
            this.children = children;
        }

        @Override
        public String toString() {
            return "Element{" +
                    "name='" + name + '\'' +
                    ", children=" + children +
                    '}';
        }

        static class Builder {
            private String name;
            private final Map<String, Base.Builder> children = new LinkedHashMap<>();

            public Builder() {
            }

            Builder name(String name) {
                this.name = name;
                return this;
            }

            Builder addChild(Base.Builder childBuilder) {
                children.put("", childBuilder);
                return this;
            }

            Node build() {
//                List<Node> nodeList = children.stream().map(Builder::build).collect(Collectors.toList());
//                List<Property> propertyList = properties.values().stream().map(Property.Builder::build).collect(Collectors.toList());
//                return new Node(name, nodeList, propertyList);
                return null;
            }
        }
    }

    public static class Property extends Base {
        public final String name;
        public final Object value;
        public final PropertyType type;

        @Override
        public String toString() {
            return "Property{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    ", type=" + type +
                    '}';
        }

        public Property(String name, Object value, PropertyType type) {
            this.name = name;
            this.value = value;
            this.type = type;
        }

        String asString() {
            return (String) value;
        }

        Integer asInteger() {
            return (Integer) value;
        }

        Float asFloat() {
            return (Float) value;
        }

        Boolean asBoolean() {
            return (Boolean) value;
        }

        static class Builder extends Base.Builder {
            private String name;
            private Object value;
            private PropertyType type;

            public Builder() {
            }

            Builder name(String name) {
                this.name = name;
                return this;
            }

            Builder value(Object value, PropertyType type) {
                this.value = value;
                this.type = type;
                return this;
            }

            Property build() {
                return new Property(name, value, type);
            }
        }
    }
}


