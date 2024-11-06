/*
 * This file is used to set constants used
 * by the statistics pages.
 */

// The type of charts the data can be viewed in
export const chartTypes = {
  BAR: "bar",
  COLUMN: "column",
  DONUT: "donut",
  LINE: "line",
  PIE: "pie",
  TINYCOLUMN: "tinyColumn",
};

// The possible statistic types
export const statisticTypes = {
  ANALYSES: "analyses",
  PROJECTS: "projects",
  SAMPLES: "samples",
  USERS: "users",
};

/* Default is to display statistics for the last week. If
 * this is changed then you will also need to update the
 * defautTimePeriodText below to the proper text.
 */
export const defaultTimePeriod = 7;

/*
 * Default text for the default time period. Used by cards
 * title in basicstats component.
 */
export const defaultTimePeriodText = "week";

// Default is a column (bar chart)
export const defaultChartType = chartTypes.COLUMN;
