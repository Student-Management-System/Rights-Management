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

### Updates
The tool listens for course changes at: `<ip>:<port>/rest/update`
