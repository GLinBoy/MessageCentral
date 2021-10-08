import { Component, Vue, Inject } from 'vue-property-decorator';

import { INotification } from '@/shared/model/notification.model';
import NotificationService from './notification.service';

@Component
export default class NotificationDetails extends Vue {
  @Inject('notificationService') private notificationService: () => NotificationService;
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
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
