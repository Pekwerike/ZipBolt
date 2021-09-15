# ZipBolt
ZipBolt is a file-sharing platform that allows digital devices to share files at incredible speeds using WiFi Peer-to-Peer technology. ZipBolt originated from the idea that creating network connections across multiple devices irrespective of the device operating system should be as fast as a finger snap and sharing large files through a wireless network could be done in the blink of an eye. 

At the moment after building on the strength of Wifi P2P technology, ZipBolt can help digital devices create network connections in an average of 2 seconds, and hopefully based on hardware advancements in the future ZipBolt will reach its ideal file transfer speed. 

## What's WiFi P2P/ WiFi Direct?
According to Wi-Fi Alliance, Wi-Fi CERTIFIED Wi-Fi Direct® enables Wi-Fi devices to connect directly to each other, making it simple and convenient to print, share, sync, play games, and display content to another device. Wi-Fi Direct devices connect to one another without joining a traditional home, office, or public network.

Wi-Fi Direct devices can connect anywhere, anytime—even when there is no access to a Wi-Fi network nearby. Wi-Fi Direct devices emit a signal to other devices in the area, letting them know a connection can be made. Users can view available devices and request a connection or may receive an invitation to connect to another device. When two or more Wi-Fi Direct-certified devices connect directly, they form a Wi-Fi Direct group using Wi-Fi Protected Setup™.

ZipBolt makes use of WiFi Direct service discovery APIs to allow devices to discover each other and create a network without the need for a server or router.

## Communication Protocol 
To maintain real-time communication across connected devices, ZipBolt has its own custom communication protocol that was built on top of the Sockets API. This communication protocol also makes it possible for ZipBolt to share any type of files and even directories across connected devices. 

## ZipBolt Features 
1. Very fast device connections with few permission requests 
2. Incredible file transfer speeds 
3. Share any type of file, even directories 
4. Beautiful User Interface with simple structure
5. Lightweight when compared to other file transfer apps in the market 

## UI Screenshots 

## Open-source libraries used 
 - ShimmerLayout 
 - Dagger/Hilt 
 - Android Jetpack 
 - Glide  

## How to Run ZipBolt on android-: 
1. Transfer the apk file to an android device 
2. Install the app 
3. Run the app 

## Todos and Future plans 
1. Refactor app to follow clean architecture with multiple modules 
2. Increase test coverage across all layers 
3. Refactor File transfer layer into a library 
4. Create ZipBolt for ios, macOS, Windows, Linux, etc 

