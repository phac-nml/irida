import React from "react";
import { Radio } from "antd";

/**
 * React component to display simple yes/no radio
 * button group
 * @param {boolean} defaultValue - The defaultValue selected
 * @param {string} size - Optional size of buttons. Default is small
 * @param {function} onchange - The function to run on change of radio button
 * selected.
 * @returns {*}
 * @constructor
 */
export function SimpleRadioButtonGroup({
  defaultValue,
  size = "small",
  onchange = () => {},
}) {
  return (
    <>
      <Radio.Group defaultValue={defaultValue} size={size} onChange={onchange}>
        <Radio.Button value={false}>
          {i18n("SimpleRadioButtonGroup.no")}
        </Radio.Button>
        <Radio.Button value={true}>
          {i18n("SimpleRadioButtonGroup.yes")}
        </Radio.Button>
      </Radio.Group>
    </>
  );
}
