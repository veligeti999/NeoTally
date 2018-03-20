import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { SendSmsPage } from './send-sms';
import {DirectivesModule} from "../../directives/directives.module";

@NgModule({
  declarations: [
    SendSmsPage,
  ],
  imports: [
    IonicPageModule.forChild(SendSmsPage),
    DirectivesModule
  ],
})
export class SendSmsPageModule {}
