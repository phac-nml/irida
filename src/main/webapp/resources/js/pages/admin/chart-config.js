import merge from "lodash/merge";
import {
  chartTypes,
  statisticTypes,
  timePeriodMap
} from "../../contexts/AdminStatisticsContext";

const chartHeight = 800;

/*
   * Gets the config required for the chart
   * @param chartType - The type of chart (bar, column, line, or pie)
   * @param statsType - The type of statistics (projects, analyses, samples, users)
   * @param timePeriod - The time period for the statistics
   * @param statistics - The data for the chart
   */
export function getChartConfiguration(chartType, statsType, timePeriod, statistics) {
  let data = null;
  const timePeriodText = timePeriodMap[timePeriod];

  let chartTitle = "";
  let chartAxisAlias = "";

  if(statsType === statisticTypes.ANALYSES) {
    chartTitle = i18n("AdminPanelStatistics.titleAnalyses", timePeriodText);
    chartAxisAlias = i18n("AdminPanelStatistics.chartAxisAliasAnalyses");
    data = statistics.analysesStats;
  } else if (statsType === statisticTypes.PROJECTS) {
    chartTitle = i18n("AdminPanelStatistics.titleProjects", timePeriodText);
    chartAxisAlias = i18n("AdminPanelStatistics.chartAxisAliasProjects");
    data = statistics.projectStats;
  } else if (statsType === statisticTypes.SAMPLES) {
    chartTitle = i18n("AdminPanelStatistics.titleSamples", timePeriodText);
    chartAxisAlias = i18n("AdminPanelStatistics.chartAxisAliasSamples");
    data = statistics.sampleStats;
  } else if (statsType === statisticTypes.USERS) {
    chartTitle = i18n("AdminPanelStatistics.titleUsersCreated", timePeriodText);
    chartAxisAlias = i18n("AdminPanelStatistics.chartAxisAliasUsers");
    data = statistics.userStats;
  }

  // Creates a const of non common values for the chart type
  const customChartTypeConfig = {
    [chartTypes.BAR]: {
      xField: 'value',
      yField: 'key',
      label: {
        position: 'right',
      },
    },
    [chartTypes.DONUT]: {
      label: {
        visible: false,
      },
      statistic: {
        visible: Boolean(data),
        content: {
          value: '',
          name: '',
        },
      },
      angleField:'value',
    },
    [chartTypes.PIE]: {
      label: {
        visible: false,
      },
      angleField:'value',
    }
  };

  // The configuration required to display a chart
  const config = {
    title: { visible: true, text: chartTitle },
    height: chartHeight,
    forceFit: true,
    data: data || [{key:"", value:""}],
    padding: 'auto',
    xField: 'key',
    yField: 'value',
    meta: { key: { alias: i18n("AdminPanelStatistics.chartAxisAliasTimePeriod") }, value: { alias: chartAxisAlias } },
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
    }
  };

  return merge(config, customChartTypeConfig[chartType]);
}