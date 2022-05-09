import uniqolor from "uniqolor";
import { v5 as uuidv5 } from "uuid";

/**
 * Create a unique color for any IRIDA thing (project, sample, etc.)
 * @param id identifier for the thing
 * @param label label for the thing
 * @returns {{background: *, text: *}}
 */
export function generateColourForItem({ id, label }) {
  const formatted = uuidv5(`${id}`, "1b671a64-40d5-491e-99b0-da01ff1f3341");
  const name = `${formatted}-${label}`;

  let background = uniqolor(name, {
    lightness: 90,
    saturation: 80,
  });

  let colour = uniqolor(name, {
    lightness: 20,
  });
  return { background: background.color, text: colour.color };
}
