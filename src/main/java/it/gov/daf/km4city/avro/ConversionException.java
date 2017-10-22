package it.gov.daf.km4city.avro;

public class ConversionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConversionException() {
    }

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConversionException(Throwable cause) {
        super(cause);
    }

}
