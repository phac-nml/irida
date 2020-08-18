import React from "react";
import { Button } from "antd";
import { IconRocket } from "../icons/Icons";
import { useLaunchDispatch } from "./launch-context";
import { DISPATCH_LAUNCH_PIPELINE } from "./lauch-constants";

export function PipelineLaunchButton() {
  const dispatch = useLaunchDispatch();

  const clickHandler = () => {
    // TODO: implement launch details here
    dispatch({ type: DISPATCH_LAUNCH_PIPELINE });
  };

  return (
    <Button type="primary" danger onClick={clickHandler} icon={<IconRocket />}>
      LAUNCH PIPELINE
    </Button>
  );
}
