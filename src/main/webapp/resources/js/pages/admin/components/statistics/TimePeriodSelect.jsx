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
    { label: i18n("TimePeriodSelect.day"), value: 1 },
    { label: i18n("TimePeriodSelect.week"), value: 7 },
    { label: i18n("TimePeriodSelect.twoWeeks"), value: 14 },
    { label: i18n("TimePeriodSelect.month"), value: 30 },
    { label: i18n("TimePeriodSelect.quarter"), value: 90 },
    { label: i18n("TimePeriodSelect.year"), value: 365 },
    { label: i18n("TimePeriodSelect.twoYears"), value: 730 },
    { label: i18n("TimePeriodSelect.fiveYears"), value: 1825 },
    { label: i18n("TimePeriodSelect.tenYears"), value: 3650 },
  ];

  return (
    <Form.Item
      label={<span>{i18n("TimePeriodSelect.timePeriod")}</span>}
      name="time-period"
    >
      <Radio.Group options={options} onChange={onChange} optionType="button" />
    </Form.Item>
  );
}
