import React from "react";
import {
  IconLaunchPipeline,
  IconPlusCircle,
} from "../../components/icons/Icons";
import { SPACE_LG } from "../../styles/spacing";
import { Button } from "antd";
import { useLaunch } from "./launch-context";

export function LaunchButton({ disabled, loading }) {
  const [{ automatedId }] = useLaunch();
  return automatedId ? (
    <Button
      type="primary"
      className="t-submit-btn"
      size="large"
      htmlType="submit"
      loading={loading}
      disabled={disabled}
      icon={<IconPlusCircle />}
      style={{ marginTop: SPACE_LG }}
    >
      {i18n("LaunchButton.submit-automated")}
    </Button>
  ) : (
    <Button
      type="primary"
      className="t-submit-btn"
      size="large"
      htmlType="submit"
      icon={<IconLaunchPipeline />}
      loading={loading}
      style={{ marginTop: SPACE_LG }}
      disabled={disabled}
    >
      {i18n("LaunchButton.submit")}
    </Button>
  );
}
