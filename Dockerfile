FROM java:8

ENV DEBIAN_FRONTEND noninteractive
ENV ADB_INSTALL_TIMEOUT 8 # minutes (2 minutes by default)

# todo: be more strict about versions (i.e. android-23-2 etc.)
ENV ANDROID_SDK_URL http://dl.google.com/android/android-sdk_r24.4.1-linux.tgz

# Android SDK Tools (2), BuildTools version used to build the project(1), SDK version used to compile the project (1)
ENV ANDROID_COMPONENTS platform-tools,tools,build-tools-23.0.2,android-23
ENV GOOGLE_COMPONENTS extra-android-support,extra-google-google_play_services,extra-google-m2repository,extra-android-m2repository,addon-google_apis-google-23

# System images used to run emulators for tests
ENV EMULATORS sys-img-armeabi-v7a-addon-google_apis-google-23

ENV ANDROID_HOME /usr/local/android-sdk-linux
ENV ANDROID_SDK /usr/local/android-sdk-linux
ENV PATH ${ANDROID_HOME}/tools:$ANDROID_HOME/platform-tools:$PATH

# Install dependencies
RUN dpkg --add-architecture i386 && \
    apt-get update && \
    apt-get install -yq libc6:i386 libstdc++6:i386 zlib1g:i386 libncurses5:i386 --no-install-recommends && \
    apt-get clean

# Download and untar SDK
RUN curl -L "${ANDROID_SDK_URL}" | tar --no-same-owner -xz -C /usr/local

# Install Android SDK components
RUN echo y | android update sdk --no-ui --all --filter "${ANDROID_COMPONENTS,GOOGLE_COMPONENTS,EMULATORS}"

# add project files and
# ./gradlew build connectedCheck
