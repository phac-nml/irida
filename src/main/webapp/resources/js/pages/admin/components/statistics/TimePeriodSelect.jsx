import React from "react";
import { Form, Select } from "antd";

/**
 * React component for updating the time period for statistics.
 * @param {string} [name] - form name value
 * @param {function} [onChange] - called when the component changes value
 * @returns {JSX.Element}
 * @constructor
 */
export function TimePeriodSelect({ onChange = () => {} }) {
  return (
    <Form.Item
        label ={
          <span>
            Time Period
          </span>
        }
      name="time-period"
    >
      <Select onChange={onChange} className={"t-time-period"} style={{width: 120}}>
        <Select.Option value={1}>
          Day
        </Select.Option>
        <Select.Option value={7}>
          Week
        </Select.Option>
        <Select.Option value={14}>
          2 Weeks
        </Select.Option>
        <Select.Option value={30}>
          Month
        </Select.Option>
        <Select.Option value={90}>
          Quarterly
        </Select.Option>
        <Select.Option value={365}>
          1 Year
        </Select.Option>
        <Select.Option value={730}>
          2 Years
        </Select.Option>
        <Select.Option value={1825}>
          5 Years
        </Select.Option>
        <Select.Option value={3650}>
          10 Years
        </Select.Option>
      </Select>
    </Form.Item>
  );
}
