import { mixins } from 'vue-class-component';

import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { IToken } from '@/shared/model/token.model';

import TokenService from './token.service';
import AlertService from '@/shared/alert/alert.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class Token extends Vue {
  @Inject('tokenService') private tokenService: () => TokenService;
  @Inject('alertService') private alertService: () => AlertService;

  private removeId: number = null;
  public itemsPerPage = 20;
  public queryCount: number = null;
  public page = 1;
  public previousPage = 1;
  public propOrder = 'id';
  public reverse = true;
  public totalItems = 0;
  public currentSearch: string = null;

  public tokens: IToken[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllTokens();
  }

  public clear(): void {
    this.page = 1;
    this.currentSearch = null;
    this.retrieveAllTokens();
  }

  public retrieveAllTokens(): void {
    this.isFetching = true;
    const paginationQuery = {
      page: this.page - 1,
      size: this.itemsPerPage,
      sort: this.sort(),
      search: this.search(),
    };
    this.tokenService()
      .retrieve(paginationQuery)
      .then(
        res => {
          this.tokens = res.data;
          this.totalItems = Number(res.headers['x-total-count']);
          this.queryCount = this.totalItems;
          this.isFetching = false;
        },
        err => {
          this.isFetching = false;
          this.alertService().showHttpError(this, err.response);
        }
      );
  }

  public handleSearch(): void {
    this.retrieveAllTokens();
  }

  public handleSyncList(): void {
    this.clear();
  }

  public prepareRemove(instance: IToken): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeToken(): void {
    this.tokenService()
      .delete(this.removeId)
      .then(() => {
        const message = this.$t('messageCentralApp.token.deleted', { param: this.removeId });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllTokens();
        this.closeDialog();
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public sort(): Array<any> {
    const result = [this.propOrder + ',' + (this.reverse ? 'desc' : 'asc')];
    if (this.propOrder !== 'id') {
      result.push('id');
    }
    return result;
  }

  public search(): string {
    let result = undefined;
    if (this.currentSearch) {
      result = `name==*${this.currentSearch}* or createdBy==*${this.currentSearch}* or updatedBy==*${this.currentSearch}*`;
    }
    return result;
  }

  public loadPage(page: number): void {
    if (page !== this.previousPage) {
      this.previousPage = page;
      this.transition();
    }
  }

  public transition(): void {
    this.retrieveAllTokens();
  }

  public changeOrder(propOrder): void {
    this.propOrder = propOrder;
    this.reverse = !this.reverse;
    this.transition();
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }

  public enableToken(token: IToken): void {
    this.tokenService()
      .enableToken(token.id)
      .then(() => {
        const message = this.$t('messageCentralApp.token.enabled', { param: token.id });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'info',
          solid: true,
          autoHideDelay: 5000,
        });
        token.disable = false;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public disableToken(token: IToken): void {
    this.tokenService()
      .disableToken(token.id)
      .then(() => {
        const message = this.$t('messageCentralApp.token.disabled', { param: token.id });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'info',
          solid: true,
          autoHideDelay: 5000,
        });
        token.disable = true;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }
}
