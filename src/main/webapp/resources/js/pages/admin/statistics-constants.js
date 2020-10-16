export const timePeriodMap = {
  1: "day",
  7: "week",
  14: "2 weeks",
  30: "month",
  90: "quarter",
  365: "year",
  730: "2 years",
  1825: "5 years",
  3650: "10 years"
};

export const chartTypes = {
  BAR: "bar",
  COLUMN: "column",
  DONUT: "donut",
  LINE: "line",
  PIE: "pie",
  TINYCOLUMN: "tinyColumn"
}

export const statisticTypes = {
  ANALYSES: "analyses",
  PROJECTS: "projects",
  SAMPLES: "samples",
  USERS: "users"
}

// Default is to display statistics for the last week
export const defaultTimePeriod = 7;

export const defaultChartType = chartTypes.COLUMN;