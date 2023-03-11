import React from "react";
import { Form, Select } from "antd";
import { HelpPopover } from "../popovers";

/**
 * React component for updating the synchronization frequency of a project.
 */
export function SyncFrequencySelect({
  name = "frequency",
  onChange = () => undefined,
  labelRequired = true,
}): JSX.Element {
  return (
    <Form.Item
      label={
        labelRequired ? (
          <span>
            {i18n("SyncFrequencySelect.frequency")}
            <HelpPopover
              content={<div>{i18n("SyncFrequencySelect.frequency.help")}</div>}
            />
          </span>
        ) : null
      }
      name={name}
    >
      <Select onChange={onChange} className={"t-sync-frequency"}>
        <Select.Option value={0}>
          {i18n("SyncFrequencySelect.frequency.0")}
        </Select.Option>
        <Select.Option value={1}>
          {i18n("SyncFrequencySelect.frequency.1")}
        </Select.Option>
        <Select.Option value={2}>
          {i18n("SyncFrequencySelect.frequency.7")}
        </Select.Option>
        <Select.Option value={3}>
          {i18n("SyncFrequencySelect.frequency.30")}
        </Select.Option>
        <Select.Option value={4}>
          {i18n("SyncFrequencySelect.frequency.60")}
        </Select.Option>
        <Select.Option value={5}>
          {i18n("SyncFrequencySelect.frequency.90")}
        </Select.Option>
      </Select>
    </Form.Item>
  );
}
