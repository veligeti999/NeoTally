import {Component, OnInit} from '@angular/core';
import {IonicPage, NavController, NavParams, ViewController} from 'ionic-angular';

@IonicPage()
@Component({
  selector: 'page-sms-confirmation',
  templateUrl: 'sms-confirmation.html',
})
export class SmsConfirmationPage implements OnInit {
  mobile: string;
  constructor(public navCtrl: NavController, public navParams: NavParams, private vc: ViewController) {
  }

  ngOnInit() {
    this.mobile = this.navParams.get('mobile');
  }
  ionViewDidLoad() {
    console.log('ionViewDidLoad SmsConfirmationPage');
  }

  dismiss() {
    this.vc.dismiss();
    this.navCtrl.setRoot('TabsPage');
  }
}
