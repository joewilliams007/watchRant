# watchRant
A devRant client for your watch!

Compatible with wear os minsdk 28.

Tested and developed on Samsung watch5.

## PlayStore
https://play.google.com/store/apps/details?id=com.dev.watchrant

Note: The newest version will always be on Github first.

## Features

| Available |                Feature           |
| :-----------: | :--------------------------------: |
|       ✅       | feed (top/recent/algo) |
|       ✅       | rant (images supported) |
|       ✅       | up/down -vote rant |
|       ✅       | comments (images supported)|
|       ✅       | make huge rant and comment split|
|       ✅       | login / logout   |
|       ✅       | upload comment (only text)   |
|       ✅       | upload rant (only text)   |
|       ✅       | tap on comment to add @username to your comment    |
|       ✅       | themes (dark/amoled/partlyAmoled/green) |
|       ✅       | Animation of SIMMORSAL devRantNativeClient   |
|       ✅       | disable animation   |
|       ✅       | set rant limit 10-50  |
|       ✅       | social media btns of devRant    |
|       ✅       | profile (about/website/.../rants)   |
|       ✅       | update (checks for updates)   |
|       ✅       | download (images/avatars)   |
|       ✅       | search (rants/profiles)   |
|       ✅       | surprise me   |
|       ✅       | notifications (amount/tapOnNotifs/SetNotifRead)   |
|       ✅       | open on phone   |
|       ✅       | credits   |

More to come!

## Bugs

login attempt might result in a "bad request" toast and the feed disappearing.
cant figure out why devRant API returns invalid tokens sometimes.

solution: tap "logout" and then login again.

## Download

[Click to download apk](https://github.com/joewilliams007/watchRant/blob/master/app/release/app-release.apk?raw=true)

Or download from here: https://github.com/joewilliams007/watchRant/blob/master/app/release/app-release.apk

Or PlayStore https://play.google.com/store/apps/details?id=com.dev.watchrant

## Installation

### Easy fire tools (no pc needed)

1. Easy fire tools is an android app from the playstore and can connect to your watch.

2. Open the app, navigate to settings/ipAddress and add the ip of your watch, shown in your watches developer settings under debugging (example: 192.168.2.126).

3. Then simply go to the main page and tap the plug icon to connect.

4. Finally, select the apk and hit install.

#### OR use adb wifi from your console to install the apk

#### OR You can build the project yourself and use adb wifi to connect to your watch


## Credit

dfox & trogus for creating devRant

SIMMORSAL for the most awesome animation

Skayo & frogstair for providing api docs
