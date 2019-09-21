# Ultrasonic AI Scanner

Arduino-based 3D Surface Scanner with Artificial Intelligence Object Classification.
![Project diagram](https://github.com/methk/UltrasonicAIScanner/blob/master/res/diagram.png)
## Arduino Model
The model is composed of two servo motors: one for vertical motion and one for horizontal motion, and an ultrasonic sensor on top of them. The distances are recorded in a 6x6 grid model and are sent to the server via WiFi module.
## Server and IA
A basic Node.js server hosts a feed-forward Neural Network with back-propagation developed with Brain.js. The network is trained with two simple objects: bottle and smartphone.
## Android App
The app get the classification result from the server and read aloud the result.

