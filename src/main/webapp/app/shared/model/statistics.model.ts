export interface IStatistics {
  successful?: number;
  failed?: number;
}

export class Statistics implements IStatistics {
  constructor(public successful?: number, public failed?: number) {}
}
