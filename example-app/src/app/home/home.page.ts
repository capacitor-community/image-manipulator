import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { IonCard, IonCardHeader, IonCardContent, IonHeader, IonCardTitle, IonButton, IonToolbar, IonTitle, IonContent } from '@ionic/angular/standalone';


// NATIVE
import { Camera, CameraSource, CameraResultType } from '@capacitor/camera';
import { ImageManipulator } from '@capacitor-community/image-manipulator';
// import { Filesystem, Directory } from '@capacitor/filesystem';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
  imports: [
    IonCard, IonCardHeader, IonCardContent, IonCardTitle, IonButton, IonHeader, IonToolbar, IonTitle, IonContent,
    NgIf
  ],
})
export class HomePage {

  public originalImageWebPath: string | undefined;
  public resizedImageWebPath: string | undefined;

  constructor() { }

  // public async echo() {
  //   const result = await ImageManipulator.echo({ value: 'Hello' });
  //   console.log('echo', result);
  //   alert(result.value);
  // }

  // public async resize() {
  //   const result = await ImageManipulator.resize({ imageUri: 'image.path' });
  //   console.log('result', result);
  //   alert(JSON.stringify(result));
  // }

  public async resize(): Promise<void> {

    const image = await Camera.getPhoto({
      quality: 100,
      allowEditing: false,
      resultType: CameraResultType.Uri,
      saveToGallery: true,
      correctOrientation: true,
      source: CameraSource.Camera
    });

    if (image.path && image.webPath) {

      console.info('image.path', image.path);
      console.info('image.webPath', image.webPath);

      this.originalImageWebPath = image.webPath;

      await new Promise(resolve => setTimeout(resolve, 500));

      const originalImageElement = document.getElementById('originalImage') as HTMLImageElement;
      // Can be set to the src of an image now
      const timestamp = new Date().getTime(); // Generate a unique timestamp
      originalImageElement.src = `${this.originalImageWebPath}?t=${timestamp}`; // Append the timestamp as a query parameter // this.originalImageWebPath;

      const result = await ImageManipulator.resize({ imageUri: image.path });
      console.info('resize result', result);
      this.resizedImageWebPath = result.webPath;

      await new Promise(resolve => setTimeout(resolve, 500));

      const resizedImageElement = document.getElementById('resizedImage') as HTMLImageElement;
      // Can be set to the src of an image now
      // resizedImageElement.src = this.resizedImageWebPath;
      resizedImageElement.src = `${this.resizedImageWebPath}?t=${timestamp}`; // Append the timestamp as a query parameter // this.originalImageWebPath;

    }

  };

}
