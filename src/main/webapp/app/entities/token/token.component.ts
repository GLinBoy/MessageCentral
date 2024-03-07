import { defineComponent, inject, onMounted, ref, type Ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

import TokenService from './token.service';
import { type IToken } from '@/shared/model/token.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Token',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const tokenService = inject('tokenService', () => new TokenService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const itemsPerPage = ref(20);
    const queryCount: Ref<number> = ref(null);
    const page: Ref<number> = ref(1);
    const propOrder = ref('id');
    const reverse = ref(true);
    const totalItems = ref(0);
    const currentSearch: Ref<string> = ref('');

    const tokens: Ref<IToken[]> = ref([]);

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
        result = `name==*${currentSearch.value}* or createdBy==*${currentSearch.value}* or updatedBy==*${currentSearch.value}*`;
      }
      return result;
    };

    const retrieveTokens = async () => {
      isFetching.value = true;
      try {
        const paginationQuery = {
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
          query: search(),
        };
        const res = await tokenService().retrieve(paginationQuery);
        totalItems.value = Number(res.headers['x-total-count']);
        queryCount.value = totalItems.value;
        tokens.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveTokens();
    };

    onMounted(async () => {
      await retrieveTokens();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IToken) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeToken = async () => {
      try {
        await tokenService().delete(removeId.value);
        const message = t$('messageCentralApp.token.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveTokens();
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
        await retrieveTokens();
      } else {
        // reset the pagination
        clear();
      }
    });

    // Whenever page changes, switch to the new page.
    watch(page, async () => {
      await retrieveTokens();
    });

    const handleSearch = () => {
      retrieveTokens();
    };

    const enableToken = (token: IToken) => {
      isFetching.value = true;
      tokenService()
        .enableToken(token.id)
        .then(() => {
          const message = t$('messageCentralApp.token.enabled', { param: token.id });
          alertService.showInfo(message, { variant: 'info' });
          token.disable = false;
        })
        .catch(error => {
          alertService().showHttpError(this, error.response);
        });
      isFetching.value = false;
    };

    const disableToken = (token: IToken) => {
      isFetching.value = true;
      tokenService()
        .disableToken(token.id)
        .then(() => {
          const message = t$('messageCentralApp.token.disabled', { param: token.id });
          alertService.showInfo(message, { variant: 'info' });
          token.disable = true;
        })
        .catch(error => {
          alertService().showHttpError(this, error.response);
        });
      isFetching.value = false;
    };

    return {
      tokens,
      handleSyncList,
      isFetching,
      retrieveTokens,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeToken,
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
      disableToken,
      enableToken,
    };
  },
});
