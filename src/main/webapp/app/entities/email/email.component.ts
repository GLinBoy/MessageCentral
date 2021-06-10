import { mixins } from 'vue-class-component';

import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { IEmail } from '@/shared/model/email.model';

import JhiDataUtils from '@/shared/data/data-utils.service';

import EmailService from './email.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class Email extends mixins(JhiDataUtils) {
  @Inject('emailService') private emailService: () => EmailService;
  private removeId: number = null;
  public itemsPerPage = 20;
  public queryCount: number = null;
  public page = 1;
  public previousPage = 1;
  public propOrder = 'id';
  public reverse = true;
  public totalItems = 0;
  public currentSearch: string = null;

  public emails: IEmail[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllEmails();
  }

  public clear(): void {
    this.page = 1;
    this.retrieveAllEmails();
  }

  public retrieveAllEmails(): void {
    this.isFetching = true;

    const paginationQuery = {
      page: this.page - 1,
      size: this.itemsPerPage,
      sort: this.sort(),
      search: this.search(),
    };
    this.emailService()
      .retrieve(paginationQuery)
      .then(
        res => {
          this.emails = res.data;
          this.totalItems = Number(res.headers['x-total-count']);
          this.queryCount = this.totalItems;
          this.isFetching = false;
        },
        err => {
          this.isFetching = false;
        }
      );
  }

  public handleSearch(): void {
    this.retrieveAllEmails();
  }

  public handleSyncList(): void {
    this.clear();
  }

  public prepareRemove(instance: IEmail): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeEmail(): void {
    this.emailService()
      .delete(this.removeId)
      .then(() => {
        const message = this.$t('messageCentralApp.email.deleted', { param: this.removeId });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllEmails();
        this.closeDialog();
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
    const result = undefined;
    if (this.currentSearch) {
      result = `( receiver:*${this.currentSearch}* OR subject:*${this.currentSearch}* OR content:*${this.currentSearch}* )`;
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
    this.retrieveAllEmails();
  }

  public changeOrder(propOrder): void {
    this.propOrder = propOrder;
    this.reverse = !this.reverse;
    this.transition();
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }
}
