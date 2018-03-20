import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { SmsConfirmationPage } from './sms-confirmation';

@NgModule({
  declarations: [
    SmsConfirmationPage,
  ],
  imports: [
    IonicPageModule.forChild(SmsConfirmationPage),
  ],
})
export class SmsConfirmationPageModule {}
