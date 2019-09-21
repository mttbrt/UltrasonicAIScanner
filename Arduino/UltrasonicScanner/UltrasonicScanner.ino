#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>

#include <NewPing.h>
#include <Servo.h>
 
#define TRIGGER_PIN D8
#define ECHO_PIN D5
#define MAX_DISTANCE 200
#define PIN_SERVO_ORIZZONTAL D10
#define PIN_SERVO_VERTICAL D9

NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE); 

Servo horizontalServo;
Servo verticalServo;
bool execute = true;

const char* ssid = "HotSpot";
const char* password = "password";

int distance;
int. data[36];
int iterator = 0;

void setup () {
  Serial.begin(9600); // 115200 or 9600
  WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print("Connecting...");
  }

  horizontalServo.attach(PIN_SERVO_ORIZZONTAL);
  verticalServo.attach(PIN_SERVO_VERTICAL);

  // Setup
  horizontalServo.write(90);
  verticalServo.write(90);
}

void loop() {
  if(execute) {
    bool scanLeft = true;
    
    for(int verticalPosition = 67; verticalPosition < 116; verticalPosition+=8){
      verticalServo.write(verticalPosition);
     
      if(scanLeft) {
        for(int horizontalPosition = 67 ; horizontalPosition < 118; horizontalPosition+=8){
          horizontalServo.write(horizontalPosition);
  
          unsigned int distance = sonar.ping_cm();
          data[iterator++] = distance;
        }
        scanLeft = false;
      } else {
        for(int horizontalPosition = 118 ; horizontalPosition > 68; horizontalPosition-=8){
          horizontalServo.write(horizontalPosition);

          unsigned int distance = sonar.ping_cm();
          data[iterator++] = distance;
        }
        scanLeft = true;
      }
     
    }
    execute = false;
    
    if (WiFi.status() == WL_CONNECTED) { // Check WiFi connection status
      HTTPClient http;  // Declare an object of class HTTPClient
      
      http.begin("http://www.mysite.com/" + data); // TODO: fix
      int httpCode = http.GET();
       
      if (httpCode > 0) { // Check the returning code
        String payload = http.getString();   // Get the request response payload
        Serial.println(payload);  
      }
       
      http.end(); // Close connection
    }
  }

}
