import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  IonHeader,
  IonButton, IonToolbar, IonTitle, IonContent,
  IonLabel, IonButtons, IonList, IonItem, IonInput
} from '@ionic/angular/standalone';


// NATIVE
import { Camera, CameraSource, CameraResultType } from '@capacitor/camera';
import { ImageManipulator, ResizeOptions } from '@capacitor-community/image-manipulator';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
  imports: [
    IonButton, IonHeader, IonToolbar, IonTitle, IonContent, IonLabel, IonButtons, IonList, IonItem, IonInput,
    NgIf, FormsModule
  ],
})
export class HomePage {

  public maxWidth: number = 200;
  public maxHeight: number = 300;
  public originalImageWebPath: string | undefined | null;
  public originalImageDimensions: { width: number, height: number } | undefined | null;
  public resizedImageWebPath: string | undefined | null;
  public resizedImageDimensions: { width: number, height: number } | undefined | null;
  public resized: boolean | undefined | null;

  constructor() {
    // this.showDummyImages();
  }

  // NOTE: this is to test UI in the browser
  // public async showDummyImages(): Promise<void> {
  //   this.originalImageWebPath = 'https://picsum.photos/1080/1920';
  //   await new Promise(resolve => setTimeout(resolve, 250));
  //   this.originalImageDimensions = { width: 1920, height: 1080 };
  //   const timestamp = new Date().getTime(); // NOTE: Generate a unique timestamp
  //   const originalImageElement = document.getElementById('originalImage') as HTMLImageElement;
  //   originalImageElement.src = `https://picsum.photos/1080/1920?t=${timestamp}`;

  //   this.resizedImageWebPath = 'https://picsum.photos/200/300';
  //   await new Promise(resolve => setTimeout(resolve, 250));
  //   this.resizedImageDimensions = { width: 300, height: 200 };
  //   const timestamp2 = new Date().getTime(); // NOTE: Generate a unique timestamp
  //   const resizedImageElement = document.getElementById('resizedImage') as HTMLImageElement;
  //   resizedImageElement.src = `https://picsum.photos/200/300?t=${timestamp2}`;

  //   this.resized = true;
  // }

  public async getDimensions(): Promise<void> {

    const photo = await Camera.getPhoto({
      quality: 100,
      allowEditing: false,
      resultType: CameraResultType.Uri,
      saveToGallery: true,
      correctOrientation: true,
      source: CameraSource.Prompt
    });

    if (photo.path && photo.webPath) {

      this.originalImageWebPath = photo.webPath;
      try {
        this.originalImageDimensions = await ImageManipulator.getDimensions({ imagePath: photo.path });

        const originalImageElement = document.getElementById('originalImage') as HTMLImageElement;
        // NOTE: Can be set to the src of an image now
        const timestamp = new Date().getTime(); // NOTE: Generate a unique timestamp
        originalImageElement.src = `${this.originalImageWebPath}?t=${timestamp}`; // NOTE: Append the timestamp as a query parameter

        this.resizedImageWebPath = null;
        this.resizedImageDimensions = null;
        this.resized = null;
      } catch (error) {
        this.resizedImageWebPath = null;
        this.resizedImageDimensions = null;
        this.resized = null;
        console.error('Error getting image dimensions:', error);
      }
    }

  };

  public async resize(): Promise<void> {

    const image = await Camera.getPhoto({
      quality: 100,
      allowEditing: false,
      resultType: CameraResultType.Uri,
      saveToGallery: true,
      correctOrientation: true,
      source: CameraSource.Prompt
    });

    if (image.path && image.webPath) {

      this.originalImageWebPath = image.webPath;
      await new Promise(resolve => setTimeout(resolve, 250));

      const originalImageElement = document.getElementById('originalImage') as HTMLImageElement;
      // NOTE: Can be set to the src of an image now
      const timestamp = new Date().getTime(); // NOTE: Generate a unique timestamp
      originalImageElement.src = `${this.originalImageWebPath}?t=${timestamp}`; // NOTE: Append the timestamp as a query parameter

      const resizeOptions: ResizeOptions = {
        imagePath: image.path,
        quality: 100,
        fixRotation: false, // true,
        fileName: `ResizedImage_${new Date().getTime()}`,
        folderName: 'MyResizedImages'
      };
      if (this.maxWidth && isNaN(this.maxWidth) === false) {
        resizeOptions.maxWidth = this.maxWidth;
      }
      if (this.maxHeight && isNaN(this.maxHeight) === false) {
        resizeOptions.maxHeight = this.maxHeight;
      }

      try {
        const resizeResult = await ImageManipulator.resize(resizeOptions);
        this.resizedImageWebPath = resizeResult.webPath;

        await new Promise(resolve => setTimeout(resolve, 250));
        const resizedImageElement = document.getElementById('resizedImage') as HTMLImageElement;
        // NOTE: Can be set to the src of an image now
        resizedImageElement.src = `${this.resizedImageWebPath}?t=${timestamp}`; // NOTE: Append the timestamp as a query parameter

        this.originalImageDimensions = { width: resizeResult?.originalWidth, height: resizeResult?.originalHeight };
        this.resizedImageDimensions = { width: resizeResult?.resizedWidth, height: resizeResult?.resizedHeight };
        this.resized = resizeResult.resized;
      } catch (error) {
        this.resizedImageWebPath = null;
        this.resizedImageDimensions = null;
        this.resized = null;
        console.error('Error resizing image:', error);
      }

    }

  };

}
