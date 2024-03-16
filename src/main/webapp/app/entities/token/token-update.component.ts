import { computed, defineComponent, inject, ref, type Ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';
import dayjs from 'dayjs';
import { DATE_TIME_FORMAT, DATE_TIME_LONG_FORMAT, DATE_TIME_SERVER_LONG_FORMAT } from '@/shared/composables/date-format';

import TokenService from './token.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IToken, Token } from '@/shared/model/token.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TokenUpdate',
  setup() {
    const tokenService = inject('tokenService', () => new TokenService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const token: Ref<IToken> = ref(new Token());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const userRoleSelected: Ref<any[]> = ref([]);
    const tokenRoleOptions = ref([
      { value: 1, text: 'Email' },
      { value: 2, text: 'Short Message' },
      { value: 3, text: 'Notification' },
    ]);
    const tokenValidityPeriod = ref(null);
    const tokenValidityOption = ref([
      { value: null, text: 'Please select duration', disabled: true },
      { value: 1, text: '1 Month' },
      { value: 3, text: '3 Months' },
      { value: 6, text: '6 Months' },
      { value: 12, text: '12 Months' },
    ]);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveToken = async tokenId => {
      try {
        const res = await tokenService().find(tokenId);
        res.deprecateAt = new Date(res.deprecateAt);
        res.createdAt = new Date(res.createdAt);
        res.updatedAt = new Date(res.updatedAt);
        token.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.tokenId) {
      retrieveToken(route.params.tokenId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 64 }).toString(), 64),
      },
      disable: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      deprecateAt: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      roles: {
        required: validations.required(t$('entity.validation.required').toString()),
        numeric: validations.numeric(t$('entity.validation.number').toString()),
      },
    };
    const v$ = useVuelidate(validationRules, token as any);
    v$.value.$validate();

    watch(userRoleSelected, async (newValue: Array<number>, oldValue: Array<number>) => {
      if (!token.value.roles) token.value.roles = 0;
      if (newValue.includes(1) && !oldValue.includes(1)) {
        token.value.roles ^= 1;
      }
      if (!newValue.includes(1) && oldValue.includes(1)) {
        token.value.roles ^= 1;
      }
      if (newValue.includes(2) && !oldValue.includes(2)) {
        token.value.roles ^= 2;
      }
      if (!newValue.includes(2) && oldValue.includes(2)) {
        token.value.roles ^= 2;
      }
      if (newValue.includes(3) && !oldValue.includes(3)) {
        token.value.roles ^= 4;
      }
      if (!newValue.includes(3) && oldValue.includes(3)) {
        token.value.roles ^= 4;
      }
    });

    const convertDateTimeToServer = (date: Date): string | null => {
      if (date && dayjs(date).isValid()) {
        return dayjs(date).format(DATE_TIME_SERVER_LONG_FORMAT);
      }
      return null;
    };

    watch(tokenValidityPeriod, async (newValue: number, _) => {
      const today: Date = new Date();
      today.setHours(23, 59, 59, 999);
      switch (newValue) {
        case 1: {
          token.value.deprecateAt = convertDateTimeToServer(today.setMonth(today.getMonth() + 1));
          break;
        }
        case 3: {
          token.value.deprecateAt = convertDateTimeToServer(today.setMonth(today.getMonth() + 3));
          break;
        }
        case 6: {
          token.value.deprecateAt = convertDateTimeToServer(today.setMonth(today.getMonth() + 6));
          break;
        }
        case 12: {
          token.value.deprecateAt = convertDateTimeToServer(today.setMonth(today.getMonth() + 12));
          break;
        }
        default: {
          token.value.deprecateAt = null;
          break;
        }
      }
    });

    const convertDateTimeToHuman = (date: Date): string | null => {
      if (date && dayjs(date).isValid()) {
        return dayjs(date).format(DATE_TIME_FORMAT);
      }
      return null;
    };

    const convertDateTimeFromServer = (date: Date): string | null => {
      if (date && dayjs(date).isValid()) {
        return dayjs(date).format(DATE_TIME_LONG_FORMAT);
      }
      return null;
    };

    const updateInstantField = (field, event) => {
      if (event.target.value) {
        token.value[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
      } else {
        token.value[field] = null;
      }
    };

    return {
      tokenService,
      alertService,
      token,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: token }),
      t$,
      userRoleSelected,
      tokenRoleOptions,
      tokenValidityOption,
      tokenValidityPeriod,
      convertDateTimeToHuman,
      convertDateTimeToServer,
      updateInstantField,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      this.tokenService()
        .create(this.token)
        .then(param => {
          this.isSaving = false;
          this.$router.push({ name: 'TokenView', params: { tokenId: param.id } });
          this.alertService.showSuccess(this.t$('messageCentralApp.token.created', { param: param.id }).toString());
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService.showHttpError(error.response);
        });
    },
  },
});
