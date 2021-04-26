import { Component, Vue, Inject } from 'vue-property-decorator';

import { INotificationData } from '@/shared/model/notification-data.model';
import NotificationDataService from './notification-data.service';

@Component
export default class NotificationDataDetails extends Vue {
  @Inject('notificationDataService') private notificationDataService: () => NotificationDataService;
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
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
