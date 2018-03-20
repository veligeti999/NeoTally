import { Component } from '@angular/core';
import {Events, Platform} from 'ionic-angular';
import { StatusBar } from '@ionic-native/status-bar';
import { SplashScreen } from '@ionic-native/splash-screen';
import {StorageProvider} from "../providers/storage/storage";
import {StateProvider} from "../providers/state/state";
import {NotificationsProvider} from "../providers/notifications/notifications";


@Component({
  templateUrl: 'app.html'
})
export class MyApp {
  rootPage:any;

  constructor(platform: Platform, statusBar: StatusBar, splashScreen: SplashScreen, private storage: StorageProvider, private state: StateProvider, private notifications: NotificationsProvider, private events: Events) {
    this.events.subscribe('network:unauthorized', async () => {
      await this.storage.removeCounter();
      this.rootPage = 'LoginPage';
    });
    this.checkAuthStatus();
    platform.ready().then(() => {
      // Okay, so the platform is ready and our plugins are available.
      // Here you can do any higher level native things you might need.
      statusBar.styleDefault();
      splashScreen.hide();
      this.notifications.registerPush();
    });
  }

  checkAuthStatus() {
    this.storage.getCounter().then((counter) => {
      console.log('app component counter', counter);
      if (counter) {
        console.log('counter present', counter);
        this.state.currentCounter = counter;
        this.rootPage = 'TabsPage';
        // this.rootPage = 'SendSmsPage';

      } else {
        this.rootPage = 'LoginPage';
      }
    }, (error) => {
      console.log(error);
    })
  }
}
