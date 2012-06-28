package eu.kprod.gui;

public class MwGuiRuntimeException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * create a new MwGuiExpception
     * @param msg message
     * @param e the parent exception
     */
    public MwGuiRuntimeException(final String msg, final Exception e) {
        // TODO Auto-generated constructor stub
        super(msg, e);
    }

    public MwGuiRuntimeException(String msg) {
        super(msg);
    }

}
