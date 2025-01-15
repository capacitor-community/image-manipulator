import { WebPlugin } from '@capacitor/core';

import type { ImageManipulatorPlugin } from './definitions';

export class ImageManipulatorWeb extends WebPlugin implements ImageManipulatorPlugin {
  public async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  public async resize(_options: { imageUri: string }): Promise<{
    originalWidth: number, originalHeight: number,
    finalWidth: number, finalHeight: number,
    imagePath: string, webPath: string,
    resized: boolean
  }> {
    throw new Error('This method is not available on the web platform.');
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  // public async resize(_options: { imageUri: string }): Promise<{ value: string }> {
  //   throw new Error('This method is not available on the web platform.');
  // }

}
