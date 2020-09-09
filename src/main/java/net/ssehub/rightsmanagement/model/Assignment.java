package net.ssehub.rightsmanagement.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;

/**
 * Represents an assignment. An assignment has a list of {@link Group}s which in turn contain {@link Individual}s
 * working on this assignment. Groups may be actual groups of multiple students or simply a single student (in which
 * case the group name is the user name)
 * 
 * @author Kunold
 * @author El-Sharkawy
 * @author Adam
 */
public class Assignment extends net.ssehub.exercisesubmitter.protocol.frontend.Assignment implements Iterable<Group> {

    private Set<Group> groups = new TreeSet<>();
    
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
        super(name, assignmentID, state, isGroupwork, 0);
    }
    
    @Override
    public String getID() {
        return super.getID();
    }
    
    /**
     * Adds a group that works on this assignment.
     * @param group A group working on this assignment.
     */
    public void addGroup(Group group) {
        groups.add(group);
    }
    
    /**
     * Adds all specified groups to the assignment.
     * 
     * @param groups To add to the set.
     */
    public void addAllGroups(Collection<Group> groups) {
        for (Group group : groups) {
            addGroup(group);
        }
    }
    
    /**
     * Overrides the previously stored groups.
     * 
     * @param groups The new list of groups
     */
    public void setGroups(List<Group> groups) {
        this.groups.clear();
        this.groups.addAll(groups);
    }
    
    /**
     * Returns a sorted array of all group names of this exercise.
     * 
     * @return An array of all group names.
     */
    public String[] getAllGroupNames() {
        return groups.stream()
                .map((group) -> group.getName())
                .collect(Collectors.toList())
                .toArray(new String[0]);
    }
    
    @Override
    public Iterator<Group> iterator() {
        return groups.iterator();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(groups);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Assignment)) {
            return false;
        }
        Assignment other = (Assignment) obj;
        return Objects.equals(groups, other.groups);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Assignment [getName()=");
        builder.append(getName());
        builder.append(", isGroupWork()=");
        builder.append(isGroupWork());
        builder.append(", groups=");
        builder.append(groups);
        builder.append("]");
        return builder.toString();
    }

}
