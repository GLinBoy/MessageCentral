export interface ITicks {
  precision?: number;
}

export class Ticks implements ITicks {
  constructor(public precision?: number) {
    this.precision = 0;
  }
}
