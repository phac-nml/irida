import React from "react";
import { Form, Radio } from "antd";

/**
 * React component for updating the time period for statistics.
 * @param {string} [name] - form name value
 * @param {function} [onChange] - called when the component changes value
 * @returns {JSX.Element}
 * @constructor
 */
export function TimePeriodSelect({ onChange = () => {} }) {

  const options = [
    { label: 'Day', value: 1 },
    { label: 'Week', value: 7 },
    { label: '2 Weeks', value: 14 },
    { label: 'Month', value: 30 },
    { label: 'Quarter', value: 90 },
    { label: 'Year', value: 365 },
    { label: '2 Years', value: 730 },
    { label: '5 Years', value: 1825 },
    { label: '10 Years', value: 3650 },
  ];

  return (
    <Form.Item
        label ={
          <span>
            Time Period
          </span>
        }
      name="time-period"
    >
      <Radio.Group
        options={options}
        onChange={onChange}
        optionType="button" />
    </Form.Item>
  );
}
