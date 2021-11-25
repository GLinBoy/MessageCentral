import { Component, Vue, Inject } from 'vue-property-decorator';

import { IToken } from '@/shared/model/token.model';
import TokenService from './token.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class TokenDetails extends Vue {
  @Inject('tokenService') private tokenService: () => TokenService;
  @Inject('alertService') private alertService: () => AlertService;

  public token: IToken = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.tokenId) {
        vm.retrieveToken(to.params.tokenId);
      }
    });
  }

  public retrieveToken(tokenId) {
    this.tokenService()
      .find(tokenId)
      .then(res => {
        this.token = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.push({ name: 'Token' });
  }

  public enableToken(): void {
    this.tokenService()
      .enableToken(this.token.id)
      .then(() => {
        const message = this.$t('messageCentralApp.token.enabled', { param: this.token.id });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'info',
          solid: true,
          autoHideDelay: 5000,
        });
        this.token.disable = false;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }
}
