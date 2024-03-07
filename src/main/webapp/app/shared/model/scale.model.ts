import { type ITicks, Ticks } from '@/shared/model/ticks.model';

export interface IScale {
  ticks?: ITicks;
}

export class Scale implements IScale {
  constructor(public ticks?: ITicks) {
    this.ticks = new Ticks();
  }
}
