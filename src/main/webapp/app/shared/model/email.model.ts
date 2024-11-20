import { type MessageStatus } from '@/shared/model/enumerations/message-status.model';
import { type EmailType } from '@/shared/model/enumerations/email-type.model';
export interface IEmail {
  id?: number;
  receiver?: string;
  subject?: string;
  content?: string;
  status?: keyof typeof MessageStatus | null;
  emailType?: keyof typeof EmailType | null;
  createdAt?: Date;
  createdBy?: string;
}

export class Email implements IEmail {
  constructor(
    public id?: number,
    public receiver?: string,
    public subject?: string,
    public content?: string,
    public status?: keyof typeof MessageStatus | null,
    public emailType?: keyof typeof EmailType | null,
    public createdAt?: Date,
    public createdBy?: string,
  ) {}
}
