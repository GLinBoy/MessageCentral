import { type MessageStatus } from '@/shared/model/enumerations/message-status.model';
export interface INotification {
  id?: number;
  username?: string;
  token?: string;
  subject?: string;
  content?: string;
  image?: string | null;
  status?: keyof typeof MessageStatus | null;
  createdAt?: Date;
  createdBy?: string;
}

export class Notification implements INotification {
  constructor(
    public id?: number,
    public username?: string,
    public token?: string,
    public subject?: string,
    public content?: string,
    public image?: string | null,
    public status?: keyof typeof MessageStatus | null,
    public createdAt?: Date,
    public createdBy?: string,
  ) {}
}
