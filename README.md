# FCM Channel Android

[![](https://jitpack.io/v/push-flow/fcm-channel-android.svg?style=flat-square)](https://jitpack.io/#push-flow/fcm-channel-android)

FCM Channel Android is a client library for [Push](http://push.al) platform that can be used inside Android apps to enable users receive and send messages through Firebase Cloud Messaging channel.

## Setup

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
    implementation 'com.github.push-flow.fcm-channel-android:fcm-channel:LATEST-VERSION'
  }

  apply plugin: 'com.google.gms.google-services'
```

Step 3: Register activities in your manifest file
```
  <application ...>
  ...
  <!-- If you will use library activity -->
  <activity android:name="io.fcmchannel.sdk.chat.FcmClientChatActivity" />

  <!-- Due to messages with media attachments -->
  <activity
    android:name="br.com.ilhasoft.support.media.view.MediaViewActivity"
    android:theme="@style/Theme.AppCompat.NoActionBar" />
  ...
  </application>
```

Step 4: Setup the client

```
// chat UI customization
ChatUiConfiguration chatUiConfiguration = new ChatUiConfiguration()
  // chat background
  .setChatBackground(drawableRes)
  // send message button color
  .setSendMessageIconColor(colorInt)
  // background color of outgoing messages
  .setSentMessageBackgroundColor(colorInt)
  // 9-patch background of outgoing messages
  .setSentMessageBackground(drawableRes)
  // text color of outgoing messages
  .setSentMessageTextColor(colorInt)
  // hour text color of outgoing messages
  .setSentMessageHourTextColor(colorInt)
  // balloon indicator of outgoing messages for top side
  .setSentMessageInTopDirection(boolean)
  // icon of incoming messages bot
  .setReceivedMessageIcon(drawableRes)
  // background color of incoming messages
  .setReceivedMessageBackgroundColor(colorInt)
  // 9-patch background of incoming messages
  .setReceivedMessageBackground(drawableRes)
  // text color of incoming messages
  .setReceivedMessageTextColor(colorInt)
  // hour text color of incoming messages
  .setReceivedMessageHourTextColor(colorInt)
  // balloon indicator of incoming messages for top side
  .setReceivedMessageInTopDirection(boolean)
  // color of progress dialog on messages list loading
  .setMessagesLoadingColor(colorInt)
  // background color of quick replies
  .setMetadataBackgroundColor(colorInt);
  // background of quick replies
  .setMetadataBackground(drawableRes);

UiConfiguration uiConfiguration = new UiConfiguration()
  // theme of FcmClient activity
  .setTheme(styleRes)
  // color of FcmClient activity toolbar
  .setToolbarColor(styleRes)
  // icon of FcmClient activity back indicator
  .setBackResource(drawableRes)
  // enable floating chat
  .setFloatingChatEnabled(boolean)
  // message of floating chat permission
  .setPermissionMessage(string)
  // icon of floating chat bubble
  .setFloatingChatIcon(drawableRes)
  // title text of floating chat window
  .setTitleString(string)
  // title color of floating chat window
  .setTitleColor(colorInt)
  // chat UI customization
  .setChatUiConfiguration(chatUiConfiguration);

FcmClient.Builder builder = new FcmClient.Builder(appContext)
  // host URL
  .setHost(string)
  // authorization token
  .setToken(string)
  // channel UUID
  .setChannel(string)
  // registration service class, we can perform some operation after contact register by overriding onFcmRegistered(fcmToken, contact) on child class
  .setRegistrationServiceClass(FcmClientRegistrationService.class)
  // UI configuration
  .setUiConfiguration(uiConfiguration)
  // enable safe mode to not perform operations involving the authorization token, which must be not set
  .setSafeModeEnabled(true);

FcmClient.initialize(builder);
```

Step 5: Register services in your manifest

```
  <application ...>
    ...
    <!-- The FcmClientRegistrationIntentService or a child of it to contact register -->
    <service
      android:name="io.fcmchannel.sdk.services.FcmClientRegistrationIntentService"
      android:exported="false" />

    <!-- The FcmClientIntentService or a child of it to receive messages, with a child we can change notification icon, for example -->
    <service
      android:name="io.fcmchannel.sdk.services.FcmClientIntentService"
      android:exported="false">
      <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
      </intent-filter>
    </service>

    <!-- To floating chat, if is enabled -->
    <service
      android:name="io.fcmchannel.sdk.chat.menu.FcmClientMenuService"
      android:exported="false" />
    ...
  </application>
```

Latest Version: [![](https://jitpack.io/v/push-flow/fcm-channel-android.svg?style=flat-square)](https://jitpack.io/#push-flow/fcm-channel-android)

## Features:

* Interactive messages activity that handles the sending and receiving of messages;
* Notifications of new messages by using Firebase Cloud Messaging;
* Works well with Right-to-left languages;
* Floating chat when users aren't with your app open;

## Sample

<img src="sample.gif" alt="...">
