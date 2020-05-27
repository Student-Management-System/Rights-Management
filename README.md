# Rights-Management
Rights-Management service of the Java submission system. 

## Build
To build and test the project execute: `mvn clean compile test assembly:single`

## Running

### Installation
We recommend to install the tool as a systemd service. An unit which may be used is placed in `CI/`

### Configuration
The tool needs to be configurated via the `settings.json`, which needs to be placed in the same directory as the application. An example of the configuration file is placed in `src/main/resources/`.

### Updates
The tool listens for course changes at: `<ip>:<port>/rest/update`
