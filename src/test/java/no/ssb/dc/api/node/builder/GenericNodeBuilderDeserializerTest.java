package no.ssb.dc.api.node.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import no.ssb.dc.api.util.CommonUtils;
import no.ssb.dc.api.util.JsonParser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

class GenericNodeBuilderDeserializerTest {

    private static final Logger LOG = LoggerFactory.getLogger(GenericNodeBuilderDeserializer.class);

    static <R extends GenericNodeBuilderDeserializer.Node.Builder> R deserialize(String source, Class<R> builderClass) {
        try {
            ObjectMapper mapper = JsonParser.createYamlParser().mapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(GenericNodeBuilderDeserializer.Node.Builder.class, new GenericNodeBuilderDeserializer());
            mapper.registerModule(module);
            return mapper.readValue(source, builderClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Disabled
    @Test
    void testGenericNodeBuilderDeserializer() {
        Path specPath = CommonUtils.currentPath().getParent().resolve("data-collection-consumer-specifications").resolve("specs");
        Path serializedSpec = specPath.resolve("toll-tvinn-test-spec.json");
        String jsonSpec = CommonUtils.readFileOrClasspathResource(serializedSpec.toString());

        GenericNodeBuilderDeserializer.Node.Builder builder = deserialize(jsonSpec, GenericNodeBuilderDeserializer.Node.Builder.class);
        traverse(builder);
        LOG.trace("Builder: {}", builder);
        LOG.trace("Spec-path: {}\n{}", serializedSpec.toAbsolutePath(), jsonSpec);
    }

    private void traverse(GenericNodeBuilderDeserializer.Node.Builder builder) {
        GenericNodeBuilderDeserializer.Node rootNode = builder.build();
        LOG.trace("{}", rootNode.name);
    }

    @Disabled
    @Test
    void testModel() {
        GenericNodeBuilderDeserializer.Node.Builder rootBuilder = new GenericNodeBuilderDeserializer.Node.Builder();
        rootBuilder.name("root");

//        {
//            GenericNodeBuilderDeserializer.Node.Builder childBuilder1 = new GenericNodeBuilderDeserializer.Node.Builder();
//            childBuilder1.name("child1");
//            childBuilder1.addProperty(new GenericNodeBuilderDeserializer.Property.Builder().name("prop1").value("value1", GenericNodeBuilderDeserializer.PropertyType.STRING));
//            rootBuilder.addChild(childBuilder1);

//            {
//                GenericNodeBuilderDeserializer.Node.Builder childBuilder1_1 = new GenericNodeBuilderDeserializer.Node.Builder();
//                childBuilder1_1.name("child1_1");
//                childBuilder1_1.addProperty(new GenericNodeBuilderDeserializer.Property.Builder().name("prop1_1").value("value1_1", GenericNodeBuilderDeserializer.PropertyType.STRING));
//                childBuilder1.addChild(childBuilder1_1);
//            }
//        }

//        {
//            GenericNodeBuilderDeserializer.Node.Builder childBuilder2 = new GenericNodeBuilderDeserializer.Node.Builder();
//            childBuilder2.name("child2");
//            childBuilder2.addProperty(new GenericNodeBuilderDeserializer.Property.Builder().name("prop2").value("value2", GenericNodeBuilderDeserializer.PropertyType.STRING));
//            rootBuilder.addChild(childBuilder2);
//        }

//        StringBuilder stringBuilder = new StringBuilder();
//        GenericNodeBuilderDeserializer.Node root = rootBuilder.build();
//        traverse(0, root, stringBuilder);
//        LOG.trace("\n{}", stringBuilder.toString());
    }

//    private void traverse(int depth, GenericNodeBuilderDeserializer.Node currentNode, StringBuilder stringBuilder) {
//        String properties = currentNode.properties.stream().map(property -> property.value.toString()).collect(Collectors.joining(","));
//        stringBuilder.append(String.format("%s(%s)%s%n%s", GenericNodeBuilderDeserializer.indent(depth), depth, currentNode.name,
//                properties.isEmpty() ? "" : String.format("\t%s%s%n", GenericNodeBuilderDeserializer.indent(depth), properties)));
//
//        for (GenericNodeBuilderDeserializer.Node child : currentNode.children) {
//            traverse(depth + 1, child, stringBuilder);
//        }
//    }

}
