import { Statistics } from '@/shared/model/statistics.model';

export interface IMessagesStatistics {
  date?: Date;
  email?: Statistics;
  sms?: Statistics;
  notification?: Statistics;
}
