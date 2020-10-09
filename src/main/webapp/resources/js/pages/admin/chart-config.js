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
export function getChartConfiguration(chartType, statsType, timePeriod) {
  let data = null;
  const timePeriodText = timePeriodMap[timePeriod];

  let chartTitle = "";
  let chartAxisAlias = "";

  if(statsType === statisticTypes.ANALYSES) {
    chartTitle = `Number of analyses run in past ${timePeriodText}`;
    chartAxisAlias = '# of Analyses';
  } else if (statsType === statisticTypes.PROJECTS) {
    chartTitle = `Number of projects created in past ${timePeriodText}`;
    chartAxisAlias ='# of Projects';
  } else if (statsType === statisticTypes.SAMPLES)
  {
    chartTitle = `Number of samples created in past ${timePeriodText}`;
    chartAxisAlias = '# of Samples';
  } else if (statsType === statisticTypes.USERS) {
    chartTitle =`Number of users created in past ${timePeriodText}`;
    chartAxisAlias = '# of Users';
  }

  const customChartTypeConfig = {
    [chartTypes.BAR]: {
      xField: "value",
      yField: "key",
      label: {
        position: "right",
      },
    },
    [chartTypes.DONUT]: {
      visible: false,
    },
    [chartTypes.PIE]: {
      visible: false,
    },
    [chartTypes.COLUMN]: {},
    [chartTypes.LINE]: {},
  };

  // The configuration required to display a chart
  const config = {
    title: { visible: true, text: chartTitle },
    forceFit: true,
    data: data || [{key:"", value:""}],
    padding: 'auto',
    xField: 'key',
    yField: 'value',
    meta: { key: { alias: 'Time Period' }, value: { alias: chartAxisAlias } },
    angleField:"value",
    label: {
      visible: Boolean(data),
      position: 'middle',
      adjustColor: true,
      style: { fill: '#0D0E68', fontSize: 12, fontWeight: 600, opacity: 0.3 },
    },
    colorField: 'key',
    legend: {
      visible: Boolean(data),
      position: 'bottom-center',
    },
    statistic: {
      visible: Boolean(data),
      content: {
        value: '',
        name: '',
      },
    },
  };

  return merge(config, [customChartTypeConfig[chartType]]);

}