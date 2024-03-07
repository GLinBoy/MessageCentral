import { type MessageStatus } from '@/shared/model/enumerations/message-status.model';

export interface IShortMessage {
  id?: number;
  phoneNumber?: string;
  content?: string;
  status?: keyof typeof MessageStatus | null;
  createdAt?: Date;
  createdBy?: string;
}

export class ShortMessage implements IShortMessage {
  constructor(
    public id?: number,
    public phoneNumber?: string,
    public content?: string,
    public status?: keyof typeof MessageStatus | null,
    public createdAt?: Date,
    public createdBy?: string,
  ) {}
}

export interface IShortMessages {
  phoneNumbers?: string[];
  content?: string;
}

export class ShortMessages implements IShortMessages {
  constructor(
    public phoneNumbers?: string[],
    public content?: string,
  ) {}
}
