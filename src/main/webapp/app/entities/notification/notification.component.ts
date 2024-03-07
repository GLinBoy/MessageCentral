import { defineComponent, inject, onMounted, ref, type Ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

import NotificationService from './notification.service';
import { type INotification } from '@/shared/model/notification.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';
import { MessageStatus } from '@/shared/model/enumerations/message-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Notification',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const notificationService = inject('notificationService', () => new NotificationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const itemsPerPage = ref(20);
    const queryCount: Ref<number> = ref(null);
    const page: Ref<number> = ref(1);
    const propOrder = ref('id');
    const reverse = ref(true);
    const totalItems = ref(0);
    const currentSearch: Ref<string> = ref('');

    const notifications: Ref<INotification[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      page.value = 1;
    };

    const sort = (): Array<any> => {
      const result = [propOrder.value + ',' + (reverse.value ? 'desc' : 'asc')];
      if (propOrder.value !== 'id') {
        result.push('id');
      }
      return result;
    };

    const search = (): string => {
      let result = '';
      if (currentSearch.value) {
        result = `username==*${currentSearch.value}* or token==*${currentSearch.value}* or subject==*${currentSearch.value}* or content==*${currentSearch.value}* or createdBy==*${currentSearch.value}*`;
      }
      return result;
    };

    const retrieveNotifications = async () => {
      isFetching.value = true;
      try {
        const paginationQuery = {
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
          query: search(),
        };
        const res = await notificationService().retrieve(paginationQuery);
        totalItems.value = Number(res.headers['x-total-count']);
        queryCount.value = totalItems.value;
        notifications.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveNotifications();
    };

    onMounted(async () => {
      await retrieveNotifications();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: INotification) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeNotification = async () => {
      try {
        await notificationService().delete(removeId.value);
        const message = t$('messageCentralApp.notification.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveNotifications();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    const changeOrder = (newOrder: string) => {
      if (propOrder.value === newOrder) {
        reverse.value = !reverse.value;
      } else {
        reverse.value = false;
      }
      propOrder.value = newOrder;
    };

    // Whenever order changes, reset the pagination
    watch([propOrder, reverse], async () => {
      if (page.value === 1) {
        // first page, retrieve new data
        await retrieveNotifications();
      } else {
        // reset the pagination
        clear();
      }
    });

    // Whenever page changes, switch to the new page.
    watch(page, async () => {
      await retrieveNotifications();
    });

    const handleSearch = () => {
      retrieveNotifications();
    };

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
      notifications,
      handleSyncList,
      isFetching,
      retrieveNotifications,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeNotification,
      itemsPerPage,
      queryCount,
      page,
      propOrder,
      reverse,
      totalItems,
      changeOrder,
      t$,
      currentSearch,
      handleSearch,
      getVariant,
    };
  },
});
