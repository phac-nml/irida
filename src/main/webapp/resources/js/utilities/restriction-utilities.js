const COLOURS = {
  LEVEL_1: "green",
  LEVEL_2: "orange",
  LEVEL_3: "volcano",
  LEVEL_4: "red",
};

const LEVELS = ["LEVEL_1", "LEVEL_2", "LEVEL_3", "LEVEL_4"];

export const getColourForRestriction = (restriction) => COLOURS[restriction];

export function compareRestrictionLevels(level1, level2) {
  return LEVELS.indexOf(level2) - LEVELS.indexOf(level1);
}
