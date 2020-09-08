package net.ssehub.rightsmanagement.model;

import java.util.Objects;

/**
 * Manages an individual participant of the course, typically a student.
 * 
 * @author kunold
 * @author Adam
 */
public class Individual implements Comparable<Individual> {
    
    private String userName;
    
    /**
     * Creates an individual.
     * 
     * @param userName The RZ name of this user.
     */
    public Individual(String userName) {
        this.userName = userName;
    }
    
    /**
     * Returns the user name of this student.
     * 
     * @return The RZ name of this user.
     */
    public String getName() {
        return userName;
    }
    
    @Override
    public int compareTo(Individual other) {
        return this.userName.compareTo(other.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Individual)) {
            return false;
        }
        Individual other = (Individual) obj;
        return Objects.equals(userName, other.userName);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Individual [userName=");
        builder.append(userName);
        builder.append("]");
        return builder.toString();
    }
    
}
