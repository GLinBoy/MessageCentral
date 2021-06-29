import { INotification } from '@/shared/model/notification.model';

export interface INotificationData {
  id?: number;
  key?: string;
  value?: string;
  notification?: INotification;
}

export class NotificationData implements INotificationData {
  constructor(public id?: number, public key?: string, public value?: string, public notification?: INotification) {}
}
