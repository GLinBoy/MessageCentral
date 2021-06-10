import { mixins } from 'vue-class-component';

import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { IShortMessage } from '@/shared/model/short-message.model';

import ShortMessageService from './short-message.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class ShortMessage extends Vue {
  @Inject('shortMessageService') private shortMessageService: () => ShortMessageService;
  private removeId: number = null;
  public itemsPerPage = 20;
  public queryCount: number = null;
  public page = 1;
  public previousPage = 1;
  public propOrder = 'id';
  public reverse = true;
  public totalItems = 0;
  public currentSearch: string = null;

  public shortMessages: IShortMessage[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllShortMessages();
  }

  public clear(): void {
    this.page = 1;
    this.currentSearch = null;
    this.retrieveAllShortMessages();
  }

  public retrieveAllShortMessages(): void {
    this.isFetching = true;

    const paginationQuery = {
      page: this.page - 1,
      size: this.itemsPerPage,
      sort: this.sort(),
      search: this.search(),
    };
    this.shortMessageService()
      .retrieve(paginationQuery)
      .then(
        res => {
          this.shortMessages = res.data;
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
    this.retrieveAllShortMessages();
  }

  public handleSyncList(): void {
    this.clear();
  }

  public prepareRemove(instance: IShortMessage): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeShortMessage(): void {
    this.shortMessageService()
      .delete(this.removeId)
      .then(() => {
        const message = this.$t('messageCentralApp.shortMessage.deleted', { param: this.removeId });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllShortMessages();
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
      result = `( phoneNumber:*${this.currentSearch}* OR content:*${this.currentSearch}* )`;
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
    this.retrieveAllShortMessages();
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
