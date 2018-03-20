import { NgModule, ErrorHandler } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { IonicApp, IonicModule, IonicErrorHandler } from 'ionic-angular';
import { MyApp } from './app.component';
import { StatusBar } from '@ionic-native/status-bar';
import { SplashScreen } from '@ionic-native/splash-screen';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { ApiProvider } from '../providers/api/api';
import { AuthProvider } from '../providers/auth/auth';
import { StateProvider } from '../providers/state/state';
import {FormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import { CounterProvider } from '../providers/counter/counter';
import { StorageProvider } from '../providers/storage/storage';
import {IonicStorageModule} from "@ionic/storage";
import {LoaderProvider} from "../providers/loader/loader";
import {AlertProvider} from "../providers/alert/alert";
import {SMS} from "@ionic-native/sms";
import { NotificationsProvider } from '../providers/notifications/notifications';
import {Push} from "@ionic-native/push";
import {Device} from "@ionic-native/device";
import {Network} from "@ionic-native/network";

@NgModule({
  declarations: [
    MyApp,
  ],
  imports: [
    BrowserModule,
    IonicModule.forRoot(MyApp, {
      mode: 'ios',
      backButtonText: ''
    }),
    BrowserAnimationsModule,
    IonicStorageModule.forRoot(),
    FormsModule,
    HttpClientModule,
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
  ],
  providers: [
    StatusBar,
    SplashScreen,
    SMS,
    Push,
    Device,
    Network,
    {provide: ErrorHandler, useClass: IonicErrorHandler},
    ApiProvider,
    AuthProvider,
    StateProvider,
    CounterProvider,
    StorageProvider,
    LoaderProvider,
    AlertProvider,
    NotificationsProvider,
  ]
})
export class AppModule {}
