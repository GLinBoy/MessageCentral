import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TokenService from './token.service';
import { useDateFormat } from '@/shared/composables';
import { type IToken } from '@/shared/model/token.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TokenDetails',
  setup() {
    const dateFormat = useDateFormat();
    const tokenService = inject('tokenService', () => new TokenService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const token: Ref<IToken> = ref({});

    const retrieveToken = async tokenId => {
      try {
        const res = await tokenService().find(tokenId);
        token.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.tokenId) {
      retrieveToken(route.params.tokenId);
    }

    return {
      ...dateFormat,
      alertService,
      token,

      previousState,
      t$: useI18n().t,
    };
  },
});
