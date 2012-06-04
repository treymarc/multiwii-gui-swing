package eu.kprod.serial;

/**
 *
 * @author treym
 *
 */
public interface SerialListener {

    /**
     *
     * @param s recieved byte
     */
    void readSerialByte(byte s);

}
