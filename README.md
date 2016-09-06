# NBusy Android
[![Build Status](https://travis-ci.org/nbusy/nbusy-android.svg?branch=master)](https://travis-ci.org/nbusy/nbusy-android)

NBusy Android app. Requires Android 5.1 or higher.

## Download
<a href="https://play.google.com/store/apps/details?id=com.nbusy.app"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" width="200px" /></a>

## Tech Stack
Android client app communicates with:

* [NBusy Server](https://github.com/nbusy/nbusy) which is built on [Titan Framework](https://github.com/titan-x).
* WebSockets over TLS for direct browser and mobile connections.
* JWT middleware for authentication.
* Google+ middleware for registration and authentication.

## Docker
To build and run the build in a Docker container:

```bash
docker build -t nbusy-android .
docker run --name nbusy-android --rm nbusy-android
```

The --name flag gives our container a predictable name to make it easier to work with.The --rm flag tells docker to remove the container image when the build is done.

Once you're done, shut down the running container from another terminal window:

```bash
docker stop nbusy-android
```

If something goes wrong and you want to stop all containers and delete all containers & images:

```bash
# stop then delete all containers
docker stop $(docker ps -a -q)
docker rm -f $(docker ps -a -q)
# delete all images
docker rmi -f $(docker images -q)
```

## Updating Gradle Wrapper
Run the following command 2 times in a row:

```bash
./gradlew wrapper --gradle-distribution-url https://services.gradle.org/distributions/gradle-2.13-all.zip
```

and make sure that following files are updated:

```
gradle/wrapper/gradle-wrapper.jar
gradle/wrapper/gradle-wrapper.properties
gradlew
gradlew.bat
```

## Troubleshooting
1. At the repo root: `git clean -xdf`.
2. Android Studio: `Clean Project` then `Rebuild Project`.
3. Emulator: wipe user cache or recreate new image and start it manually.
4. Real device: Restart the device and disable then enable USB debugging.

## License
[Apache License 2.0](LICENSE)
