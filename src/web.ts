import { WebPlugin } from '@capacitor/core';

import type { ImageManipulatorPlugin } from './definitions';

export class ImageManipulatorWeb extends WebPlugin implements ImageManipulatorPlugin {
  public async getDimensions(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    _options: {
      imagePath: string;
    },
  ): Promise<{
    width: number;
    height: number;
  }> {
    throw new Error('This method is not available on the web platform.');
  }

  public async resize(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    _options: {
      imagePath: string;
      folderName?: string;
      fileName?: string;
      maxWidth?: number;
      maxHeight?: number;
      quality?: number;
      fixRotation?: boolean;
    },
  ): Promise<{
    originalWidth: number;
    originalHeight: number;
    resizedWidth: number;
    resizedHeight: number;
    imagePath: string;
    webPath: string;
    resized: boolean;
  }> {
    throw new Error('This method is not available on the web platform.');
  }
}
