package net.ssehub.rightsmanagement.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import io.swagger.client.model.AssignmentDto.StateEnum;

/**
 * Manages the Assignments.
 * 
 * @author Kunold
 * @author El-Sharkawy
 *
 */
public class Assignment implements Iterable<IParticipant> {

    private String name;
    
    private StateEnum status;
    
    private Set<IParticipant> participants = new TreeSet<>();
    
    /**
     * Adds one participant to the assignment.
     * @param participant to add to the set.
     */
    public void addParticipant(IParticipant participant) {
        participants.add(participant);
    }
    
    /**
     * Adds all specified participants to the assignment.
     * @param participants to add to the set.
     */
    public void addAllParticipants(Collection<? extends IParticipant> participants) {
        for (IParticipant participant : participants) {
            addParticipant(participant);
        }
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
    public void setStatus(StateEnum status) {
        if (null != status) {
            this.status = status;
        }
    }
    
    /**
     * Getter for the status of the assignment.
     * @return the status of the assignment.
     */
    public StateEnum getStatus() {
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
