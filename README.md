# VIPSCore
This is the server component that hosts the models. It is accessible via
VIPSCoreManager over http/REST. Models that are on the component's classpath 
are autodetected, indexed and made available through the REST service. The service is used by [VIPSLogic](https://gitlab.nibio.no/VIPS/VIPSLogic) when running models. It can also be contacted directly by any client.

## VIPSCoreManager
[VIPSCoreManager](https://gitlab.nibio.no/VIPS/VIPSCoreManager) is a layer in front of VIPSCore that handles authentication of clients and logging of usage. All services available in VIPSLogic are transparently accessible via VIPSCoreManager. Add this layer if you want to limit and log usage, and also if you want to set up e.g. scaling of VIPSCore instances.

![How VIPSCore and VIPSCoreManager fits into the whole VIPS architecture](./docs/illustrations/VIPS_whole_architecture.png "All VIPS system parts")

## Build and deploy
You need Maven installed.

Clone and build the [VIPSCommon package](https://gitlab.nibio.no/VIPS/VIPSCommon)

Clone and build this project

Download and install [WildFly](https://www.wildfly.org/) >= 25.0.1

Deploy the build from this project in Wildfly. 

Wildfly should run on Java >= 11

### Adding models
To add models, create this folder in WildFly:
`[WILDFLY_ROOT]/modules/no/nibio/vips/modelcontainer/main/`
Add the model jar file, and edit module.xml accordingly.

``` xml
<?xml version="1.0" encoding="UTF-8"?>

<!--
  	&copy; 2019 NIBIO
	Author: Tor-Einar Skog <tor-einar.skog@nibio.no>
  -->

<module xmlns="urn:jboss:module:1.1" name="no.nibio.vips.modelcontainer">
    <resources>
		<resource-root path="PsilaRosaeTempModel-1.0-SNAPSHOT.jar"/>
    </resources>
    <dependencies>
		<module name="no.nibio.vips.VIPSCommon" export="false"/>
		<module name="com.fasterxml.jackson.core.jackson-core" export="false"/>
		<module name="com.fasterxml.jackson.core.jackson-databind" export="false"/>
		<module name="com.vividsolutions.jts" export="false"/>
		<module name="org.renjin" export="false"/>
    </dependencies>
    
</module>

```

You also need to make sure you have the dependency modules available.


## Create client for a model
See [create_client.md](./docs/create_client.md)
