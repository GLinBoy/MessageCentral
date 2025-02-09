import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import ShortMessageService from './short-message.service';
import { useDateFormat } from '@/shared/composables';
import { type IShortMessage } from '@/shared/model/short-message.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ShortMessageDetails',
  setup() {
    const dateFormat = useDateFormat();
    const shortMessageService = inject('shortMessageService', () => new ShortMessageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const shortMessage: Ref<IShortMessage> = ref({});

    const retrieveShortMessage = async shortMessageId => {
      try {
        const res = await shortMessageService().find(shortMessageId);
        shortMessage.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.shortMessageId) {
      retrieveShortMessage(route.params.shortMessageId);
    }

    return {
      ...dateFormat,
      alertService,
      shortMessage,

      previousState,
      t$: useI18n().t,
    };
  },
});
