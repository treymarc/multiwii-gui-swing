package eu.kprod.serial;

/**
 * 
 * @author treym
 * 
 */
public class SerialNotFoundException extends SerialException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SerialNotFoundException() {
        super();
    }

    public SerialNotFoundException(final String message) {
        super(message);
    }

    public SerialNotFoundException(final String message,final Throwable cause) {
        super(message, cause);
    }

    public SerialNotFoundException(final Throwable cause) {
        super(cause);
    }
}
