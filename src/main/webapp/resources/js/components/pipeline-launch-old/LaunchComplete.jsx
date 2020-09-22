import React from "react";
import { Result } from "antd";
import { useLaunchState } from "./launch-context";

export function LaunchComplete() {
  const { modified } = useLaunchState();
  return (
    <Result
      status={"success"}
      title={"PIPELINE HAS BEEN LAUNCH"}
      subTitle={"Grab a coffee and go chill, you are done!"}
    />
  );
}
