/**
 * Generate a random hexadecimal colour
 * @return {string} random colour
 */
export const getRandomColour = () =>
  `#${Math.random()
    .toString(16)
    .slice(-6)}`;

const COLOURS = [
  "RGB(176, 190, 197)",
  "#D50000",
  "#C51162",
  "#AA00FF",
  "#6200EA",
  "#304FFE",
  "#2962FF",
  "#0091EA",
  "#00B8D4",
  "#00BFA5",
  "#00C853",
  "#64DD17",
  "#AEEA00",
  "#FFD600",
  "#FFAB00",
  "#FF6D00"
];
export class GetOrderedColour {
  constructor() {
    this.index = 0;
  }

  getNext() {
    return COLOURS[this.index++];
  }
}
