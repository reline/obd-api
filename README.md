obd-api
============

OBD-II API

## Important resources

Before opening an issue or using this library, please take a look at the following resources:

* [Understanding OBD](https://www.elmelectronics.com/help/obd/tips/#UnderstandingOBD)
* [The ELM327](https://www.elmelectronics.com/help/obd/tips/#327_Commands)


### Example ###

After pairing and establishing Bluetooth connection to your ELM327 device..
```kotlin
..
// retrieve Bluetooth socket
socket = .. // specific to the platform you're using

// execute commands
try {
  EchoOffCommand().run(socket.inputStream, socket.outputStream)
  LineFeedOffCommand().run(socket.inputStream, socket.outputStream)
  TimeoutCommand(125).run(socket.inputStream, socket.outputStream)
  SelectProtocolCommand(ObdProtocols.AUTO).run(socket.inputStream, socket.outputStream)
  AmbientAirTemperatureCommand().run(socket.inputStream, socket.outputStream)
} catch (e: Exception) {
  // handle errors
}
```

## Troubleshooting ##

As *@dembol* noted:

Have you checked your ELM327 adapter with Torque or Scanmaster to see if it works with your car? Maybe the problem is with your device?

Popular OBD diagnostic tools reset state and disable echo, spaces etc before protocol selection. Download some ELM327 terminal for android and try following commands in order:
```
ATD
ATZ
AT E0
AT L0
AT S0
AT H0
AT SP 0
```
