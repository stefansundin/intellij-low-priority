# What this does
- Plugin for IntelliJ IDEA and friends (e.g. Android Studio)
- **Windows only**
- The plugin sets the priority of the editor's process to low priority.
- The purpose is to prevent your computer from lagging as shit when you try to compile something.
- The plugin uses JNA to make system calls.

# Usage
Right now, the plugin has to be invoked manually using the _Tools_ menu. In a future version, this will most likely be automated in some capacity.

If the editor has already spawned a `java.exe` child process, it's priority will not be changed automatically. This will be done in the next version.

# Installation
1. Download [latest version](https://github.com/stefansundin/intellij-low-priority/releases/latest).
2. Open _File_ → _Settings..._ (Ctrl+Alt+S)
3. Select _Plugins_ on the left.
4. Click _Install plugin from disk..._
5. Select the zip file you downloaded in the file picker.
6. Restart editor.

# Build
- Compiled with [JDK 6u45](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase6-419409.html#jdk-6u45-oth-JPR) and IntelliJ 13.1.4.
