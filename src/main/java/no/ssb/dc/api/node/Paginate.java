package no.ssb.dc.api.node;

import java.util.List;
import java.util.Set;

public interface Paginate extends Node {

    Set<String> variableNames();

    String variable(String name);

    boolean addPageContent();

    List<Execute> targets();

    double threshold();

    Condition condition();

}