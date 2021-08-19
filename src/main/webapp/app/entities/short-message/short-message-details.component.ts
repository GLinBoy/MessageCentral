import { Component, Vue, Inject } from 'vue-property-decorator';

import { IShortMessage } from '@/shared/model/short-message.model';
import { MessageStatus } from '@/shared/model/enumerations/message-status.model';
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

  public getVariant(status) {
    if (MessageStatus.IN_QUEUE === status) {
      return 'info';
    } else if (MessageStatus.SENT === status) {
      return 'success';
    } else if (MessageStatus.FAILED === status) {
      return 'danger';
    } else {
      return 'secondary';
    }
  }
}
