import React from "react";
import { Form, Radio } from "antd";
import { chartTypes } from "../../statistics-constants";

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
    { label: 'Line Chart', value: chartTypes.LINE },
    { label: 'Pie Chart', value: chartTypes.PIE }
  ];

  return (
    <Form.Item
      label ={
        <span>
            Chart Type
          </span>
      }
      name="chart-type"
    >
      <Radio.Group
        options={buttonOptions}
        onChange={onChange}
        value={value}
        optionType="button"
      />
    </Form.Item>
  );
}
