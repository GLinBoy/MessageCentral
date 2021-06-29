export interface IShortMessage {
  id?: number;
  phoneNumber?: string;
  content?: string;
}

export class ShortMessage implements IShortMessage {
  constructor(public id?: number, public phoneNumber?: string, public content?: string) {}
}
