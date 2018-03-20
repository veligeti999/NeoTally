import { Component } from '@angular/core';
import {IonicPage, ModalController, NavController, NavParams} from 'ionic-angular';
import {NgForm} from "@angular/forms";
import {CounterProvider} from "../../providers/counter/counter";
import {LoaderProvider} from "../../providers/loader/loader";
import {OrderSubmitParams} from "../../model/submitOrder.params";
import {QrCode} from "../../model/qrCode.model";
import {SMS} from "@ionic-native/sms";
import {StateProvider} from "../../providers/state/state";

@IonicPage()
@Component({
  selector: 'page-send-sms',
  templateUrl: 'send-sms.html',
})
export class SendSmsPage {
  qrData: QrCode;
  constructor(
    public navCtrl: NavController,
    private sms: SMS,
    private state: StateProvider,
    public navParams: NavParams) {
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad SendSmsPage');
    this.qrData = this.navParams.get('qrData');
  }

  async send(form: NgForm) {
    if (!form.invalid) {
      const body = `Dear Customer, thanks for the purchase. 
Please transfer the amount to the address : ${this.qrData.walletAddress} . Regards, ${this.state.currentCounter.merchant_name}`;
      try {
        const sms = await this.sms.send(form.value.number, body, {android: {intent: 'INTENT'}});
        if (sms) {
          this.navCtrl.push('SmsConfirmationPage', {mobile: form.value.number})
        }
      } catch (error) {
        console.log(error);
      }
    }
  }
}
