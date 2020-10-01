import React from "react";
import { Radio } from "antd";
import { chartTypes } from "../../../../contexts/AdminStatisticsContext";

/**
 * React component for displaying chart type buttons
 * @param {function} [onChange] - called when the component changes value
 * @param {String} value - Select radio group button value
 * @returns {JSX.Element}
 * @constructor
 */
export function ChartTypeButtons({ onChange = () => {}, value }) {

  const buttonOptions = [
    { label: 'Bar Chart', value: chartTypes.BAR },
    { label: 'Column Chart', value: chartTypes.COLUMN },
    { label: 'Donut Chart', value: chartTypes.DONUT },
    { label: 'Line Chart', value: chartTypes.LINE },
    { label: 'Pie Chart', value: chartTypes.PIE }
  ];

  return (
    <Radio.Group
      options={buttonOptions}
      onChange={onChange}
      value={value}
      optionType="button"
    />
  );
}
