import { MessageStatus } from '@/shared/model/enumerations/message-status.model';
import { EmailType } from '@/shared/model/enumerations/email-type.model';
export interface IEmail {
  id?: number;
  receiver?: string;
  subject?: string;
  content?: string;
  status?: MessageStatus | null;
  emailType?: EmailType | null;
  createdAt?: Date;
  createdBy?: string;
}

export class Email implements IEmail {
  constructor(
    public id?: number,
    public receiver?: string,
    public subject?: string,
    public content?: string,
    public status?: MessageStatus | null,
    public emailType?: EmailType | null,
    public createdAt?: Date,
    public createdBy?: string
  ) {}
}

export interface IEmails {
  receivers?: string[];
  subject?: string;
  content?: string;
  status?: MessageStatus | null;
}

export class Emails implements IEmails {
  constructor(public receivers?: string[], public subject?: string, public content?: string, public status?: MessageStatus | null) {}
}
