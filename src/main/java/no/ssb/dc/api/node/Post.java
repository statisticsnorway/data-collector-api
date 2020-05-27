package no.ssb.dc.api.node;

public interface Post extends Operation {

    void data(String text); // support el-vars

    void data(byte[] payload);

}
