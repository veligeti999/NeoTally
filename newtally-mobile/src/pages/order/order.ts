import {Component, OnInit} from '@angular/core';
import { IonicPage, NavController, NavParams } from 'ionic-angular';
import {StateProvider} from "../../providers/state/state";
import {Counter} from "../../model/counter.model";
import {CounterProvider} from "../../providers/counter/counter";
import {NgForm} from "@angular/forms";
import {LoaderProvider} from "../../providers/loader/loader";

@IonicPage()
@Component({
  selector: 'page-order',
  templateUrl: 'order.html',
})
export class OrderPage implements OnInit {
  counter: Counter;
  constructor(
    public navCtrl: NavController,
    public navParams: NavParams,
    private loader: LoaderProvider,
    private counterProvider: CounterProvider,
    private state: StateProvider) {
  }

  ngOnInit() {
    this.counter = this.state.currentCounter;
  }
  ionViewDidLoad() {
    console.log('ionViewDidLoad OrderPage');
  }

  async pay(form: NgForm) {
    if (!form.invalid) {
      console.log('clicked');
      // calculate discounts
      try {
        this.loader.show();
        const discounts = await this.counterProvider.calculateDiscount({payment_amount: parseFloat(form.value.amount)});
        this.loader.hide();
        this.navCtrl.push('DiscountPage', {discounts});
      } catch (error) {
        this.loader.hide();
        console.log('error', error);
      }
    }

  }
}
