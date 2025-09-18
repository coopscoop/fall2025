#include <Arduino.h>
#include "Freenove_WS2812_Lib_for_ESP32.h"

// pin to use for analog input
#define PIN_ANALOG 36
#define LED_BUILTIN 2

// number of LED's in strip
#define RGB_COUNT 1
// GPIO pin connected to W2812 LED Controller.
#define PIN_RGB_LED 16
// channel number
#define CHANNEL 0
// WS2812 object
Freenove_ESP32_WS2812 rgb_led = Freenove_ESP32_WS2812(RGB_COUNT, PIN_RGB_LED, CHANNEL, TYPE_GRB);

void setup()
{
//   // initial serial monitor
  Serial.begin(115200);
  // init RGB LED
  rgb_led.begin();
  // set initial brightness. Use 0-255 here.
  rgb_led.setBrightness(20);
}

void loop()
{
  // trigger A/D converter to digitize signal
  int iVal = analogRead(PIN_ANALOG);

  float voltage = map(iVal, 0, 4095, 0, 3300) / 1000.0;

  // change colour of built-in LED depending on voltage level
  // 0v -> solid blue
  // 1.65v (half way) -> solid green
  // 3.3v -> solid red

  // these should be overwritten
  int red = 0;
  int green = 0;
  int blue = 0;

  if (iVal < 2048)
  {
    // up to half
    blue = map(iVal, 0, 2048, 255, 0);
    green = map(iVal, 1024, 2048, 0, 255);
    red = 0;
  }
  else
  {
    // above half
    blue = 0;
    green = map(iVal, 2048, 3072, 255, 0);
    red = map(iVal, 2048, 4095, 0, 255);
  }

  if (green < 0)
    green = 0;

  // print digitized value to serial monitor
  Serial.printf("digitized signal: %5d | voltage: %.2f | ", iVal, voltage);
  Serial.printf("red: %3d | green: %3d | blue: %3d\n", red, green, blue);

  // set the built in LED colour to the calculated RGB value
  rgb_led.setLedColorData(0, red, green, blue);
  rgb_led.show();
}
