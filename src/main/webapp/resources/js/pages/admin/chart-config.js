import merge from "lodash/merge";
import { chartTypes } from "./statistics-constants";

const chartHeight = 800;

/*
 * Gets the config required for the chart. Data is accepted as an
 * array of objects which have a key and value.
 * @param chartType - The type of chart (bar, column, line, or pie)
 * @param data - The data for the chart
 * @param statsType - The type of statistics (projects, analyses, samples, users)
 */
export function getChartConfiguration(chartType, data) {
  const customChartTypeConfig = {
    [chartTypes.BAR]: {
      xField: "value",
      yField: "key",
      seriesField: 'key',
    },
    [chartTypes.PIE]: {
      appendPadding: 10,
      colorField: "key",
      radius: 0.8,
      angleField: "value",
      label: {
        content: "",
      },
    },
    [chartTypes.COLUMN]: {
      seriesField: 'key'
    },
    [chartTypes.LINE]: { },
  };

  // The configuration required to display a chart
  const config = {
    data: data,
    padding: "auto",
    xField: "key",
    yField: "value",
    width: "100%",
    height: chartHeight,
    meta: { key: { alias: "" }, value: { alias: "" } },
    label: {
      visible: Boolean(data),
      position: "middle",
      adjustColor: true,
      style: { fill: "#0D0E68", fontSize: 12, fontWeight: 600, opacity: 0.3 },
    },
    legend: {
      visible: Boolean(data),
      position: "bottom",
    },
  };

  return merge(config, customChartTypeConfig[chartType]);
}

// Tiny chart requires just an array of values
export function getTinyChartConfiguration(data) {
  const config = {
    data: data,
    title: { visible: false },
    legend: {
      visible: false,
    },
    autoFit: true,
    height: 80,
    columnWidthRatio: 1,
    tooltip: false,
  };

  return config;
}
