package net.ssehub.rightsmanagement.model;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Manages the Assignments.
 * 
 * @author Kunold
 *
 */
public class Assignment implements Iterable<IParticipant> {

    private String name;
    
    private AssignmentStates status;
    
    private Set<IParticipant> participants = new TreeSet<>();
    
    /**
     * Adds participants to the set of participants
     * @param participant to add to the set.
     */
    public void addParticipant(IParticipant participant) {
        participants.add(participant);
    }
    
    /**
     * Sets the name of the assignment.
     * @param name of the assignment.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Getter for name of the assignment.
     * @return the name of the assignment.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the status of a assignment.
     * @param status of the assignment.
     */
    public void setStatus(AssignmentStates status) {
        if (null != status) {
            this.status = status;
        }
    }
    
    /**
     * Getter for the status of the assignment.
     * @return the status of the assignment.
     */
    public AssignmentStates getStatus() {
        return status;
    }

    @Override
    public Iterator<IParticipant> iterator() {
        return participants.iterator();
    }
    
    /**
     * Getter for the participants of the assignment.
     * @return the participants.
     */
    public String[] getParticipants() {
        String[] participants = new String[this.participants.size()];
        int index = 0;
        for (IParticipant participant : this.participants) {
            participants[index++] = participant.getName();
        }
        return participants;
    }
   
}
