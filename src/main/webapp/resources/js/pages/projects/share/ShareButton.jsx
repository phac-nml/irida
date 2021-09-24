import { Dropdown, Menu } from "antd";
import React from "react";
import { useSelector } from "react-redux";

export function ShareButton() {
  const { samples, owner, projectId } = useSelector(
    (state) => state.shareReducer
  );

  const shareSamples = (type) => {
    console.log({ type, samples, owner, projectId });
  };

  const disabled = samples.length === 0 || typeof projectId === "undefined";

  return (
    <div style={{ display: "flex", flexDirection: "row-reverse" }}>
      <Dropdown.Button
        disabled={disabled}
        onClick={() => shareSamples("copy")}
        overlay={
          <Menu onClick={(e) => shareSamples(e.key)}>
            <Menu.Item key="move">Move Samples</Menu.Item>
          </Menu>
        }
      >
        Copy Samples
      </Dropdown.Button>
    </div>
  );
}
