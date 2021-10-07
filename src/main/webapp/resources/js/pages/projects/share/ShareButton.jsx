import { Button } from "antd";
import React from "react";
import { IconShare } from "../../../components/icons/Icons";

export function ShareButton({ shareSamples, isLoading, disabled }) {
  return (
    <div style={{ display: "flex", flexDirection: "row-reverse" }}>
      <Button
        type="primary"
        disabled={disabled}
        onClick={() => shareSamples()}
        loading={isLoading}
        icon={<IconShare />}
      >
        {i18n("ShareButton.button")}
      </Button>
    </div>
  );
}
