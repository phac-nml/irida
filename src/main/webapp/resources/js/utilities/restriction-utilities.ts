/**
 * @fileoverview utilities to help with metadata restrictions
 */
export type Restriction = "LEVEL_1" | "LEVEL_2" | "LEVEL_3" | "LEVEL_4";
export interface RestrictionListItem {
  label: string;
  value: string;
}
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
export const getColourForRestriction = (restriction: Restriction) =>
  COLOURS[restriction];

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
export function compareRestrictionLevels(
  level1: Restriction,
  level2: Restriction
) {
  return LEVELS.indexOf(level2) - LEVELS.indexOf(level1);
}

/**
 * Get the label if the value matches.
 * @param restrictions
 * @param value
 *
 * @returns {string}
 */
export function getRestrictionLabel(
  restrictions: RestrictionListItem[],
  value: string
) {
  const restriction = restrictions?.find(
    (restriction) => restriction.value === value
  );
  return restriction?.label;
}
