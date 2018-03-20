import {Component, ViewChild} from '@angular/core';
import {IonicPage, NavController, NavParams} from 'ionic-angular';
import {AuthProvider} from "../../providers/auth/auth";
import {NgForm} from "@angular/forms";
import {StateProvider} from "../../providers/state/state";
import {StorageProvider} from "../../providers/storage/storage";
import {LoaderProvider} from "../../providers/loader/loader";
import {NotificationsProvider} from "../../providers/notifications/notifications";
import {AlertProvider} from "../../providers/alert/alert";

@IonicPage()
@Component({
  selector: 'page-login',
  templateUrl: 'login.html',
})
export class LoginPage {
  //3fe7a149df4f69fd
  //3fe7a149df4f69fd
  //3fe6fb33c59eeb9c
  secret = '';
  constructor(
    public navCtrl: NavController,
    private auth: AuthProvider,
    public state: StateProvider,
    private loader: LoaderProvider,
    private alert: AlertProvider,
    private notifications: NotificationsProvider,
    private storage: StorageProvider,
    public navParams: NavParams) {
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad LoginPage');
  }

  // set-cookie: JSESSIONID=1omxvwyafc0pr10ul9mvu04lqv;Path=/new-tally
  async login(form: NgForm) {
    // this.auth.login();
    if (!form.invalid) {
      try {
        this.loader.show();
        const counter = await this.auth.login(form.value.secret);
        if (counter) {
          console.log(counter);
          await this.storage.saveCounter(counter);
          const pushToken = await this.storage.getPushToken();
          this.state.currentCounter = counter;
          if (pushToken) {
            const registered = await this.notifications.registerDevice(pushToken, counter.counter_id);
            console.log('registered', registered);
          }
          this.loader.hide();
          this.navCtrl.setRoot('TabsPage', {counter});
        }
      } catch (error) {
        this.loader.hide();
        this.alert.showWithTitle('Counter Code not Found. Please check and re-enter.', 'Error');
        console.log('error', error);
      }
    }

  }

  forgotPassword(event) {
  }
}
