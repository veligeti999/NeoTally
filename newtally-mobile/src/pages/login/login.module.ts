import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { LoginPage } from './login';
import {TooltipsModule} from "ionic-tooltips";
import {FormsModule} from "@angular/forms";

@NgModule({
  declarations: [
    LoginPage,
  ],
  imports: [
    IonicPageModule.forChild(LoginPage),
    TooltipsModule,
  ],
})
export class LoginPageModule {}
