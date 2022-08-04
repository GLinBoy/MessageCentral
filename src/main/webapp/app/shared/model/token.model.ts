export interface IToken {
  id?: number;
  name?: string;
  token?: string;
  disable?: boolean;
  createdAt?: Date;
  deprecateAt?: Date;
  roles?: number;
  createdBy?: string;
  updatedAt?: Date;
  updatedBy?: string;
}

export class Token implements IToken {
  constructor(
    public id?: number,
    public name?: string,
    public token?: string,
    public disable?: boolean,
    public createdAt?: Date,
    public deprecateAt?: Date,
    public roles?: number,
    public createdBy?: string,
    public updatedAt?: Date,
    public updatedBy?: string
  ) {
    this.disable = this.disable ?? false;
  }
}
