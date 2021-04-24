export interface INotification {
  id?: number;
  username?: string;
  token?: string;
  subject?: string;
  content?: string;
  image?: string | null;
}

export class Notification implements INotification {
  constructor(
    public id?: number,
    public username?: string,
    public token?: string,
    public subject?: string,
    public content?: string,
    public image?: string | null
  ) {}
}
