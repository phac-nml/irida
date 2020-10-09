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
          {i18n("TimePeriodSelect.day")}
        </Select.Option>
        <Select.Option value={7}>
          {i18n("TimePeriodSelect.week")}
        </Select.Option>
        <Select.Option value={14}>
          {i18n("TimePeriodSelect.twoWeeks")}
        </Select.Option>
        <Select.Option value={30}>
          {i18n("TimePeriodSelect.month")}
        </Select.Option>
        <Select.Option value={90}>
          {i18n("TimePeriodSelect.quarter")}
        </Select.Option>
        <Select.Option value={365}>
          {i18n("TimePeriodSelect.year")}
        </Select.Option>
        <Select.Option value={730}>
          {i18n("TimePeriodSelect.twoYears")}
        </Select.Option>
        <Select.Option value={1825}>
          {i18n("TimePeriodSelect.fiveYears")}
        </Select.Option>
        <Select.Option value={3650}>
          {i18n("TimePeriodSelect.tenYears")}
        </Select.Option>
      </Select>
    </Form.Item>
  );
}
