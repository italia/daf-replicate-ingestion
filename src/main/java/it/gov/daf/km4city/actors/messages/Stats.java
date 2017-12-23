package it.gov.daf.km4city.actors.messages;

public class Stats {

    private int ok;
    private int ko;
    private String uri;

    public Stats(String uri, int ok, int ko) {
        this.ok = ok;
        this.ko = ko;
        this.uri = uri;
    }

    public int getOk() {
        return ok;
    }

    public int getKo() {
        return ko;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "ok=" + ok +
                ", ko=" + ko +
                ", uri='" + uri + '\'' +
                '}';
    }
}
