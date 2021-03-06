package net.ssehub.rightsmanagement.conf;

import java.util.List;

import net.ssehub.rightsmanagement.logic.UpdateChangeListener.UpdateStrategy;

/**
 * Serves as data class for storing the configuration as a JSON object.
 * @author El-Sharkawy
 *
 */
public class Configuration {
    
    /**
     * Stores the configuration to one course.
     * @author El-Sharkawy
     *
     */
    public static class CourseConfiguration {
        
        private String courseName;
        private String semester;
        private String repositoryPath;
        private String accessPath;
        private String svnName;
        private String author;
        private boolean initRepositoryIfNotExists;
        private UpdateStrategy updateStrategy;

        /**
         * The name of the course as used by the management system.
         * @return the courseName
         */
        public String getCourseName() {
            return courseName;
        }
        
        /**
         * The name of the semester as used by the management system.
         * @return the semester
         */
        public String getSemester() {
            return semester;
        }
        
        /**
         * The absolute path the the repository (its base folder).
         * @return the repositoryPath
         */
        public String getRepositoryPath() {
            return repositoryPath;
        }
        
        /**
         * The absolute path to the access file.
         * @return the accessPath
         */
        public String getAccessPath() {
            return accessPath;
        }
        
        /**
         * The name of the repository as used inside the access file.
         * @return the svnName
         */
        public String getSvnName() {
            return svnName;
        }
        
        /**
         * The name of the management system.
         * @return the author.
         */
        public String getAuthor( ) {
            return author;
        }
        
        /**
         * Specifies the {@link UpdateStrategy} to use for the course.
         * @return the updateStrategy
         */
        public UpdateStrategy getUpdateStrategy() {
            return updateStrategy;
        }

        /**
         * Specifies if a new, local SVN repository shall be created if it does not exist on the server, yet.
         * @return <tt>true</tt> creates a new SVN repository, <tt>false</tt> assumes that repositories
         *     are created always before the application starts.
         */
        public boolean isInitRepositoryIfNotExists() {
            return initRepositoryIfNotExists;
        }

        /**
         * The absolute path to the access file.
         * @param accessPath the accessPath to set
         */
        public void setAccessPath(String accessPath) {
            this.accessPath = accessPath;
        }

        /**
         * The absolute path the the repository (its base folder).
         * @param repositoryPath the repositoryPath to set
         */
        public void setRepositoryPath(String repositoryPath) {
            this.repositoryPath = repositoryPath;
        }

        /**
         * The name of the course as used by the management system.
         * @param courseName the courseName to set
         */
        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        /**
         * The name of the semester as used by the management system.
         * @param semester the semester to set
         */
        public void setSemester(String semester) {
            this.semester = semester;
        }

        /**
         * The name of the repository as used inside the access file.
         * @param svnName the svnName to set
         */
        public void setSvnName(String svnName) {
            this.svnName = svnName;
        }

        /**
         * The name of the management system.
         * @param author the author to set.
         */
        public void setAuthor(String author) {
            this.author = author;
        }
        /**
         * Specifies the {@link UpdateStrategy} to use for the course.
         * @param updateStrategy the updateStrategy to set
         */
        public void setUpdateStrategy(UpdateStrategy updateStrategy) {
            this.updateStrategy = updateStrategy;
        }
        
        /**
         * Specifies if a new, local SVN repository shall be created if it does not exist on the server, yet.
         * @param initRepositoryIfNotExists <tt>true</tt> creates a new SVN repository, <tt>false</tt> assumes
         *     that repositories are created always before the application starts.
         */
        public void setInitRepositoryIfNotExists(boolean initRepositoryIfNotExists) {
            this.initRepositoryIfNotExists = initRepositoryIfNotExists;
        }
    }
    
    private int restServerPort;
    private String authServerURL;
    private String mgmtServerURL;
    private List<CourseConfiguration> courses;
    private String cacheDir;
    private String authUser;
    private String authPassword;
    
    /**
     * Configurations of courses managed by this service.
     * @return the courses
     */
    public List<CourseConfiguration> getCourses() {
        return courses;
    }
    
    /**
     * The port at which service listens for incoming update events.
     * @return the restPort
     */
    public int getRestPort() {
        return restServerPort;
    }
    
    /**
     * The location where to store cached information of managed courses.
     * @return the cacheDir
     */
    public String getCacheDir() {
        return cacheDir;
    }
    
    /**
     * The URL to query the student management system.
     * @return The mgmtServerURL
     */
    public String getMgmtServerURL() {
        return mgmtServerURL;
    }
    
    /**
     * The URL to query the authentication system.
     * @return The authServerURL
     */
    public String getAuthServerURL() {
        return authServerURL;
    }
    
    /**
     * Optional user name to authenticate at the <b>student management system</b>.
     * @return the authUser
     */
    public String getAuthUser() {
        return authUser;
    }
    
    /**
     * Optional password to authenticate at the <b>student management system</b>.
     * @return the authPassword
     */
    public String getAuthPassword() {
        return authPassword;
    }
    
    /**
     * Configurations of courses managed by this service.
     * @param courses the courses to set
     */
    public void setCourses(List<CourseConfiguration> courses) {
        this.courses = courses;
    }
    
    /**
     * The port at which service listens for incoming update events.
     * @param restPort the restPort to set
     */
    public void setRestPort(int restPort) {
        this.restServerPort = restPort;
    }

    /**
     * The location where to store cached information of managed courses.
     * @param cacheDir the cacheDir to set
     */
    public void setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
    }

    /**
     * The URL to query the student management system.
     * @param mgmtServerURL The mgmtURL to set
     */
    public void setMgmtURL(String mgmtServerURL) {
        this.mgmtServerURL = mgmtServerURL;
    }

    /**
     * The URL to query the authentication system.
     * @param authServerURL The authServerURL to set
     */
    public void setAuthServerURL(String authServerURL) {
        this.authServerURL = authServerURL;
    }

    /**
     * Optional user name to authenticate at the <b>student management system</b>.
     * @param authUser the authUser to set
     */
    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    /**
     * Optional password to authenticate at the <b>student management system</b>.
     * @param authPassword the authPassword to set
     */
    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }
}
