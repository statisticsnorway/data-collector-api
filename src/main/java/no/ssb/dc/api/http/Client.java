package no.ssb.dc.api.http;

import javax.net.ssl.SSLContext;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;

public interface Client {

    static Client newClient() {
        return newClientBuilder().build();
    }

    static Builder newClientBuilder() {
        return ServiceLoader.load(Client.Builder.class).findFirst().orElseThrow();
    }

    Response send(Request request);

    CompletableFuture<Response> sendAsync(Request request);

    Object getDelegate();

    enum Version {
        HTTP_1_1,
        HTTP_2
    }

    interface Builder {
        Builder version(Version version);

        Builder sslContext(SSLContext sslContext);

        Client build();
    }

}
