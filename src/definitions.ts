export interface GetDimensionsOptions {

  /**
   * The path to the image to get its dimensions.
   * 
   * @since 6.0.0
   */
  imagePath: string;

}

export interface ResizeOptions {

  /**
   * The path to the image to resize.
   * 
   * @since 6.0.0
   */
  imagePath: string;

  /**
   * (Android Only) The name of the folder to store the resized images (optional, defaults to 'ResizedImages' if not provided).
   * 
   * @since 6.0.0
   */
  folderName?: string;

  /**
   * The name of the resized file without extension (optional, timestamp as name if not provided).
   * 
   * @since 6.0.0
   */
  fileName?: string;

  /**
   * The resized image quality from 0 to 100, where 100 is max (optional, defaults to 85 if not provided).
   * 
   * @since 6.0.0
   */
  quality?: number;

  /**
   * The max width of the resized image (optional, but at least either height or width must be provided).
   * 
   * @since 6.0.0
   */
  maxWidth?: number;

  /**
   * The max height of the resized image (optional, but at least either width or height must be provided).
   * 
   * @since 6.0.0
   */
  maxHeight?: number;

  /**
   * Fix the rotation of the image based on EXIF metadata (optional, defaults to false if not provided).
   * 
   * @since 6.0.0
   */
  fixRotation?: boolean;

}

export interface ImageManipulatorPlugin {

  /**
   * Get dimensions of an image (width and height)
   * 
   * @since 6.0.0
   * @param options options to get dimensions of an image
   * @returns Dimensions of the image (with and height)
   */
  getDimensions(options: GetDimensionsOptions): Promise<{
    width: number, height: number
  }>;

  /**
   * Method to resize an image based on the provided options and return the resized image details. Please note that the resized
   * image will respect the aspect ratio of the original image and will be resized to be equal to less of the provided maxWidth or maxHeight.
   * If the image both width and height are less than the provided maxWidth and maxHeight, the image will not be resized
   * and the original image details will be returned with resized as false.
   * If either maxWidth or maxHeight is not provided or 0, this parameter will be ignored (not used) and the image will be
   * resized based on the provided maxWidth or maxHeight, accordingly.
   * Please note that either maxWidth or maxHeight must be provided to resize an image.
   *
   * @since 6.0.0 
   * @param options Options to resize an image
   * @returns Resized image details (original width, original height, resized width, resized height, image path, web path, resized)
   */
  resize(options: ResizeOptions): Promise<{
    originalWidth: number, originalHeight: number,
    resizedWidth: number, resizedHeight: number,
    imagePath: string, webPath: string,
    resized: boolean
  }>;

}
