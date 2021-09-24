import { Dropdown, Menu } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { useShareSamplesWithProjectMutation } from "../../../apis/projects/samples";

export function ShareButton() {
  const { samples, owner, projectId, currentProject } = useSelector(
    (state) => state.shareReducer
  );
  const [
    shareSamplesWithProject,
    { isLoading, isUpdating },
  ] = useShareSamplesWithProjectMutation();
  console.log({ isLoading, isUpdating });

  const shareSamples = (type) => {
    shareSamplesWithProject({
      sampleIds: samples.map((s) => s.id),
      owner,
      currentId: currentProject,
      targetId: projectId,
      type,
    });
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
