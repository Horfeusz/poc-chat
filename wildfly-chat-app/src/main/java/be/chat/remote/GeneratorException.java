package be.chat.remote;

public class GeneratorException extends Exception {

    private String reason;

    public GeneratorException(String s) {
        super(s);
        this.reason = s;
    }

    public String toString() {
        return this.reason;
    }
}
