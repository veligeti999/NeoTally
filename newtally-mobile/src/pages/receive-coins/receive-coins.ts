import {Component, OnInit} from '@angular/core';
import { IonicPage, NavController, NavParams } from 'ionic-angular';
import {QrCode} from "../../model/qrCode.model";

@IonicPage()
@Component({
  selector: 'page-receive-coins',
  templateUrl: 'receive-coins.html',
})
export class ReceiveCoinsPage {
  qrData: QrCode;
  orderTotal: number;
  constructor(public navCtrl: NavController, public navParams: NavParams) {
  }

  ionViewWillEnter() {
    console.log('view will enter');
    this.qrData = this.navParams.get('qrData');
    this.orderTotal = this.navParams.get('orderTotal');
    console.log(this.qrData);
  }
  ionViewDidLoad() {
    console.log('ionViewDidLoad ReceiveCoinsPage');
  }

  sendSMS() {
    this.navCtrl.push('SendSmsPage', {qrData: this.qrData});
  }
}
