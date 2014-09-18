Android Autoshot
================
Proof of concept code for automatic localized batch screenshot taking of Android apps.

The problem
-----------
You have a localized app, that runs on multiple device sizes, and now you want to provide localized screenshots for all these device types.
Now you have a problem: you are too lazy to actually fire up a phone and tablet and change languages and go through all the motions in your app, taking screenshots through ddms or whatever...

The solution
-----------
Fortunately, I was that lazy too and created a Robotium powered screenshot taker that handles localized apps: Autoshot!

Main issue with existing 'solutions' was that they required adb commands to change complete device language. Autoshot doesn't need that, it can change the locale of the app itself and relaunch.

The code here is just a proof of concept. You will still have to manually code what Robotium does in your app, where to click, etc. There are probably better solutions out there, but this works great for me, and I use it for both my app KeyChord and Mini Piano Lite.

It's basically a Junit instrumented test project. To run it, run it as 'Android Junit test'.

Tips/Notes
-----------
- Use Solo.sleep() generously. If you wait too little between actions, the animations of your app will not have finished and you have weird screenshots
- Use a adb pull after it ran, to get all the screenshots, or use DDMS in eclipse
- On newer android versions you can enable detailed touch feedback overlays, so you'll see what your code actually does when 'touching' stuff
- You will need to have WRITE_EXTERNAL_STORAGE permission in the TARGET APP's manifest.

Thanks
-----------
Code was inspired by a Dutch AUG talk on Jenkins by the guy who wrote the Android Jenkins plugin: Christopher Orr

Issues
-----------
This code is based on Robotium 5.0.1. You'll probably have to tweak stuff to make it work with a newer one. But hey, it's a proof of concept :)




