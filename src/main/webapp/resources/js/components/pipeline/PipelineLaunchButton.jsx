import React, { useState } from "react";
import { Button, Popover } from "antd";
import { IconRocket } from "../icons/Icons";
import {
  useLaunchDispatch,
  useLaunchState,
} from "../pipeline-launch/launch-context";

export function PipelineLaunchButton() {
  const { modified } = useLaunchState();
  const [visible, setVisible] = useState(false);

  const dispatch = useLaunchDispatch();

  const clickHandler = () => {
    if (modified && !visible) {
      setVisible(true);
      return;
    }

    if (modified) {
      alert("SAVE TEMPLATE NOW");
    } else {
      alert("LAUNCHING");
    }
  };

  const onOk = () => {
    alert("FOOBAR");
    setVisible(false);
  };

  const onCancel = () => setVisible(false);

  return (
    <Popover
      visible={visible}
      placement="bottomRight"
      title={"Parameters have been modified"}
      content={
        <div>
          <p>blah blah blah</p>
          <Button onClick={clickHandler}>Save</Button>
        </div>
      }
      onConfirm={clickHandler}
      onCancel={onCancel}
      onOk={onOk}
    >
      <Button type="primary" icon={<IconRocket />} onClick={clickHandler}>
        Launch Pipeline
      </Button>
    </Popover>
  );
}
