/**
 * From https://material.google.com/style/color.html#color-color-palette
 * Light Blue (800)
 * Pink (800)
 * Cyan (800)
 * Deep Purple (300)
 * Blue (200)
 * Teal (200)
 * Purple (A100)
 * Lime (400)
 * Orange (200)
 * Pink (200)
 * Teal (A700)
 */
const COLOUR_LIST = new Set(['#0277BD', '#AD1457', '#00838F', '#9575CD', '#90CAF9', '#4DB6AC', '#EA80FC', '#D4E157', '#FFCC80', '#F48FB1', '#00BFA5']);

export class Colours {
  constructor() {
    this.iterator = COLOUR_LIST.values();
  }

  getNext() {
    const colour = this.iterator.next();
    console.log(colour);
  }
}
