package net.ssehub.rightsmanagement.logic;

/**
 * Part of the {@link UpdateChangeListener} to denote that an update message points <b>not</b>
 * to a managed course / repository.
 * @author El-Sharkawy
 *
 */
public class WrongFormatException extends Exception {

    /**
     * Generated.
     */
    private static final long serialVersionUID = -5308268855197010469L;
    
    /**
     * Creates a new {@link WrongFormatException}.
     * @param cause the detail message. The detail message is saved for later retrieval by the
     *     {@link #getMessage()} method.
     */
    public WrongFormatException(String cause) {
        super(cause);
    }

}
