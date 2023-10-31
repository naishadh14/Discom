# Discom
A Java based android app to automatically discover routes using bluetooth mesh networks with ad-hoc routing.

# Use Case
When an earthquake occurs, communication infrastructure breaks down. When cell towers break and WiFi cables are ruptured, there is suddenly no way to communicate with other people - despite being fairly close by. This app aims to bridge that gap by creating a mesh of bluetooth devices so that users can still communicate with each other, despite not being directly in range of each other.

If the user A wants to talk to B, they might not within bluetooth range of each other. However, user C might be connected to both of them. In that case, user A can send a message meant for user B, by routing the message through user C [who will never be shown the message - since he isn't the intended target] by using this app. We have named it Discom, standing for *DIS*aster *COM*munication.

# Optimizations
Since we use a broadcast based transmission mechanism, extra transmissions are inevitable. Since there is no way to predetermine the route, we MUST broadcast messages in the hope of reaching our intended target. However, we have used some optimizations to prevent redundant transmissions as far as possible.

### Timestamps
Let's say user A is connected to (B, C), and sends a message meant for D.
User B is connected to (A, C)
User C is connected to (A, B, D)

In this case, user A will broadcast the message to B & C.
B will broadcast the message to C & D.
Observe that user C has now received the SAME message twice - once from A & once from B. Since C will broadcast the message to D the first time around, we need not send it again! But, how do we identify the message is the same? Timestamps! 

When user A generates the message, we assign a unique timestamp for that message. When C receives the duplicate message from B, it realizes that it's a duplicate message, and simply drops it. With this, we can massively reduce the number of redundant transmissions occuring in the network.

### Ignore source
If B receives a message meant for D, from user A, it will broadcast the message to every node it is connected to - except the node it received a message from.
Hence, B will send the message to C, but not back to A [since that was the source].
Similarly, when C receives the message from B, it will broadcast it to D, but not B.


# Setup
There are 3 ways to test this Android App -

### Use the pre-built APK
A pre-built APK has been generated at ```app/build/outputs/apk/debug```. The APK there can be imported into any Android phone running Android 12+.
It might require you to bypass security settings if prompted by Google Play Protect (click "Install anyway").

### Generate the APK bundle
Once the project has been imported into Android Studio, we can use the Build > Build Bundle/APK > Build APK to build the APK manually.
As above, this can be installed into any Android 12+ phone.

### Android Studio Emulators
Android studio offers emulators for a variety of Android phones. With this option, we can run the app on a virtual phone on the machine itself, without the need for installing the APK on a separate mobile device.


# What it looks like

Here is the home page, where the user registers using his phone number -
![home_page](https://github.com/naishadh14/Discom/assets/29754746/b1b36089-1021-4391-8a93-146cf53397b6)

Here we have the bluetooth discovery page - 
![discovery_page](https://github.com/naishadh14/Discom/assets/29754746/61ff81f8-4e13-471f-a093-37889723d9ab)

The center button is clickable, and initiates the discovery of nearby bluetooth devices -
![discovered_devices](https://github.com/naishadh14/Discom/assets/29754746/bb877a70-996b-4713-a150-740ae610420f)

Here we have the bluetooth pairing page, where we initiate requests to pair to the discovered bluetooth devices -
![pairing_prompt](https://github.com/naishadh14/Discom/assets/29754746/9df37ad0-fcf3-442b-b4f6-d671e7e68cb8)

Finally, we can send messages to our intended users on the messaging page - 
![messaging_page](https://github.com/naishadh14/Discom/assets/29754746/8e2f4c4b-95de-484f-b229-4be9b1a53705)
