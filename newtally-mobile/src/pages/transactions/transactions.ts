import { Component } from '@angular/core';
import { IonicPage, NavController, NavParams } from 'ionic-angular';
import {CounterProvider} from "../../providers/counter/counter";
import {Transaction} from "../../model/transaction.mode";
import {LoaderProvider} from "../../providers/loader/loader";

@IonicPage()
@Component({
  selector: 'page-transactions',
  templateUrl: 'transactions.html',
})
export class TransactionsPage {
  transactions: Transaction[];
  date = '2018-02-01'
  constructor(
    public navCtrl: NavController,
    public navParams: NavParams,
    private loader: LoaderProvider,
    private counterProvider: CounterProvider) {
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad TransactionsPage');
  }

  ionViewWillEnter() {
    this.getTransactions();
  }

  async getTransactions() {
    try {
      this.loader.show();
      const transactions = await this.counterProvider.getTransactions();
      console.log('transactions', transactions);
      this.transactions = transactions;
      this.loader.hide();
    } catch (error) {
      this.loader.hide();
      console.log(error);
    }
  }
}
