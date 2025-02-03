<p align="center"><br><img src="https://user-images.githubusercontent.com/236501/85893648-1c92e880-b7a8-11ea-926d-95355b8175c7.png" width="128" height="128" /></p>
<h3 align="center">Image Manipulator Plugin</h3>
<p align="center"><strong><code>@capacitor-community/image-manipulator</code></strong></p>
<p align="center">
  Capacitor community plugin to manipulate images (resize, compress, crop etc.)
</p>

<p align="center">
  <img src="https://img.shields.io/maintenance/yes/2024?style=flat-square" />
  <a href="https://www.npmjs.com/package/@capacitor-community/image-manipulator"><img src="https://img.shields.io/npm/l/@capacitor-community/image-manipulator?style=flat-square" /></a>
  <br>
  <a href="https://www.npmjs.com/package/@capacitor-community/image-manipulator"><img src="https://img.shields.io/npm/dw/@capacitor-community/image-manipulator?style=flat-square" /></a>
  <a href="https://www.npmjs.com/package/@capacitor-community/image-manipulator"><img src="https://img.shields.io/npm/v/@capacitor-community/image-manipulator?style=flat-square" /></a>
  <!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
  <a href="#contributors-"><img src="https://img.shields.io/badge/all%20contributors-1-orange?style=flat-square" /></a>
  <!-- ALL-CONTRIBUTORS-BADGE:END -->
</p>

## Table of Contents

- [Maintainers](#maintainers)
- [About](#about)
- [Plugin versions](#plugin-versions)
- [Supported Platforms](#supported-platforms)
- [Install](#install)
- [API](#api)
- [Troubleshooting](#troubleshooting)

## Maintainers

| Maintainer | GitHub                          | Active |
| ---------- | ------------------------------- | ------ |
| ryaa       | [ryaa](https://github.com/ryaa) | yes    |

## About

This capcitor plugin allows reading image dimensions (width and height) and resize images.
This plugin is inspired and similar to [cordova-plugin-image-resizer](https://github.com/JoschkaSchulz/cordova-plugin-image-resizer) plugin. Please note that it does not depend on **cordova-plugin-camera** plugin and can be used independently.

<br>

**Features:**

- supports getting image dimensions
- supports resizing image
- supports Android and iOS platforms

**NOTE**: The plugin version 7.0.0 is compatible with Capacitor 7

## Plugin versions

| Capacitor version | Plugin version |
| ----------------- | -------------- |
| 7.x               | 7.x            |
| 6.x               | 6.x            |

## Supported Platforms

- iOS
- Android

## Install

```bash
npm install @capacitor-community/image-manipulator
npx cap sync
```

## API

<docgen-index>

* [`getDimensions(...)`](#getdimensions)
* [`resize(...)`](#resize)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getDimensions(...)

```typescript
getDimensions(options: GetDimensionsOptions) => Promise<{ width: number; height: number; }>
```

Get dimensions of an image (width and height)

| Param         | Type                                                                  | Description                           |
| ------------- | --------------------------------------------------------------------- | ------------------------------------- |
| **`options`** | <code><a href="#getdimensionsoptions">GetDimensionsOptions</a></code> | options to get dimensions of an image |

**Returns:** <code>Promise&lt;{ width: number; height: number; }&gt;</code>

**Since:** 6.0.0

--------------------


### resize(...)

```typescript
resize(options: ResizeOptions) => Promise<{ originalWidth: number; originalHeight: number; resizedWidth: number; resizedHeight: number; imagePath: string; webPath: string; resized: boolean; }>
```

Method to resize an image based on the provided options and return the resized image details. Please note that the resized
image will respect the aspect ratio of the original image and will be resized to be equal to less of the provided maxWidth or maxHeight.
If the image both width and height are less than the provided maxWidth and maxHeight, the image will not be resized
and the original image details will be returned with resized as false.
If either maxWidth or maxHeight is not provided or 0, this parameter will be ignored (not used) and the image will be
resized based on the provided maxWidth or maxHeight, accordingly.
Please note that either maxWidth or maxHeight must be provided to resize an image.

| Param         | Type                                                    | Description                |
| ------------- | ------------------------------------------------------- | -------------------------- |
| **`options`** | <code><a href="#resizeoptions">ResizeOptions</a></code> | Options to resize an image |

**Returns:** <code>Promise&lt;{ originalWidth: number; originalHeight: number; resizedWidth: number; resizedHeight: number; imagePath: string; webPath: string; resized: boolean; }&gt;</code>

**Since:** 6.0.0

--------------------


### Interfaces


#### GetDimensionsOptions

| Prop            | Type                | Description                                  | Since |
| --------------- | ------------------- | -------------------------------------------- | ----- |
| **`imagePath`** | <code>string</code> | The path to the image to get its dimensions. | 6.0.0 |


#### ResizeOptions

| Prop              | Type                 | Description                                                                                                                | Since |
| ----------------- | -------------------- | -------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`imagePath`**   | <code>string</code>  | The path to the image to resize.                                                                                           | 6.0.0 |
| **`folderName`**  | <code>string</code>  | (Android Only) The name of the folder to store the resized images (optional, defaults to 'ResizedImages' if not provided). | 6.0.0 |
| **`fileName`**    | <code>string</code>  | The name of the resized file without extension (optional, timestamp as name if not provided).                              | 6.0.0 |
| **`quality`**     | <code>number</code>  | The resized image quality from 0 to 100, where 100 is max (optional, defaults to 85 if not provided).                      | 6.0.0 |
| **`maxWidth`**    | <code>number</code>  | The max width of the resized image (optional, but at least either height or width must be provided).                       | 6.0.0 |
| **`maxHeight`**   | <code>number</code>  | The max height of the resized image (optional, but at least either width or height must be provided).                      | 6.0.0 |
| **`fixRotation`** | <code>boolean</code> | Fix the rotation of the image based on EXIF metadata (optional, defaults to false if not provided).                        | 6.0.0 |

</docgen-api>

## Usage

Please also see **example-app** for a complete example.

### Resize image

```
import { ImageManipulator } from '@capacitor-community/image-manipulator';

const options: ImageResizeOptions = {
  imagePath: 'path/to/image.jpg',
  maxWidth: 300,
  maxHeight: 300,
  quality: 85,
  folderName: 'ResizedImages',
  fileName: 'resized',
  fixRotation: true
};
const result = await ImageManipulator.resize(options);
```

### Resize image

```
import { ImageManipulator } from '@capacitor-community/image-manipulator';

const options: GetDimensionsOptions = {
  imagePath: 'path/to/image.jpg'
};
const result = await ImageManipulator.getDimensions(options);
```
