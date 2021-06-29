import { Component, Vue, Inject } from 'vue-property-decorator';

import { IShortMessage } from '@/shared/model/short-message.model';
import ShortMessageService from './short-message.service';

@Component
export default class ShortMessageDetails extends Vue {
  @Inject('shortMessageService') private shortMessageService: () => ShortMessageService;
  public shortMessage: IShortMessage = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.shortMessageId) {
        vm.retrieveShortMessage(to.params.shortMessageId);
      }
    });
  }

  public retrieveShortMessage(shortMessageId) {
    this.shortMessageService()
      .find(shortMessageId)
      .then(res => {
        this.shortMessage = res;
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
