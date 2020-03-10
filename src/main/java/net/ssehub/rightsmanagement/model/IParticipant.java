package net.ssehub.rightsmanagement.model;

/**
 * Interface of participants.
 * 
 * @author kunold
 *
 */
public interface IParticipant extends Comparable<IParticipant> {
    
    /**
     * Sorts the participants with name.
     */
    public default int compareTo(IParticipant o) {
        return this.getName().compareTo(o.getName());
    }
    
    /**
     * Getter method that is used by classes that implements this interface.
     * @return a name.
     */
    public String getName();

}
