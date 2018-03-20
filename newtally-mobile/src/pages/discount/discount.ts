import { Component } from '@angular/core';
import { IonicPage, NavController, NavParams } from 'ionic-angular';
import {Discount} from "../../model/discount.model";
import {CounterProvider} from "../../providers/counter/counter";
import {QrCodeParams} from "../../model/qrcode.params";
import {StateProvider} from "../../providers/state/state";
import {LoaderProvider} from "../../providers/loader/loader";

/**
 * Generated class for the DiscountPage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@IonicPage()
@Component({
  selector: 'page-discount',
  templateUrl: 'discount.html',
})
export class DiscountPage {
  discounts: Discount[];
  orderTotal: number;

  constructor(
    public navCtrl: NavController,
    private counterProvider: CounterProvider,
    private loader: LoaderProvider,
    public state: StateProvider,
    public navParams: NavParams) {
  }

  ionViewWillEnter() {
    console.log('view will enter');
    this.discounts = this.navParams.get('discounts').currency_discounts;
    this.orderTotal = this.navParams.get('discounts').payment_amount;
    console.log('discounts', this.discounts);
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad DiscountPage');
  }

  async receiveCoins(discount: Discount, orderTotal) {
    const qrCodeParams: QrCodeParams = {
      counter_id: this.state.currentCounter.counter_id,
      currency_code: discount.currency_code,
      currency_amount: discount.currency_amount,
      discount_amount: discount.discount_amount,
      currency_id: discount.currency_id,
      payment_amount: this.orderTotal
    };
    this.loader.show();
    try {
      const qrData = await this.counterProvider.getQrCode(qrCodeParams);
      console.log('qrData', qrData);
      if (qrData) {
        console.log('qrData', qrData);
        this.navCtrl.push('ReceiveCoinsPage', {qrData, orderTotal});
      }
      this.loader.hide();
    } catch (error) {
      this.loader.hide();
      console.log(error);
    }

  }

}
