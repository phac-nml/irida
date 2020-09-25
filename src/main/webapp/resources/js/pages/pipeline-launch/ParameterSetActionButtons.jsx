import React from "react";
import { Button, Space, Tooltip } from "antd";
import { IconUndo } from "../../components/icons/Icons";
import { useLaunchState } from "./launch-context";
import { ParameterSetSaveButton } from "./ParameterSetSaveButton";

export function ParameterSetActionButtons({ set }) {
  const { api } = useLaunchState();

  const resetParameters = (e) => {
    e.stopPropagation();
    api.resetParameters(set);
  };

  return (
    <Space>
      <ParameterSetSaveButton set={set} />
      <Tooltip title={"Revert parameters to original"}>
        <Button
          shape="circle"
          size="small"
          icon={<IconUndo />}
          onClick={resetParameters}
        />
      </Tooltip>
    </Space>
  );
}
