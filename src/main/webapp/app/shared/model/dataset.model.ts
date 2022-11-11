export interface IDataset {
  label?: string;
  backgroundColor?: string;
  data?: Array<number>;
}

export class Dataset implements IDataset {
  constructor(public label?: string, public backgroundColor?: string, public data?: Array<number>) {}
}
