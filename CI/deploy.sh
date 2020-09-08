#!/bin/bash
jarFile=$(find ../target/ -name "RightsManagement-*-jar-with-dependencies.jar")
destFolder=Rightsmanagement

# Copy Unit & JAR
scp -i ~/.ssh/id_rsa_student_mgmt_backend "rightsmanagement.service" elscha@147.172.178.30:~/.config/systemd/user
scp -i ~/.ssh/id_rsa_student_mgmt_backend "${jarFile}" elscha@147.172.178.30:~/"${destFolder}"/RightsManagement.jar

# Update service
ssh -i ~/.ssh/id_rsa_student_mgmt_backend elscha@147.172.178.30 'systemctl --user daemon-reload'
ssh -i ~/.ssh/id_rsa_student_mgmt_backend elscha@147.172.178.30 'systemctl --user restart rightsmanagement'
