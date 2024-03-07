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
    const { t: t$ } = useI18n();
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

    const enableToken = () => {
      tokenService()
        .enableToken(token.value.id)
        .then(() => {
          const message = t$('messageCentralApp.token.enabled', { param: token.value.id });
          alertService.showInfo(message, { variant: 'info' });
          token.value.disable = false;
        })
        .catch(error => {
          alertService().showHttpError(this, error.response);
        });
    };

    const disableToken = () => {
      tokenService()
        .disableToken(token.value.id)
        .then(() => {
          const message = t$('messageCentralApp.token.disabled', { param: token.value.id });
          alertService.showInfo(message, { variant: 'info' });
          token.value.disable = true;
        })
        .catch(error => {
          alertService().showHttpError(this, error.response);
        });
    };

    return {
      ...dateFormat,
      alertService,
      token,

      previousState,
      t$,
      enableToken,
      disableToken,
    };
  },
  methods: {
    copy(): void {
      this.$refs.clone.focus();
      document.execCommand('copy');
    },
  },
});
