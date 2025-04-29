# cordova-plugin-video-viewer

[![npm](https://img.shields.io/github/package-json/v/cesarcervantesb/cordova-plugin-video-viewer
)](https://github.com/cesarcervantesb/cordova-plugin-video-viewer/)
![License](https://img.shields.io/github/license/cesarcervantesb/cordova-plugin-video-viewer?color=orange)

This plugin allows to show a video from a file path or URL on new window.

## Installation

Install plugin from npm:

```
npm i cordova-plugin-video-viewer
```

Or install the latest master version from GitHub:

```
cordova plugin add https://github.com/cesarcervantesb/cordova-plugin-video-viewer
```

## Supported Platforms

- Android
- iOS

## Usage

The plugin creates the object cordova.plugins.VideoViewer and is accessible after the deviceready event has been fired.

```js
document.addEventListener('deviceready', function () {
    // cordova.plugins.VideoViewer is now available
}, false);
```

## Available methods

- `show` - show a video from a file path or URL.
```js
VideoViewer.show(successCallback, errorCallback, options);
```

OR

```js
cordova.plugins.VideoViewer.show(successCallback, errorCallback, options);
```

### Parameters

| Parameter        | Type       | Default | Description                                                   |
| ---------------- | ---------- | ------- | ------------------------------------------------------------- |
| `success` | `Function` |         | Success callback              |
| `error`   | `Function` |         | Error callback. |
| `options`   | `Object` |         | Required. Attibutes. |

All available `options` attributes:

| Attribute                      | Type     | Default                                                      | Comment                                        |
| ------------------------------ | -------- | ------------------------------------------------------------ | -------------------------------------------------- |
| `src`  | `String` |  | Required. Video source. Support `file://` , `content://` , `http://` or `https://` (Android) . Only `file://`  (iOS) |
| `title` | `String` |  | Optional. Custom video title. If omit take the name of source as title. Exmple `file://path/to/my/custom/video.mp4` title is `video.mp4`|
| `share` | `Boolean` | true | Optional. Show share button (Only for Android). |

## Example

```js
const options = {
    src: "file://path/to/my/custom/video.mp4",
    title: "My Custom Video",
    share: false     // only for Android
};
VideoViewer.show(function (){
    // Success callback 
    console.log("OK");
}, function (error) {
    // Error callback
    console.log(error.message);
}, options);
```