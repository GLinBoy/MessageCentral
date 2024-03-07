import { type IScale, Scale } from '@/shared/model/scale.model';

export interface IChartOptions {
  responsive?: boolean;
  scale?: IScale;
}

export class ChartOptions implements IChartOptions {
  constructor(
    public responsive?: boolean,
    public scale?: IScale,
  ) {
    this.responsive = true;
    this.scale = new Scale();
  }
}
