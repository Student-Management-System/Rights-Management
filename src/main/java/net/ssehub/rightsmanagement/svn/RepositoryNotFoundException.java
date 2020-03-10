package net.ssehub.rightsmanagement.svn;

/**
 * Exception for the case that the repository is not found.
 * 
 * @author Kunold
 *
 */
public class RepositoryNotFoundException extends Exception {

    /**
     * Generated.
     */
    private static final long serialVersionUID = -178396927402012692L;
    
    /**
     * A exception that occurs when the repository is not found.
     * @param msg the exception message.
     */
    public RepositoryNotFoundException(String msg) {
        super(msg);
    }
    
    /**
     * A exception that occurs when the repository is not found.
     * @param cause can throw another exception.
     */
    public RepositoryNotFoundException(Throwable cause) {
        super(cause);
    }

}
