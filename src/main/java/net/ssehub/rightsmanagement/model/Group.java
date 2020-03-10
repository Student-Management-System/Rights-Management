package net.ssehub.rightsmanagement.model;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.ssehub.rightsmanagement.AccessWriter;

/**
 * Manages the Groups and Members.
 * 
 * @author Kunold
 *
 */
public class Group implements Iterable<String>, IParticipant {
    
    private String groupName;
    
    private Set<String> members = new TreeSet<>();
    
    /**
     * Sets the Name of a group.
     * @param groupName the name of the group.
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    /**
     * Getter for the Name of the Group.
     * @return the group name.
     */
    @Override
    public String getName() {
        return groupName;
    }
    
    /**
     * Adds Members to a group.
     * @param members that belongs to a group.
     */
    public void addMembers(String... members) {
        if (null != members) {
            for (int i = 0; i < members.length; i++) {
                this.members.add(members[i]);
            }
        }
    }

    @Override
    public Iterator<String> iterator() {
        return members.iterator();
    }

}
