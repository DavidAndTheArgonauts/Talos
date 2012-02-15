# Robot

## Bashrc

To use the robot make sure the following lines are in your bashrc:

	export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/group/teaching/sdp/sdp3/secure/bluez/lib
	export NXJ_HOME=/group/teaching/sdp/sdp3/secure/lejos_nxj
	export PATH=$PATH:$JAVA_HOME/bin:$NXJ_HOME/bin

To edit your bashrc run the following command:

	gedit ~/.bashrc &

## Uploading

Go to the scripts directory and run the following:

	./uploadnxt.sh

## Starting the proxy

Go to the scripts directory and run the following:

	./startproxy.sh

