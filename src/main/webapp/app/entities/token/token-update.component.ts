import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, maxLength, numeric } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

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
    { value: null, text: 'Please select duration' },
    { value: 1, text: '1 Month' },
    { value: 3, text: '3 Months' },
    { value: 6, text: '6 Months' },
    { value: 12, text: '12 Months' },
  ];

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

  public initRelationships(): void {}
}
