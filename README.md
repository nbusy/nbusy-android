# [![NBusy](http://soygul.com/nbusy/logo.png)](http://nbusy.com/)

[![Build Status](https://travis-ci.org/nbusy/nbusy-android.svg?branch=master)](https://travis-ci.org/nbusy/nbusy-android)

NBusy Android app. Requires Android 5+.

## Download

<a href="https://play.google.com/store/apps/details?id=com.nbusy.app"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" width="200px" /></a>

## Tech Stack

Android client app communicates with:

* [NBusy Server](https://github.com/nbusy/nbusy)
  * WebSockets over TLS for direct browser and mobile connections.
  * JWT middleware for authentication.
  * Google+ middleware for registration and authentication.
  * Built on [Titan Framework](https://github.com/titan-x)

## License

[Apache License 2.0](LICENSE)
