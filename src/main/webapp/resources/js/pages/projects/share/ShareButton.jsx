import { Button, Result } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { useShareSamplesWithProjectMutation } from "../../../apis/projects/samples";

export function ShareButton() {
  const { samples, locked, remove, projectId, currentProject } = useSelector(
    (state) => state.shareReducer
  );

  const [
    shareSamplesWithProject,
    { isLoading, isUpdating, isError, error },
  ] = useShareSamplesWithProjectMutation();

  const shareSamples = () => {
    shareSamplesWithProject({
      sampleIds: samples.map((s) => s.id),
      locked,
      currentId: currentProject,
      targetId: projectId,
      remove,
    });
  };

  const disabled = samples.length === 0 || typeof projectId === "undefined";

  return (
    <>
      <div style={{ display: "flex", flexDirection: "row-reverse" }}>
        <Button
          disabled={disabled}
          onClick={() => shareSamples()}
          loading={isLoading}
        >
          {i18n("ShareButton.copy")}
        </Button>
      </div>
      {isError && <Result status="error" title={error.data.error} />}
    </>
  );
}
