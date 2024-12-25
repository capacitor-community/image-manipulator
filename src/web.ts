import { WebPlugin } from '@capacitor/core';

import type { ImageManipulatorPlugin } from './definitions';

export class ImageManipulatorWeb extends WebPlugin implements ImageManipulatorPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
