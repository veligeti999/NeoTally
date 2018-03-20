import {Component, OnInit} from '@angular/core';
import { IonicPage, NavController, NavParams } from 'ionic-angular';
import {StateProvider} from "../../providers/state/state";
import {Counter} from "../../model/counter.model";

@IonicPage()
@Component({
  selector: 'page-profile',
  templateUrl: 'profile.html',
})
export class ProfilePage implements OnInit {
  counter: Counter;
  constructor(
    public navCtrl: NavController,
    public state: StateProvider,
    public navParams: NavParams) {
  }

  ngOnInit() {
    this.counter = this.state.currentCounter;
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad ProfilePage');
  }


}
