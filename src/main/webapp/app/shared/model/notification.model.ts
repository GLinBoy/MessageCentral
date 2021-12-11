import { INotificationData } from '@/shared/model/notification-data.model';

import { MessageStatus } from '@/shared/model/enumerations/message-status.model';
export interface INotification {
  id?: number;
  username?: string;
  token?: string;
  subject?: string;
  content?: string;
  image?: string | null;
  status?: MessageStatus | null;
  createdAt?: Date;
  createdBy?: string;
  data?: INotificationData[] | null;
}

export class Notification implements INotification {
  constructor(
    public id?: number,
    public username?: string,
    public token?: string,
    public subject?: string,
    public content?: string,
    public image?: string | null,
    public data?: INotificationData[] | null
  ) {}
}

export interface IReceiver {
  username?: string;
  token?: string;
}

export class Receiver implements IReceiver {
  constructor(public username?: string, public token?: string) {}
}

export interface INotifications {
  receivers?: IReceiver[] | null;
  subject?: string;
  content?: string;
  image?: string | null;
  data?: INotificationData[] | null;
}

export class Notifications implements INotifications {
  constructor(
    public receivers?: IReceiver[] | null,
    public subject?: string,
    public content?: string,
    public image?: string | null,
    public status?: MessageStatus | null,
    public createdAt?: Date,
    public createdBy?: string,
    public data?: INotificationData[] | null
  ) {}
}
