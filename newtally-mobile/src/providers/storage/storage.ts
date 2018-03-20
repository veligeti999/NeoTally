import { Injectable } from '@angular/core';
import {Storage} from "@ionic/storage";
import {Platform} from "ionic-angular";
import {Counter} from "../../model/counter.model";
@Injectable()
export class StorageProvider {

  constructor(private storage: Storage, private platform: Platform) {
    console.log('Hello StorageProvider Provider');
  }


  async saveCounter(counter: Counter) {
    await this.platform.ready();
    await this.storage.ready();
    await this.storage.set('counter', counter);
  }

  async getCounter() {
    await this.platform.ready();
    await this.storage.ready();
    return await this.storage.get('counter');
  }

  async removeCounter() {
    await this.platform.ready();
    await this.storage.ready();
    return await this.storage.remove('counter');
  }

  async savePushToken(token: string) {
    await this.storage.ready();
    return await this.storage.set('push_token', token);
  }

  async getPushToken() {
    await this.platform.ready();
    await this.storage.ready();
    return await this.storage.get('push_token');
  }
}
