import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, maxLength } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import NotificationService from '@/entities/notification/notification.service';
import { INotification } from '@/shared/model/notification.model';

import { INotificationData, NotificationData } from '@/shared/model/notification-data.model';
import NotificationDataService from './notification-data.service';

const validations: any = {
  notificationData: {
    key: {
      required,
      maxLength: maxLength(128),
    },
    value: {
      required,
      maxLength: maxLength(256),
    },
    notification: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class NotificationDataUpdate extends Vue {
  @Inject('notificationDataService') private notificationDataService: () => NotificationDataService;
  @Inject('alertService') private alertService: () => AlertService;

  public notificationData: INotificationData = new NotificationData();

  @Inject('notificationService') private notificationService: () => NotificationService;

  public notifications: INotification[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.notificationDataId) {
        vm.retrieveNotificationData(to.params.notificationDataId);
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
    if (this.notificationData.id) {
      this.notificationDataService()
        .update(this.notificationData)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('messageCentralApp.notificationData.updated', { param: param.id });
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
      this.notificationDataService()
        .create(this.notificationData)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('messageCentralApp.notificationData.created', { param: param.id });
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

  public retrieveNotificationData(notificationDataId): void {
    this.notificationDataService()
      .find(notificationDataId)
      .then(res => {
        this.notificationData = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.notificationService()
      .retrieve()
      .then(res => {
        this.notifications = res.data;
      });
  }
}
