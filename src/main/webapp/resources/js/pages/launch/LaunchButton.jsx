import React from "react";
import {
  IconLaunchPipeline,
  IconPlusCircle,
} from "../../components/icons/Icons";
import { SPACE_LG } from "../../styles/spacing";
import { Button, Checkbox, Form, Space } from "antd";
import { useLaunch } from "./launch-context";

/**
 * Launch button can change depending on whether it is an automated pipeline or
 * a standard launch.  This will render the appropriate button.
 * @param disabled
 * @param loading
 * @returns {JSX.Element}
 * @constructor
 */
export function LaunchButton({ disabled, loading }) {
  const [{ automatedId }] = useLaunch();

  const props = automatedId
    ? {
        icon: <IconPlusCircle />,
      }
    : {
        icon: <IconLaunchPipeline />,
      };
  const label = automatedId
    ? i18n("LaunchButton.submit-automated")
    : i18n("LaunchButton.submit");

  return (
    <Space direction="vertical" style={{ marginTop: SPACE_LG }}>
      <Form.Item name="keepSamples" valuePropName="checked" noStyle>
        <Checkbox>Keep samples after launch</Checkbox>
      </Form.Item>
      <Button
        type="primary"
        className="t-submit-btn"
        size="large"
        htmlType="submit"
        loading={loading}
        disabled={disabled}
        {...props}
      >
        {label}
      </Button>
    </Space>
  );
}
