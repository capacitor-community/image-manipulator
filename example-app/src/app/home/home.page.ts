import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { IonCard, IonCardHeader, IonCardContent, IonHeader, IonCardTitle, IonButton, IonToolbar, IonTitle, IonContent } from '@ionic/angular/standalone';

// NATIVE
import { Camera, CameraResultType } from '@capacitor/camera';
import { ImageManipulator } from '@capacitor-community/image-manipulator';

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

  public async echo() {
    const result = await ImageManipulator.echo({ value: 'Hello' });
    console.log('echo', result);
    alert(result.value);
  }

  public async takePicture(): Promise<void> {

    const image = await Camera.getPhoto({
      quality: 100,
      allowEditing: false,
      resultType: CameraResultType.Uri
    });

    // image.webPath will contain a path that can be set as an image src.
    // You can access the original file using image.path, which can be
    // passed to the Filesystem API to read the raw data of the image,
    // if desired (or pass resultType: CameraResultType.Base64 to getPhoto)


    if (image.path && image.webPath) {

      console.info('image.path', image.path);
      console.info('image.webPath', image.webPath);

      this.originalImageWebPath = image.webPath;

      await new Promise(resolve => setTimeout(resolve, 500));

      const originalImageElement = document.getElementById('originalImage') as HTMLImageElement;
      // Can be set to the src of an image now
      originalImageElement.src = this.originalImageWebPath;


      // const result = await ImageManipulator.resize({ imageUri: image.path });
      // console.info('resize result', result);
      // this.resizedImageWebPath = result.webPath;

      // const reziedImageElement = document.getElementById('resizedImage') as HTMLImageElement;
      // // Can be set to the src of an image now
      // reziedImageElement.src = this.resizedImageWebPath;

    }

  };

}
