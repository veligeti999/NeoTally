import { Injectable } from '@angular/core';
import {App, Platform} from "ionic-angular";
import {Push, PushObject} from "@ionic-native/push";
import {API_CONFIG, PUSH_CONFIG} from "../../app/app.config";
import {StorageProvider} from "../storage/storage";
import {ApiProvider} from "../api/api";
import {Device} from "@ionic-native/device";

@Injectable()
export class NotificationsProvider {
  pushObject: PushObject;
  constructor(
    private platform: Platform,
    private storage: StorageProvider,
    private app: App,
    private device: Device,
    private apiProvider: ApiProvider,
    private push: Push) {
  }

  registerPush() {
    if (this.platform.is('cordova')) {
      this.pushObject  = this.push.init(PUSH_CONFIG);
      this.handlePushRegistration();
      this.handleReceivedNotifications();
      this.handlePushError();
    }
  }

  private handleReceivedNotifications() {
    this.pushObject && this.pushObject.on('notification').subscribe((notification) => {
      alert('notification received');
      this.app.getRootNav().getActiveChildNav().select(1);
      // const page = notification.additionalData.page ? notification.additionalData.page : null;
      // const advice_id = notification.additionalData.advice_id ? notification.additionalData.advice_id : null;
      // console.log('notification', notification);
      //
      // if (this.platform.is('ios') && notification.additionalData.foreground) {
      //   // alert('app in foreground');
      //   this.showNotificationToast(notification.message, page, advice_id);
      // } else {
      //   this.onNotificationClicked(page, advice_id);
      // }

    });
  }

  private showNotificationToast(message, page, advice_id) {
    // const toast = this.toastCtrl.create({
    //   message,
    //   position: 'top',
    //   showCloseButton: true,
    //   cssClass: 'notification-toast',
    //   closeButtonText: 'View'
    // });
    //
    // toast.present();
    //
    // toast.onDidDismiss(() => {
    //   this.onNotificationClicked(page, advice_id);
    // });
  }

  private onNotificationClicked(page, advice_id) {
    switch (page) {
      case 'advice_details':
        if(advice_id) {
          this.app.getRootNav().push('AdviceDetailPage', {advice_id});
        }
        break;
      default:
        break;
    }
  }

  private async handlePushRegistration() {
    this.pushObject && this.pushObject.on('registration').subscribe(async (token) => {
      console.log('push token', token);
      await this.storage.savePushToken(token.registrationId);
    });
  }


  private handlePushError() {
    this.pushObject.on('error').subscribe(error => console.error('Error with Push plugin', error));
  }

  registerDevice(registration_key:string, user_id:number) {
    let params: {device_id?:string, device_type?:string, registration_key:string, user_id:number} = {
      device_id: this.device.uuid,
      device_type: this.device.platform,
      registration_key,
      user_id
    };
    console.log('params', params);
    return this.apiProvider.post(API_CONFIG.DEVICE, params).toPromise();
  }

}
