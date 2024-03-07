import { defineComponent, inject, onMounted, ref, type Ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

import EmailService from './email.service';
import { type IEmail } from '@/shared/model/email.model';
import { MessageStatus } from '@/shared/model/enumerations/message-status.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Email',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const dataUtils = useDataUtils();
    const emailService = inject('emailService', () => new EmailService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const itemsPerPage = ref(20);
    const queryCount: Ref<number> = ref(null);
    const page: Ref<number> = ref(1);
    const propOrder = ref('id');
    const reverse = ref(true);
    const totalItems = ref(0);
    const currentSearch: Ref<string> = ref('');

    const emails: Ref<IEmail[]> = ref([]);

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
        result = `receiver==*${currentSearch.value}* or subject==*${currentSearch.value}* or content==*${currentSearch.value}* or createdBy==*${currentSearch.value}*`;
      }
      return result;
    };

    const retrieveEmails = async () => {
      isFetching.value = true;
      try {
        const paginationQuery = {
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
          query: search(),
        };
        const res = await emailService().retrieve(paginationQuery);
        totalItems.value = Number(res.headers['x-total-count']);
        queryCount.value = totalItems.value;
        emails.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveEmails();
    };

    onMounted(async () => {
      await retrieveEmails();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IEmail) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeEmail = async () => {
      try {
        await emailService().delete(removeId.value);
        const message = t$('messageCentralApp.email.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveEmails();
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
        await retrieveEmails();
      } else {
        // reset the pagination
        clear();
      }
    });

    // Whenever page changes, switch to the new page.
    watch(page, async () => {
      await retrieveEmails();
    });

    const handleSearch = () => {
      retrieveEmails();
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
      emails,
      handleSyncList,
      isFetching,
      retrieveEmails,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeEmail,
      itemsPerPage,
      queryCount,
      page,
      propOrder,
      reverse,
      totalItems,
      changeOrder,
      t$,
      ...dataUtils,
      currentSearch,
      handleSearch,
      getVariant,
    };
  },
});
