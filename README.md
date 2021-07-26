# Rights-Management
Rights-Management service of the Java submission system. 

## Build
To build and test the project execute: `mvn clean compile test assembly:single`

## Running

### Installation
We recommend to install the tool as a systemd service. An unit which may be used is placed in `CI/`
To install the service as user service you have to
* Copy the unit to your home directory at: `~/.local/share/systemd/<unit name>`
* Install as user service: `systemctl --user enable <unit name>`
* Start/Stop/Restart: `systemctl --user restart <unit name>`
* See system logs: `journalctl --user`

### Configuration
The tool needs to be configurated via the `settings.json`, which needs to be placed in the same directory as the application. An example of the configuration file is placed in `src/main/resources/`.
The configuration file contains a general section, containing configuration parameters that affect the complete application, and a course-specifc section that applies only to one course.

#### General Settings
| Setting | Description | Mandatory | Example |
|---------|-------------|-----------|---------|
| `authServerURL`  | URL to the authentication server (aka. Sparky-Service). | Required, if `authUser` and `authPassword` are used. | `http://147.172.178.30:8080` |
| `mgmtServerURL`  | URL to the backend of the **student management service**. | Required | `http://147.172.178.30:3000` |
| `restServerPort` | Port at which the RightsManagement service listens for course updates. | Required | `1111` |
| `cacheDir`       | Path to a folder, where course settings are cached locally. This may be an absolute path or a path relative to the JAR. | Required, if IncrementalUpdateHandler is used (cf. Course-specific Settings). | `cache/` |
| `authUser`       | Username to be used to query the **student management service**. | Required, if the **student management service** requires authenticated users to query requested APIs (usually the case) | `admin_user` |
| `authPassword`   | Password of `authUser`. | Required, if `authUser` is used | `1234` |


#### Course-specific Settings
These settings can be configured per observed course. However, observing more than one course is experimental right now.

| Setting | Description | Mandatory | Example |
|---------|-------------|-----------|---------|
| `courseName`                | The short name of the observed course as used by the **student management service** to identify a course. | Required | `java` |
| `semester`                  | The semester of the observed course as used by the **student management service** to identify a course. | Required | `wise1920` |
| `repositoryPath`            | The path to the folder of an SVN-repository to manage by the RightsManagement service | Required | `/repository/java/` |
| `accessPath`                | The path to an SVN access file that is used to manage the access rights of the aforementioned repository. | Required | `/repository/java.access` |
| `svnName`                   | The name of the repository as used it is published (e.g., by Apache). | Required | `java-abgaben` |
| `updateStrategy`            | The strategy to be used to pull the relevant information from the **student management service**. `IMMEDIATELY`: pulls immediately the full course configuration to perform a complete update. `EVERY_MINUTE`: Like `IMMEDIATELY`, but waits 1 minute to reduce traffic. `FIVE_MINUTES`: Like `IMMEDIATELY`, but waits 5 minutes to reduce traffic. `INCREMENTAL`: Uses a cache an pulls only relevant information regarding changed objects. This works immediately and lowers the traffic. However, this is experimental and should not be used at the moment. | Required | `IMMEDIATELY` |
| `author           `         | An author name to be used when applying changes to the repository. Doesn't need to be an existing user. | Optional | `Studenten Management System` |
| `initRepositoryIfNotExists` | Specifies if a new repository shall be created at start-up if `repositoryPath` points to an empty of non-existent folder. | Optional | `true` |

### Updates
The tool listens for course changes at: `<ip>:<port>/rest/update`

You can check if the server is up at: `<ip>:<port>/rest/heartbeat`
