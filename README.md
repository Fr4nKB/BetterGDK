# BetterGDK
## Disclaimer
This project is a **modified version of Glyph Developer Kit by Nothing-Developer-Programme**. The original project can be found [here](https://github.com/Nothing-Developer-Programme/Glyph-Developer-Kit/). This modified version is distributed under the [Your License] license. The original author is not responsible for any changes made in this version.

## Why is it better?
Numerous bugs have been fixed and the code was made more readable:
-  Incorrect handling for Phone(2a) Plus which lead into GDK not working on that model
-  `toggle` method now actually toggles off old frames: previously, frames were "summed" if you didn't call `getFrameBuilder` to create different frames
-  `animate` method actually works now: all methods like `buildCycles` are now useful
-  `turnOff` should be more reliable
-  New `buildChannelC1` method to build Phone(2)'s variable Glyph in one go

## How to compile
You will need:
-  A Java IDE, I'm using Eclipse
-  Android SDK (version 34)

Once you have imported the project, you need to add `AppData/Local/Android/Sdk/platforms/android-34/android.jar`
in the classpath, **be sure not to have anything in the modulepath as it might conflict with Android's classes**.

In Eclipse you can then right click on the project -> Export -> Java -> JAR file -> Next -> Select the output path -> Finish.

## Usage
Just like Nothing's official GDK, place the package inside `app/libs` in your Android project and be sure to import the package in your dependencies.
