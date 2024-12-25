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

This plugins allows to resize images.
<br>

**Features:**

- supports resizing image
- supports Android and iOS platforms

**NOTE**: The plugin version 1.0.0 is compatible with Capacitor 5 which requires gradle version 8.0

## Plugin versions

| Capacitor version | Plugin version |
| ----------------- | -------------- |
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

* [`echo(...)`](#echo)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------

</docgen-api>

## Usage

### Resize image

```
import { ImageManipulator } from '@capacitor-community/image-manipulator';

const options: ImageResizeOptions = {};
await ImageManipulator.resize(options);
```
