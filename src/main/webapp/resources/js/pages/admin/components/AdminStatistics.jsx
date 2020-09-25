/*
 * This file renders the Statistics component
 */

import React, { useRef } from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import {
  Bar,
  Column,
  Histogram,
  Line,
  Pie,
  Donut
} from '@ant-design/charts';
import { AnalysesQueue } from "../../../components/AnalysesQueue";

const data = [
  { year: '2012', numProjects: 38 },
  { year: '2013', numProjects: 52 },
  { year: '2014', numProjects: 61 },
  { year: '2015', numProjects: 145 },
  { year: '2016', numProjects: 221 },
  { year: '2017', numProjects: 421 },
  { year: '2018', numProjects: 801 },
  { year: '2019', numProjects: 1607 },
];

const barChartData = [
  { year: '2019', numProjects: 1607 },
  { year: '2018', numProjects: 801 },
  { year: '2017', numProjects: 421 },
  { year: '2016', numProjects: 221 },
  { year: '2015', numProjects: 145 },
  { year: '2014', numProjects: 61 },
  { year: '2013', numProjects: 52 },
  { year: '2012', numProjects: 38 },
  { year: "", numProjects: "" },
];

const data2 = [
  { year: '2012', numProjects: 38 },
  { year: '2013', numProjects: 52 },
  { year: '2014', numProjects: 61 },
  { year: '2015', numProjects: 145 },
  { year: '2016', numProjects: 221 },
  { year: '2017', numProjects: 421 },
  { year: '2018', numProjects: 801 },
  { year: '2019', numProjects: 1607 },
];

const histogramData = [
  { value: 1.2 },
  { value: 3.4 },
  { value: 3.7 },
  { value: 4.3 },
  { value: 5.2 },
  { value: 5.8 },
  { value: 6.1 },
  { value: 6.5 },
  { value: 6.8 },
  { value: 7.1 },
  { value: 7.3 },
  { value: 7.7 },
  { value: 8.3 },
  { value: 8.6 },
  { value: 8.8 },
  { value: 9.1 },
  { value: 9.2 },
  { value: 9.4 },
  { value: 9.5 },
  { value: 9.7 },
  { value: 10.5 },
  { value: 10.7 },
  { value: 10.8 },
  { value: 11 },
  { value: 11 },
  { value: 11.1 },
  { value: 11.2 },
  { value: 11.3 },
  { value: 11.4 },
  { value: 11.4 },
  { value: 11.7 },
  { value: 12 },
  { value: 12.9 },
  { value: 12.9 },
  { value: 13.3 },
  { value: 13.7 },
  { value: 13.8 },
  { value: 13.9 },
  { value: 14 },
  { value: 14.2 },
  { value: 14.5 },
  { value: 15 },
  { value: 15.2 },
  { value: 15.6 },
  { value: 16 },
  { value: 16.3 },
  { value: 17.3 },
  { value: 17.5 },
  { value: 17.9 },
  { value: 18 },
  { value: 18 },
  { value: 20.6 },
  { value: 21 },
  { value: 23.4 },
];

const data4 = [
  { year: '2012', numProjects: 38 },
  { year: '2013', numProjects: 52 },
  { year: '2014', numProjects: 61 },
  { year: '2015', numProjects: 145 },
  { year: '2016', numProjects: 221 },
  { year: '2017', numProjects: 421 },
  { year: '2018', numProjects: 801 },
  { year: '2019', numProjects: 1607 }
];

const donutData = [
  { year: '2012', numProjects: 38 },
  { year: '2013', numProjects: 52 },
  { year: '2014', numProjects: 61 },
  { year: '2015', numProjects: 145 },
  { year: '2016', numProjects: 221 },
  { year: '2017', numProjects: 421 },
  { year: '2018', numProjects: 801 },
  { year: '2019', numProjects: 1607 }
];

const totalNumProjects = 3346;


const lineConfig = {
  data: data,
  title: {
    visible: true,
    text: 'Number of Projects by Year',
  },
  xField: 'year',
  yField: 'numProjects',
  label: {
    visible: true,
  },
  meta: { year: { alias: 'Year' }, numProjects: { alias: '# of Projects' } },
};

const barConfig = {
  data: barChartData,
  title: {
    visible: true,
    text: 'Number of Projects by Year',
  },
  meta: { year: { alias: 'Year' }, numProjects: { alias: '# of Projects' } },
  xField: 'numProjects',
  yField: 'year',
  colorField: "year",
  label: {
    visible: true,
    position: 'middle', // options: left / middle / right
    adjustColor: true,
  },
};

const pieConfig = {
  data: data2,
  title: {
    visible: true,
    text: 'Number of Projects by Year',
  },
  label: {
    visible: false,
  },
  angleField:"numProjects",
  colorField:"year",
  legend: {
    visible: true,
    position: 'bottom-center',
  },
};

const histogramConfig = {
  data: histogramData,
  title: {
    visible: true,
    text: 'Test Histogram Chart',
  },
  meta: { range: { alias: 'Year' }, count: { alias: '# of Projects' } },
  padding: 'auto',
  binField: 'value',
  binNumber: 6,
  forceFit: true,
};


const colConfig = {
  title: { visible: true, text: 'Number of Projects by Year' },
  forceFit: true,
  data: data4,
  padding: 'auto',
  xField: 'year',
  yField: 'numProjects',
  meta: { year: { alias: 'Year' }, numProjects: { alias: '# of Projects' } },
  label: {
    visible: true,
    style: { fill: '#0D0E68', fontSize: 12, fontWeight: 600, opacity: 0.3 },
  },
  colorField: "year",
  legend: {
    visible: false
  },
};

const donutConfig = {
  data: donutData,
  forceFit: true,
  title: { visible: true, text: 'Number of Projects by Year' },
  radius: 0.8,
  padding: 'auto',
  angleField: 'numProjects',
  colorField: 'year',
  label: {
    visible: false
  },
  statistic: {
    visible: true,
    content: {
      value: totalNumProjects,
      name: 'Total',
    },
  },
  legend: {
    visible: true,
    position: 'bottom-center',
  },
};


export default function AdminStatistics() {
  const ref = useRef();
  return (
    <PageWrapper
      title={i18n("AdminPanel.statistics")}
      headerExtras={<AnalysesQueue />}
    >
      <div>
        <Line {...lineConfig} chartRef={ref} />
      </div>

      <div>
        <Bar {...barConfig} chartRef={ref} />
      </div>

      <div>
        <Pie {...pieConfig } chartRef={ref} />
      </div>

      <div>
        <Histogram {...histogramConfig} chartRef={ref} />
      </div>

      <div>
        <Column {...colConfig} chartRef={ref} />
      </div>

      <Donut {...donutConfig} chartRef={ref} />
    </PageWrapper>
  );
}