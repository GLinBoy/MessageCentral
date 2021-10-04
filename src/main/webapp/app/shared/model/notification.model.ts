import { INotificationData } from '@/shared/model/notification-data.model';

export interface INotification {
  id?: number;
  username?: string;
  token?: string;
  subject?: string;
  content?: string;
  image?: string | null;
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

export interface IReceivers {
  username?: string;
  token?: string;
}

export class Receivers implements IReceivers {
  constructor(
    public username?: string,
    public token?: string,
  ) {}
}
