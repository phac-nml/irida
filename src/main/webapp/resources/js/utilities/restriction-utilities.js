/**
 * @fileoverview utilities to help with metadata restrictions
 */

/**
 * Default colours for restriction level
 */
const COLOURS = {
  LEVEL_1: "green",
  LEVEL_2: "orange",
  LEVEL_3: "volcano",
  LEVEL_4: "red",
};

const LEVELS = ["LEVEL_1", "LEVEL_2", "LEVEL_3", "LEVEL_4"];

/**
 * Get a colour assoc with a specific restriction. Use for consistency.
 * @param restriction
 * @returns {*}
 */
export const getColourForRestriction = (restriction) => COLOURS[restriction];

/**
 * Compare two metadata restrictions.
 *
 * Right now levels can be easily compared by their string values (e.g. "LEVEL_1 < LEVEL_2"
 * would evaluate to true).  This helper function is here incase those values ever change
 * so we do not need to go everywhere in the code to update.
 *
 * @param level1 - existing restriction
 * @param level2 - target restriction
 * @returns {number}
 */
export function compareRestrictionLevels(level1, level2) {
  return LEVELS.indexOf(level2) - LEVELS.indexOf(level1);
}
