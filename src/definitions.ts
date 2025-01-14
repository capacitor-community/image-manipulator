export interface ImageManipulatorPlugin {
  
  echo(options: { value: string }): Promise<{ value: string }>;
  
  // resize(options: { imageUri: string }): Promise<{
  //   originalWidth: number, originalHeight: number,
  //   finalWidth: number, finalHeight: number,
  //   imagePath: string, webPath: string,
  //   resized: boolean
  // }>;

}
