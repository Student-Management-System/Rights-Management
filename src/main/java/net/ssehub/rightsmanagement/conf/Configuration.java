package net.ssehub.rightsmanagement.conf;

import java.util.List;

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
         * The absolute path to the access file.
         * @param accessPath the accessPath to set
         */
        public void setAccessPath(String accessPath) {
            this.accessPath = accessPath;
        }

        /**
         * @param repositoryPath the repositoryPath to set
         */
        public void setRepositoryPath(String repositoryPath) {
            this.repositoryPath = repositoryPath;
        }

        /**
         * @param courseName the courseName to set
         */
        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        /**
         * @param semester the semester to set
         */
        public void setSemester(String semester) {
            this.semester = semester;
        }

        /**
         * @param svnName the svnName to set
         */
        public void setSvnName(String svnName) {
            this.svnName = svnName;
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
     * @param courses the courses to set
     */
    public void setCourses(List<CourseConfiguration> courses) {
        this.courses = courses;
    }
    
    /**
     * @param restPort the restPort to set
     */
    public void setRestPort(int restPort) {
        this.restServerPort = restPort;
    }

    /**
     * @param cacheDir the cacheDir to set
     */
    public void setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
    }

    /**
     * @param mgmtURL the mgmtURL to set
     */
    public void setMgmtURL(String mgmtURL) {
        this.mgmtURL = mgmtURL;
    }
}
