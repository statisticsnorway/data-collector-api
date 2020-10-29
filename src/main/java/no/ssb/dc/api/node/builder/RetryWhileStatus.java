package no.ssb.dc.api.node.builder;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.ssb.dc.api.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RetryWhileStatus {

    @JsonProperty("statusCode") public final HttpStatus statusCode;
    @JsonProperty("duration") public final TimeUnit duration;
    @JsonProperty("amount") public final Integer amount;

    public RetryWhileStatus(HttpStatus statusCode, TimeUnit duration, Integer amount) {
        this.statusCode = statusCode;
        this.duration = duration;
        this.amount = amount;
    }

    public static RetryWhileStatus valueOf(int statusCode) {
        return new Builder().status(statusCode).build();
    }

    public static RetryWhileStatus valueOf(int statusCode, TimeUnit duration, Integer amount) {
        return valueOf(HttpStatus.valueOf(statusCode), duration, amount);
    }

    public static RetryWhileStatus valueOf(HttpStatus statusCode, TimeUnit duration, Integer amount) {
        return new Builder()
                .status(statusCode)
                .duration(duration)
                .amount(amount)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RetryWhileStatus that = (RetryWhileStatus) o;
        return statusCode == that.statusCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode);
    }

    @Override
    public String toString() {
        return "RetryWhileStatus{" +
                "statusCode=" + statusCode +
                ", duration=" + duration +
                ", amount=" + amount +
                '}';
    }

    public static class Builder {
        final Map<String, Object> map = new LinkedHashMap<>();

        public Builder status(int statusCode) {
            map.put("httpStatus", HttpStatus.valueOf(statusCode));
            return this;
        }

        public Builder status(HttpStatus httpStatus) {
            map.put("httpStatus", httpStatus);
            return this;
        }

        public Builder duration(TimeUnit duration) {
            map.put("duration", duration);
            return this;
        }

        public Builder amount(Integer amount) {
            map.put("amount", amount);
            return this;
        }

        public RetryWhileStatus build() {
            return new RetryWhileStatus(
                    (HttpStatus) map.get("httpStatus"),
                    (TimeUnit) map.get("duration"),
                    (Integer) map.get("amount")
            );
        }

        static Builder of(RetryWhileStatus retryWhileStatus) {
            Builder builder = new Builder();
            Optional.of(retryWhileStatus.statusCode).map(builder::status).orElseThrow();
            Optional.ofNullable(retryWhileStatus.duration).map(builder::duration);
            Optional.ofNullable(retryWhileStatus.amount).map(builder::amount);
            return builder;
        }
    }
}
