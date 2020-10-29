package no.ssb.dc.api.node;

import no.ssb.dc.api.node.builder.RetryWhileStatus;

import java.util.List;
import java.util.Map;

public interface HttpStatusRetryWhile extends Validator {

    Map<RetryWhileStatus, List<ResponsePredicate>> is();

}
