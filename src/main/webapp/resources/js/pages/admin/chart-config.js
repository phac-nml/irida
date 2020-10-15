import merge from "lodash/merge";
import {
  chartTypes,
  statisticTypes
} from "../../contexts/AdminStatisticsContext";

const chartHeight = 800;

/*
 * Gets the config required for the chart
 * @param chartType - The type of chart (bar, column, line, or pie)
 * @param statsType - The type of statistics (projects, analyses, samples, users)
 * @param statistics - The data for the chart
 */
export function getChartConfiguration(chartType, statsType, statistics) {
  let data = [{"key":"", "value":""}];

  if(statsType === statisticTypes.ANALYSES) {
    data = statistics.analysesStats;
  } else if (statsType === statisticTypes.PROJECTS) {
    data = statistics.projectStats;
  } else if (statsType === statisticTypes.SAMPLES)
  {
    data = statistics.sampleStats;
  } else if (statsType === statisticTypes.USERS) {
    data = statistics.userStats;
  }

  /*
   * Tiny charts only require an array of values rather than
   * an array of objects.
   */
  if(chartType === chartTypes.TINYCOLUMN) {
    data = data.map(obj => obj.value);
  }

  const customChartTypeConfig = {
    [chartTypes.BAR]: {
      xField: "value",
      yField: "key",
    },
    [chartTypes.PIE]: {
      appendPadding: 10,
      colorField: 'key',
      radius: 0.8,
      angleField: 'value',
      label: {
        content: ''
      },
    },
    [chartTypes.COLUMN]: {},
    [chartTypes.LINE]: {
      colorField: '',
    },
    [chartTypes.TINYCOLUMN]: {
      title: { visible: false},
      legend: {
        visible: false,
      },
      autoFit: true,
      height: 80,
      columnWidthRatio: 0.5,
    },
  };

  // The configuration required to display a chart
  const config = {
    data: data,
    padding: 'auto',
    xField: 'key',
    yField: 'value',
    width: "100%",
    height: 800,
    meta: { key: { alias: '' }, value: { alias: '' } },
    label: {
      visible: Boolean(data),
      position: 'middle',
      adjustColor: true,
      style: { fill: '#0D0E68', fontSize: 12, fontWeight: 600, opacity: 0.3 },
    },
    colorField: 'key',
    legend: {
      visible: Boolean(data),
      position: 'bottom',
    },
  };

  return merge(config, customChartTypeConfig[chartType]);

}