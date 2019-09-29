package no.ssb.dc.api;

import no.ssb.dc.api.node.BaseNode;
import no.ssb.dc.api.node.Query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CompositionHandler {
    Class<? extends BaseNode> forClass();

    Class<? extends Query> selectorClass();

}
