import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, maxLength, minLength } from 'vuelidate/lib/validators';

import NotificationDataService from '@/entities/notification-data/notification-data.service';

import AlertService from '@/shared/alert/alert.service';

import { INotifications, Notifications } from '@/shared/model/notification.model';
import { INotificationData, NotificationData } from '@/shared/model/notification-data.model';
import { IReceiver, Receiver } from '@/shared/model/notification.model';
import NotificationService from './notification.service';

const validations: any = {
  notifications: {
    receivers: {
      required,
      minLength: minLength(1),
      $each: {
        required,
      },
    },
    subject: {
      required,
      maxLength: maxLength(128),
    },
    content: {
      required,
      maxLength: maxLength(4000),
    },
    image: {
      maxLength: maxLength(256),
    },
    status: {},
  },
};

@Component({
  validations,
})
export default class NotificationMultiple extends Vue {
  @Inject('notificationService') private notificationService: () => NotificationService;
  @Inject('notificationDataService') private notificationDataService: () => NotificationDataService;
  @Inject('alertService') private alertService: () => AlertService;

  public notifications: INotifications = new Notifications();
  public data: INotificationData = new NotificationData();
  public receiver: IReceiver = new Receiver();
  public isSaving = false;
  public currentLanguage = '';

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    this.notificationService()
      .createMultiple([this.notifications])
      .then(() => {
        this.isSaving = false;
        this.$router.go(-1);
        const message = this.$t('messageCentralApp.notification.createdMultiple');
        return (this.$root as any).$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Success',
          variant: 'success',
          solid: true,
          autoHideDelay: 5000,
        });
      })
      .catch(error => {
        this.isSaving = false;
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public addData(): void {
    if (this.notifications.data) {
      this.notifications.data = this.notifications.data.filter(obj => obj.dataKey !== this.data.dataKey);
      this.notifications.data.push(this.data);
    } else {
      this.notifications.data = [this.data];
    }
    this.resetData();
  }

  public resetData(): void {
    this.data = new NotificationData();
  }

  public prepareDataEdit(data): void {
    this.data = data;
  }

  public prepareDataRemove(data) {
    this.notifications.data = this.notifications.data.filter(obj => obj.dataKey !== data.dataKey);
  }

  public addReceiver(): void {
    if (this.notifications.receivers) {
      this.notifications.receivers = this.notifications.receivers.filter(
        obj => obj.username !== this.receiver.username && obj.token !== this.receiver.token
      );
      this.notifications.receivers.push(this.receiver);
    } else {
      this.notifications.receivers = [this.receiver];
    }
    this.resetReceiver();
  }

  public resetReceiver(): void {
    this.receiver = new Receiver();
  }

  public prepareReceiverEdit(receiver): void {
    this.receiver = receiver;
  }

  public prepareReceiverRemove(receiver) {
    this.notifications.receivers = this.notifications.receivers.filter(
      obj => obj.username !== receiver.username && obj.token !== receiver.token
    );
  }
}
