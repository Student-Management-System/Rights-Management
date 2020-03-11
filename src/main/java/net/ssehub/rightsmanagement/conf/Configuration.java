package net.ssehub.rightsmanagement.conf;

import java.util.List;

/**
 * Serves as data class for storing the configuration as a JSON object.
 * @author El-Sharkawy
 *
 */
public class Configuration {
    
    public static class CourseConfiguration {
        
        private String courseName;
        private String semester;
        private String repositoryPath;
        private String svnName;
        
        /**
         * @return the repositoryPath
         */
        public String getRepositoryPath() {
            return repositoryPath;
        }

        /**
         * @param repositoryPath the repositoryPath to set
         */
        public void setRepositoryPath(String repositoryPath) {
            this.repositoryPath = repositoryPath;
        }

        /**
         * @return the courseName
         */
        public String getCourseName() {
            return courseName;
        }

        /**
         * @param courseName the courseName to set
         */
        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        /**
         * @return the semester
         */
        public String getSemester() {
            return semester;
        }

        /**
         * @param semester the semester to set
         */
        public void setSemester(String semester) {
            this.semester = semester;
        }

        /**
         * @return the svnName
         */
        public String getSvnName() {
            return svnName;
        }

        /**
         * @param svnName the svnName to set
         */
        public void setSvnName(String svnName) {
            this.svnName = svnName;
        }
    }
    
    private int restServerPort;
    private List<CourseConfiguration> courses;
    
    /**
     * @return the courses
     */
    public List<CourseConfiguration> getCourses() {
        return courses;
    }
    
    /**
     * @param courses the courses to set
     */
    public void setCourses(List<CourseConfiguration> courses) {
        this.courses = courses;
    }
    
    /**
     * @return the restPort
     */
    public int getRestPort() {
        return restServerPort;
    }
    
    /**
     * @param restPort the restPort to set
     */
    public void setRestPort(int restPort) {
        this.restServerPort = restPort;
    }

}
