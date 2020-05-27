package no.ssb.dc.api.node;

public class PostData {

    enum Encoding {
        TEXT_PLAIN("text/plain"),
        URL_ENCODED("application/x-www-form-urlencoded"),
        MULTI_PART("multipart/form-data");

        private final String mimeType;

        Encoding(String mimeType) {
            this.mimeType = mimeType;
        }
    }

}
