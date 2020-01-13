# FCM Channel Android

[![](https://jitpack.io/v/push-flow/fcm-channel-android.svg?style=flat-square)](https://jitpack.io/#push-flow/fcm-channel-android)

FCM Channel Android is a client library for [Push](http://push.al) platform that can be used inside Android apps to enable users receive and send messages through Firebase Cloud Messaging channel.

## Download

Step 1: Add it in your root build.gradle at the end of repositories:

```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

Step 2: Add the dependency and GMS plugin to your app build.gradle
```
  dependencies {
    compile 'com.github.push-flow.fcm-channel-android:fcm-channel:LATEST-VERSION'
  }

  apply plugin: 'com.google.gms.google-services'
```

Step 3: Register media view activity due to messages with media attachments, exactly that way:
```
  <activity
    android:name="br.com.ilhasoft.support.media.view.MediaViewActivity"
    android:theme="@style/Theme.AppCompat.NoActionBar" />
```

Latest Version: [![](https://jitpack.io/v/push-flow/fcm-channel-android.svg?style=flat-square)](https://jitpack.io/#push-flow/fcm-channel-android)

## Features:

* Interactive messages activity that handles the sending and receiving of messages;
* Notifications of new messages by using Firebase Cloud Messaging;
* Works well with Right-to-left languages;
* Floating chat when users aren't with your app open;

## Sample

<img src="sample.gif" alt="...">
