import { Component, Vue, Inject } from 'vue-property-decorator';

import { INotification } from '@/shared/model/notification.model';
import NotificationService from './notification.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class NotificationDetails extends Vue {
  @Inject('notificationService') private notificationService: () => NotificationService;
  @Inject('alertService') private alertService: () => AlertService;

  public notification: INotification = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.notificationId) {
        vm.retrieveNotification(to.params.notificationId);
      }
    });
  }

  public retrieveNotification(notificationId) {
    this.notificationService()
      .find(notificationId)
      .then(res => {
        this.notification = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
