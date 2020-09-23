import React from "react";
import { Button, Tooltip } from "antd";
import { IconSave } from "../../components/icons/Icons";

export function ParameterSaveButton({ parameter }) {
  const saveParameters = (e) => {
    e.stopPropagation();
    console.log(parameter);
  };
  return (
    <Tooltip
      title={
        "The parameters in this set have been modified, save as a new set?"
      }
    >
      <Button onClick={saveParameters} shape="circle" size="small">
        <IconSave />
      </Button>
    </Tooltip>
  );
}
