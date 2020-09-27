# qubino-flush-shutter


### Steps

1. Create capability - definition of attributes & commands

  `smartthings capabilities:create`

  Name: Custom Levels

  Commands:

  customLevelOnePosition
│ customLevelThreePosition
│ customLevelTwoPosition

2. Create presentation capability - Define how the capability will look in the dashboard, detail view

2a. Define a file that a describe the controls

2b. Create the capability via the api

  `smartthings capabilities:presentation:create <XXXX NAMESPACE XXXX>.capabilityName 1 -y -i=customLevels.json`

3. Generate device config - Which capabilities to show on dashboard, detail view

3.a Export one based on DTH

  `smartthings presentation:device-config:generate 4df13d7a-e51b-4ef4-b5a8-2ea27990739a --dth -o=deviceConfig-1.json -j`

where id is the dth id from https://graph-eu01-euwest1.api.smartthings.com/ide/device/editor/[ID here]

3b. Remove extroneus output

  Remove presentationId
  Remove manufacturerName 

3c. Create the config via the api

  `smartthings presentation:device-config:create -j -i deviceConfig.json`

3d. add vid: 'xxx' to DTH, from the output of `presentation:device-config:create`
