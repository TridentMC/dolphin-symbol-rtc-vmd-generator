# Dolphin Symbol Map to RTC VMD Generator

A simple JavaFX application that loads in symbol maps for Gamecube and Wii games formatted for the Dolphin emulator, and provides a UI to filter and select multiple symbols and export them as a VMD for use with RTC Vanguard.

### Notes
The filter bar at the top of the application special-cases the `/` character allowing you to search for symbols that contain multiple keywords at once. 

For example, if you wanted to find three different symbols with two common attributes like the following:
- Foo.Attack.Damage.Critical.Script
- Script.Damage.Run.Sleep
- Fly.Script.Damage.Cry

You could match all three of these by searching for: `Script/Damage`
