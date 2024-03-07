import { defineComponent, inject, onMounted, ref, type Ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

import ShortMessageService from './short-message.service';
import { type IShortMessage } from '@/shared/model/short-message.model';
import { MessageStatus } from '@/shared/model/enumerations/message-status.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ShortMessage',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const shortMessageService = inject('shortMessageService', () => new ShortMessageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const itemsPerPage = ref(20);
    const queryCount: Ref<number> = ref(null);
    const page: Ref<number> = ref(1);
    const propOrder = ref('id');
    const reverse = ref(true);
    const totalItems = ref(0);
    const currentSearch: Ref<string> = ref('');

    const shortMessages: Ref<IShortMessage[]> = ref([]);

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
        result = `phoneNumber==*${currentSearch.value}* or content==*${currentSearch.value}* or createdBy==*${currentSearch.value}*`;
      }
      return result;
    };

    const retrieveShortMessages = async () => {
      isFetching.value = true;
      try {
        const paginationQuery = {
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
          query: search(),
        };
        const res = await shortMessageService().retrieve(paginationQuery);
        totalItems.value = Number(res.headers['x-total-count']);
        queryCount.value = totalItems.value;
        shortMessages.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveShortMessages();
    };

    onMounted(async () => {
      await retrieveShortMessages();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IShortMessage) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeShortMessage = async () => {
      try {
        await shortMessageService().delete(removeId.value);
        const message = t$('messageCentralApp.shortMessage.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveShortMessages();
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
        await retrieveShortMessages();
      } else {
        // reset the pagination
        clear();
      }
    });

    // Whenever page changes, switch to the new page.
    watch(page, async () => {
      await retrieveShortMessages();
    });

    const handleSearch = () => {
      retrieveShortMessages();
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
      shortMessages,
      handleSyncList,
      isFetching,
      retrieveShortMessages,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeShortMessage,
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
