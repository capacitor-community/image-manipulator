export interface ImageManipulatorPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
