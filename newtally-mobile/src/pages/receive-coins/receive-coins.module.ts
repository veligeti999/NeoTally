import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { ReceiveCoinsPage } from './receive-coins';

@NgModule({
  declarations: [
    ReceiveCoinsPage,
  ],
  imports: [
    IonicPageModule.forChild(ReceiveCoinsPage),
  ],
})
export class ReceiveCoinsPageModule {}
