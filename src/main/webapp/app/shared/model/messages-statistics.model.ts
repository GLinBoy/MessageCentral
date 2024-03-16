import { Statistics } from '@/shared/model/statistics.model';

export interface IMessagesStatistics {
  date?: Date;
  email?: Statistics;
  sms?: Statistics;
  notification?: Statistics;
}

export class MessagesStatistics implements IMessagesStatistics {
  constructor(
    public date?: Date,
    public email?: Statistics,
    public sms?: Statistics,
    public notification?: Statistics,
  ) {}
}
