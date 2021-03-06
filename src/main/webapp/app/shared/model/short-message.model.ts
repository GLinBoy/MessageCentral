import { MessageStatus } from '@/shared/model/enumerations/message-status.model';
export interface IShortMessage {
  id?: number;
  phoneNumber?: string;
  content?: string;
  status?: MessageStatus | null;
  createdAt?: Date;
  createdBy?: string;
}

export class ShortMessage implements IShortMessage {
  constructor(public id?: number, public phoneNumber?: string, public content?: string, public status?: MessageStatus | null) {}
}

export interface IShortMessages {
  phoneNumbers?: string[];
  content?: string;
  status?: MessageStatus | null;
}

export class ShortMessages implements IShortMessages {
  constructor(public phoneNumbers?: string[], public content?: string, public status?: MessageStatus | null) {}
  constructor(
    public id?: number,
    public phoneNumber?: string,
    public content?: string,
    public status?: MessageStatus | null,
    public createdAt?: Date,
    public createdBy?: string
  ) {}
}
