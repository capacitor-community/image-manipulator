import { Component } from '@angular/core';
import { IonCard, IonCardHeader, IonCardContent, IonHeader, IonCardTitle, IonButton, IonToolbar, IonTitle, IonContent } from '@ionic/angular/standalone';

import { ImageManipulator } from '@capacitor-community/image-manipulator';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
  imports: [IonCard, IonCardHeader, IonCardContent, IonCardTitle, IonButton, IonHeader, IonToolbar, IonTitle, IonContent],
})
export class HomePage {
  constructor() { }

  public async echo() {
    const result = await ImageManipulator.echo({ value: 'Hello' });
    console.log('echo', result);
  }
}
