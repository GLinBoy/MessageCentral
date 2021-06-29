export interface IEmail {
  id?: number;
  receiver?: string;
  subject?: string;
  content?: string;
}

export class Email implements IEmail {
  constructor(public id?: number, public receiver?: string, public subject?: string, public content?: string) {}
}
