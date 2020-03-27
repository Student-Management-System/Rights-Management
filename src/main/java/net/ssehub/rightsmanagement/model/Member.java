package net.ssehub.rightsmanagement.model;

/**
 * Manages the members.
 * 
 * @author kunold
 *
 */
public class Member implements IParticipant {
    
    private String userName;
    
    /**
     * Sets the user name of the student.
     * @param userName The RZ name.
     */
    public void setMemberName(String userName) {
        this.userName = userName;
    }
    
    @Override
    public String getName() {
        return userName;
    }

    /**
     * Only intended for the Debugger: Returns a meaningful name during debugging.
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName();
    }
}
