import { registerPlugin } from '@capacitor/core';

import type { ImageManipulatorPlugin } from './definitions';

const ImageManipulator = registerPlugin<ImageManipulatorPlugin>('ImageManipulator', {
  web: () => import('./web').then((m) => new m.ImageManipulatorWeb()),
});

export * from './definitions';
export { ImageManipulator };
