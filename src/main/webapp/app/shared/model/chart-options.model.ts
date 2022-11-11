export interface IChartOptions {
  responsive?: boolean;
}

export class ChartOptions implements IChartOptions {
  constructor(public responsive?: boolean) {}
}
