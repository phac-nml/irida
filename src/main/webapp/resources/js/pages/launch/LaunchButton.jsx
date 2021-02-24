import React from "react";
import {
  IconLaunchPipeline,
  IconPlusCircle,
} from "../../components/icons/Icons";
import { SPACE_LG } from "../../styles/spacing";
import { Button } from "antd";
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
    <Button
      type="primary"
      className="t-submit-btn"
      size="large"
      htmlType="submit"
      loading={loading}
      disabled={disabled}
      style={{ marginTop: SPACE_LG }}
      {...props}
    >
      {label}
    </Button>
  );
}
