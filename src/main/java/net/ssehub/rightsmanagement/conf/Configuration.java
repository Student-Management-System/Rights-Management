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
         * Specifies the {@link UpdateStrategy} to use for the course.
         * @return the updateStrategy
         */
        public UpdateStrategy getUpdateStrategy() {
            return updateStrategy;
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
         * Specifies the {@link UpdateStrategy} to use for the course.
         * @param updateStrategy the updateStrategy to set
         */
        public void setUpdateStrategy(UpdateStrategy updateStrategy) {
            this.updateStrategy = updateStrategy;
        }
    }
    
    private int restServerPort;
    private String mgmtURL;
    private List<CourseConfiguration> courses;
    private String cacheDir;
    
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
     * @return the mgmtURL
     */
    public String getMgmtURL() {
        return mgmtURL;
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
     * @param mgmtURL the mgmtURL to set
     */
    public void setMgmtURL(String mgmtURL) {
        this.mgmtURL = mgmtURL;
    }
}
