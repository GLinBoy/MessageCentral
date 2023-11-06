import { INotification } from '@/shared/model/notification.model';

export interface INotificationData {
  id?: number;
  dataKey?: string;
  dataValue?: string;
  notification?: INotification;
}

export class NotificationData implements INotificationData {
  constructor(public id?: number, public dataKey?: string, public dataValue?: string, public notification?: INotification) {}
}
