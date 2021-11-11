import { Component, Vue, Inject, Watch } from 'vue-property-decorator';

import { required, maxLength, numeric, between } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT, DATE_TIME_FORMAT } from '@/shared/date/filters';

import AlertService from '@/shared/alert/alert.service';

import { IToken, Token } from '@/shared/model/token.model';
import TokenService from './token.service';

const validations: any = {
  token: {
    name: {
      required,
      maxLength: maxLength(64),
    },
    token: {
      required,
      maxLength: maxLength(512),
    },
    disable: {
      required,
    },
    createdAt: {
      required,
    },
    deprecateAt: {
      required,
    },
    roles: {
      required,
      numeric,
      between: between(1, 7),
    },
  },
};

@Component({
  validations,
})
export default class TokenUpdate extends Vue {
  @Inject('tokenService') private tokenService: () => TokenService;
  @Inject('alertService') private alertService: () => AlertService;

  public token: IToken = new Token();
  public isSaving = false;
  public currentLanguage = '';
  public tokenValidityOption = [
    { value: null, text: 'Please select duration', disabled: true },
    { value: 1, text: '1 Month' },
    { value: 3, text: '3 Months' },
    { value: 6, text: '6 Months' },
    { value: 12, text: '12 Months' },
  ];
  public tokenValidityPeriod = null;
  public userRoleOptions = [
    { value: 1, text: 'Email' },
    { value: 2, text: 'Short Message' },
    { value: 3, text: 'Notification' },
  ];
  public userRoleSelected = [];

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.tokenId) {
        vm.retrieveToken(to.params.tokenId);
      }
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    if (this.token.id) {
      this.tokenService()
        .update(this.token)
        .then(param => {
          this.isSaving = false;
          this.$router.push({ name: 'TokenView', params: { tokenId: param.id } });
          const message = this.$t('messageCentralApp.token.updated', { param: param.id });
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    } else {
      this.tokenService()
        .create(this.token)
        .then(param => {
          this.isSaving = false;
          this.$router.push({ name: 'TokenView', params: { tokenId: param.id } });
          const message = this.$t('messageCentralApp.token.created', { param: param.id });
          this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    }
  }

  public convertDateTimeFromServer(date: Date): string {
    if (date && dayjs(date).isValid()) {
      return dayjs(date).format(DATE_TIME_LONG_FORMAT);
    }
    return null;
  }

  public updateInstantField(field, event) {
    if (event.target.value) {
      this.token[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.token[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.token[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.token[field] = null;
    }
  }

  public retrieveToken(tokenId): void {
    this.tokenService()
      .find(tokenId)
      .then(res => {
        res.createdAt = new Date(res.createdAt);
        res.deprecateAt = new Date(res.deprecateAt);
        this.token = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  @Watch('tokenValidityPeriod')
  onTokenValidityChanged(value: int, _) {
    var today = new Date();
    today.setHours(23, 59, 59);
    switch (value) {
      case 1: {
        this.token.deprecateAt = today.setMonth(today.getMonth() + 1);
        break;
      }
      case 3: {
        this.token.deprecateAt = today.setMonth(today.getMonth() + 3);
        break;
      }
      case 6: {
        this.token.deprecateAt = today.setMonth(today.getMonth() + 6);
        break;
      }
      case 12: {
        this.token.deprecateAt = today.setMonth(today.getMonth() + 12);
        break;
      }
      default: {
        this.token.deprecateAt = null;
        break;
      }
    }
  }

  public convertDateTimeToHuman(date: Date): string {
    if (date && dayjs(date).isValid()) {
      return dayjs(date).format(DATE_TIME_FORMAT);
    }
    return null;
  }

  @Watch('userRoleSelected')
  onUserRoleSelectedChanged(newValue: int[], oldValue: int[]) {
    if (!this.token.roles) this.token.roles = 0;
    if (newValue.includes(1) && !oldValue.includes(1)) {
      this.token.roles ^= 1;
    }
    if (!newValue.includes(1) && oldValue.includes(1)) {
      this.token.roles ^= 1;
    }
    if (newValue.includes(2) && !oldValue.includes(2)) {
      this.token.roles ^= 2;
    }
    if (!newValue.includes(2) && oldValue.includes(2)) {
      this.token.roles ^= 2;
    }
    if (newValue.includes(3) && !oldValue.includes(3)) {
      this.token.roles ^= 4;
    }
    if (!newValue.includes(3) && oldValue.includes(3)) {
      this.token.roles ^= 4;
    }
  }

  public initRelationships(): void {}
}
