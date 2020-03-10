package net.ssehub.rightsmanagement.model;

/**
 * Manages the members.
 * 
 * @author kunold
 *
 */
public class Member implements IParticipant {
    
    private String userName;
    
    public void setMemberName(String userName) {
        this.userName = userName;
    }
    
    @Override
    public String getName() {
        return userName;
    }

}
