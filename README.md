# oep-demo
Oracle Event Processing with Diffusion Demo

##Building
To build the demo, ```cd``` into each of the HomeMonitorDemo* directories and run ```mvn clean install```. This requires that Maven and JDK7+ are installed.

##Running
Deploy the HomeMonitorDemoDiffusion publisher to a Diffusion instance by copying from ```HomeMonitorDemoDiffusion/target/publisher-0.1-SNAPSHOT.dar``` to your ```${DIFFUSION_DIR}/deploy``` directory.

To run Diffusion, run ```DIFFUSION_DIR}/bin/diffusion.sh```

The demo can now be accessed through a web browser at ```localhost:8080/sensors/```

To run the accompanying OEP adapters, ensure you have setup a domain. Navigate to ```${OEP_DIR}/user_projects/domains/diffusion/defaultserver``` and run ```./startwlevs.sh```
