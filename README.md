# ZipBolt
ZipBolt is a file-sharing platform that allows digital devices to share files at incredible speeds using WiFi Peer-to-Peer technology. ZipBolt originated from the idea that creating network connections across multiple devices irrespective of device operating systems should be as fast as a finger snap and sharing large files through a wireless network should be done in the blink of an eye. 

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

## App Screenshots 
### Light Mode 
<img src="https://user-images.githubusercontent.com/43956851/133894965-0e083cfe-df3c-4ecd-ac45-d5d6e407be80.jpg" width="216" height="468"> <img src="https://user-images.githubusercontent.com/43956851/133894941-1157f5a0-8930-41df-9297-fd9923463a69.jpg" width="216" height="468">  <img src="https://user-images.githubusercontent.com/43956851/133894669-f75d001b-1773-488c-b431-a0702f82b627.jpg" width="216" height="468">  <img src="https://user-images.githubusercontent.com/43956851/133894779-f623912d-6a1d-4359-99c1-059d7a128a4e.jpg" width="216" height="468">  <img src="https://user-images.githubusercontent.com/43956851/133894847-0b0972dd-cac7-46ad-8ad1-6dd4dc320329.jpg" width="216" height="468">  <img src="https://user-images.githubusercontent.com/43956851/133894952-0a2ef9c5-138b-4e2c-86ab-834808c58490.jpg" width="216" height="468">

### Dark Mode 

## Open-source libraries used 
 - [ShimmerLayout](https://github.com/facebook/shimmer-android) - Shimmer is an Android library that provides an easy way to add a shimmer effect to any view in your Android app.
 - [Dagger/Hilt](https://dagger.dev/hilt/) - Hilt provides a standard way to incorporate Dagger dependency injection into an Android application.
 - [Android Jetpack](https://developer.android.com/jetpack) - Jetpack is a suite of libraries to help developers follow best practices, reduce boilerplate code, and write code that works consistently across Android versions and devices so that developers can focus on the code they care about.
 - [Glide](https://github.com/bumptech/glide) - Glide is a fast and efficient open source media management and image loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface.
 - [Junit4](https://junit.org/junit4/) - JUnit is a simple framework to write repeatable tests. It is an instance of the xUnit architecture for unit testing frameworks.
 - [Robolectric](http://robolectric.org) - Robolectric is a framework that brings fast and reliable unit tests to Android. 

## How to Run ZipBolt on android-: 
1. Transfer the apk file to an android device 
2. Install the app 
3. Run the app 

## Todos and Future plans 
1. Refactor app to follow clean architecture with multiple modules 
2. Increase test coverage across all layers 
3. Refactor File transfer layer into a library 
4. Migrate the UI to Jetpack compose
5. Create ZipBolt for ios, macOS, Windows, Linux, etc 

