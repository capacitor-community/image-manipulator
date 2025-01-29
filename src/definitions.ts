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
   * The resized image quality (optional, defaults to 85 if not provided).
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
   * Method to resize an image
   * If the image width and height are less than the provided maxWidth and maxHeight, the image will not be resized.
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
