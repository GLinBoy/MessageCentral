import { Component, Vue, Inject } from 'vue-property-decorator';

import { INotificationData } from '@/shared/model/notification-data.model';
import NotificationDataService from './notification-data.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class NotificationDataDetails extends Vue {
  @Inject('notificationDataService') private notificationDataService: () => NotificationDataService;
  @Inject('alertService') private alertService: () => AlertService;

  public notificationData: INotificationData = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.notificationDataId) {
        vm.retrieveNotificationData(to.params.notificationDataId);
      }
    });
  }

  public retrieveNotificationData(notificationDataId) {
    this.notificationDataService()
      .find(notificationDataId)
      .then(res => {
        this.notificationData = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
