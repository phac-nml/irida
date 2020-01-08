import React from "react";
import { Switch } from "antd";

export function RemoteApiStatus({}) {
  return (
    <Switch
      checkedChildren={<span>Connected</span>}
      unCheckedChildren={<span>Connect</span>}
    />
  );
}
