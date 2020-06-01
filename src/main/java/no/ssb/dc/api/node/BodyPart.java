package no.ssb.dc.api.node;

import java.nio.charset.Charset;

public class BodyPart {

    public final String name;
    public final String filename;
    public final Object value;
    public final Charset charset;

    public BodyPart(String name, Object value, Charset charset) {
        this.name = name;
        this.filename = null;
        this.value = value;
        this.charset = charset;
    }

    public BodyPart(String name, String filename, Object value, Charset charset) {
        this.name = name;
        this.filename = filename;
        this.value = value;
        this.charset = charset;
    }

    public boolean isTextPart() {
        return filename == null;
    }

    public boolean isFormPart() {
        return filename != null;
    }

}
