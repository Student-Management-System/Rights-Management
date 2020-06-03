package net.ssehub.rightsmanagement.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;

/**
 * Manages the Assignments.
 * 
 * @author Kunold
 * @author El-Sharkawy
 *
 */
public class Assignment extends net.ssehub.exercisesubmitter.protocol.frontend.Assignment
    implements Iterable<IParticipant> {

    private Set<IParticipant> participants = new TreeSet<>();
    
    /**
     * Creates a new Assignment instance for managing access rights.
     * @param dto The data received from the server.
     * @throws IllegalArgumentException If data was received which cannot be handled by the exercise
     *     submitters / reviewer system
     */
    public Assignment(AssignmentDto dto) throws IllegalArgumentException {
        super(dto);
    }

    /**
     * Creates manually an Assignment.
     * @param name The name of the assignment
     * @param assignmentID The ID used by the REST system, may be <tt>null</tt> during unit tests
     * @param state The state of the assignment. 
     * @param isGroupwork <tt>true</tt> for groups, <tt>false</tt> for individuals.
     */
    public Assignment(String name, String assignmentID, State state, boolean isGroupwork) {
        super(name, assignmentID, state, isGroupwork);
    }
    
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
    
    /**
     * Only intended for the Debugger: Returns a meaningful name during debugging.
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName();
    }
}
