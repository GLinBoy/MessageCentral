import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, maxLength } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import NotificationDataService from '@/entities/notification-data/notification-data.service';

import { INotification, Notification } from '@/shared/model/notification.model';
import { INotificationData, NotificationData } from '@/shared/model/notification-data.model';
import NotificationService from './notification.service';
import { MessageStatus } from '@/shared/model/enumerations/message-status.model';

const validations: any = {
  notification: {
    username: {
      required,
      maxLength: maxLength(64),
    },
    token: {
      required,
      maxLength: maxLength(164),
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
export default class NotificationUpdate extends Vue {
  @Inject('notificationService') private notificationService: () => NotificationService;
  @Inject('alertService') private alertService: () => AlertService;

  public notification: INotification = new Notification();
  public data: INotificationData = new NotificationData();

  @Inject('notificationDataService') private notificationDataService: () => NotificationDataService;

  public notificationData: INotificationData[] = [];
  public messageStatusValues: string[] = Object.keys(MessageStatus);
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.notificationId) {
        vm.retrieveNotification(to.params.notificationId);
      }
      vm.initRelationships();
    });
  }

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
    if (this.notification.id) {
      this.notificationService()
        .update(this.notification)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('messageCentralApp.notification.updated', { param: param.id });
          return (this.$root as any).$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    } else {
      this.notificationService()
        .create(this.notification)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('messageCentralApp.notification.created', { param: param.id });
          (this.$root as any).$bvToast.toast(message.toString(), {
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
  }

  public retrieveNotification(notificationId): void {
    this.notificationService()
      .find(notificationId)
      .then(res => {
        this.notification = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.notificationDataService()
      .retrieve()
      .then(res => {
        this.notificationData = res.data;
      });
  }

  public addData(): void {
    if (this.notification.data) {
      this.notification.data = this.notification.data.filter(obj => obj.key !== this.data.key);
      this.notification.data.push(this.data);
    } else {
      this.notification.data = [this.data];
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
    this.notification.data = this.notification.data.filter(obj => obj.key !== data.key);
  }
}
