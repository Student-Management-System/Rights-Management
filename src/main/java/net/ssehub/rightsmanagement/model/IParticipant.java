package net.ssehub.rightsmanagement.model;

/**
 * Interface of participants.
 * 
 * @author kunold
 * @author El-Sharkawy
 *
 */
public interface IParticipant extends Comparable<IParticipant> {
    
    /**
     * Sorts the participants by their name.<p>
     * {@inheritDoc}
     * @param otherParticipant Another participant to sort with
     */
    @Override
    public default int compareTo(IParticipant otherParticipant) {
        return this.getName().compareTo(otherParticipant.getName());
    }
    
    /**
     * Getter method that is used by classes that implements this interface.
     * @return a name.
     */
    public String getName();

}
