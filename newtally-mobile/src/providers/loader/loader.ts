import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {LoadingController, LoadingOptions} from "ionic-angular";

@Injectable()
export class LoaderProvider {
  loading;

  constructor(
    public loadingCtrl: LoadingController,
  ) {

  }

  show(content?: string) {
    const options: LoadingOptions = {
      dismissOnPageChange: true,
    };

    if (content) {
      options.content = content;
    }
    this.loading = this.loadingCtrl.create(options);
    this.loading.present();
  }
  hide() {
    try {
      this.loading.dismiss();
    } catch (error) { }
  }
  autoHide(time) {
    this.loading = this.loadingCtrl.create({
      duration: time
    });
    this.loading.present();
  }

}
