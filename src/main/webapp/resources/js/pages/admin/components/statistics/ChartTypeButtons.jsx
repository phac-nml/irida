import React from "react";
import { Radio } from "antd";

/**
 * React component for displaying chart type buttons
 * @param {function} [onChange] - called when the component changes value
 * @param {String} value - Select radio group button value
 * @returns {JSX.Element}
 * @constructor
 */
export function ChartTypeButtons({ onChange = () => {}, value }) {

  const buttonOptions = [
    { label: 'Bar Chart', value: 'bar' },
    { label: 'Column Chart', value: 'column' },
    { label: 'Line Chart', value: 'line' },
    { label: 'Pie Chart', value: 'pie' },
    { label: 'Donut Chart', value: 'donut' },
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
