import { IDataset } from '@/shared/model/dataset.model';

export interface IChartData {
  labels?: Array<string>;
  datasets?: Array<IDataset>;
}

export class ChartData implements IChartData {
  constructor(public labels?: Array<string>, public datasets?: Array<IDataset>) {}
}
