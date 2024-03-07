import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import NotificationService from './notification.service';
import { useDateFormat } from '@/shared/composables';
import { type INotification } from '@/shared/model/notification.model';
import { useAlertService } from '@/shared/alert/alert.service';
import { MessageStatus } from '@/shared/model/enumerations/message-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'NotificationDetails',
  setup() {
    const dateFormat = useDateFormat();
    const notificationService = inject('notificationService', () => new NotificationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const notification: Ref<INotification> = ref({});

    const retrieveNotification = async notificationId => {
      try {
        const res = await notificationService().find(notificationId);
        notification.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.notificationId) {
      retrieveNotification(route.params.notificationId);
    }

    const getVariant = (status: MessageStatus) => {
      if (MessageStatus.IN_QUEUE === status) {
        return 'info';
      } else if (MessageStatus.SENT === status) {
        return 'success';
      } else if (MessageStatus.FAILED === status) {
        return 'danger';
      } else {
        return 'secondary';
      }
    };

    return {
      ...dateFormat,
      alertService,
      notification,

      previousState,
      t$: useI18n().t,
      getVariant,
    };
  },
});
