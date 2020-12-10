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
    { label: i18n("ChartTypeButtons.barChart"), value: chartTypes.BAR },
    { label: i18n("ChartTypeButtons.columnChart"), value: chartTypes.COLUMN },
    { label: i18n("ChartTypeButtons.lineChart"), value: chartTypes.LINE },
    { label: i18n("ChartTypeButtons.pieChart"), value: chartTypes.PIE },
  ];

  return (
    <Form.Item
      label={<span>{i18n("ChartTypeButtons.chartType")}</span>}
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
