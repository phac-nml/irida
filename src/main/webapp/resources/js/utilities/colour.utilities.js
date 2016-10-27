/**
 * From https://material.google.com/style/color.html#color-color-palette
 * Light Blue (800)
 * Pink (800)
 * Cyan (800)
 * Deep Purple (300)
 * Blue (200)
 * Teal (200)
 */
const COLOUR_LIST = ['#0277BD', '#AD1457', '#00838F', '#9575CD', '#90CAF9', '#4DB6AC'];

export class Colours {
  constructor() {
    this.current = 0;
  }

  getNext() {
    const colour = COLOUR_LIST[this.current];
    this.current = this.current + 1;
    if (this.current === COLOUR_LIST.length) {
      this.current = 0;
    }
    return colour;
  }
}
