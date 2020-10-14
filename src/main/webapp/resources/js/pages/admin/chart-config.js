import merge from "lodash/merge";
import {
  chartTypes,
  statisticTypes,
  timePeriodMap
} from "../../contexts/AdminStatisticsContext";

/*
   * Gets the config required for the chart
   * @param chartType - The type of chart (bar, column, line, or pie)
   * @param data - The data for the chart
   * @param statsType - The type of statistics (projects, analyses, samples, users)
   * @param timePeriod - The time period for the statistics
   */
export function getChartConfiguration(chartType, statsType, timePeriod, statistics) {
  let data = [{"key":"", "value":""}];
  const timePeriodText = timePeriodMap[timePeriod];

  let chartTitle = "";
  let chartAxisAlias = "";

  if(statsType === statisticTypes.ANALYSES) {
    chartTitle = `Number of analyses run in past ${timePeriodText}`;
    chartAxisAlias = '# of Analyses';
    data = statistics.analysesStats;
  } else if (statsType === statisticTypes.PROJECTS) {
    chartTitle = `Number of projects created in past ${timePeriodText}`;
    chartAxisAlias ='# of Projects';
    data = statistics.projectStats;
  } else if (statsType === statisticTypes.SAMPLES)
  {
    chartTitle = `Number of samples created in past ${timePeriodText}`;
    chartAxisAlias = '# of Samples';
    data = statistics.sampleStats;
  } else if (statsType === statisticTypes.USERS) {
    chartTitle =`Number of users created in past ${timePeriodText}`;
    chartAxisAlias = '# of Users';
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
      label: {
        position: "right",
      },
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
    [chartTypes.LINE]: {},
    [chartTypes.TINYCOLUMN]: {
      title: { visible: false},
      legend: {
        visible: false,
      },
      autoFit: true,
      height: 80,
      columnWidthRatio: 0.7,
    }
  };

  // The configuration required to display a chart
  const config = {
    title: { visible: Boolean(data), text: chartTitle },
    forceFit: true,
    data: data,
    padding: 'auto',
    xField: 'key',
    yField: 'value',
    width: "100%",
    height: 800,
    meta: { key: { alias: "Time Period" }, value: { alias: chartAxisAlias } },
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
    statistic: {
      visible: Boolean(data),
      content: {
        value: '',
        name: '',
      },
    },
  };

  return merge(config, customChartTypeConfig[chartType]);

}