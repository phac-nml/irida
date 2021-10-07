import { Result, Space } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import {
  useGetSampleIdsForProjectQuery,
  useShareSamplesWithProjectMutation,
} from "../../../apis/projects/samples";
import { ShareButton } from "./ShareButton";
import { ShareProject } from "./ShareProject";
import { ShareSamples } from "./ShareSamples";

export function ShareLayout() {
  const {
    originalSamples,
    currentProject,
    locked,
    projectId,
    remove,
  } = useSelector((state) => state.shareReducer);

  const [
    shareSamplesWithProject,
    { isLoading, isError, error },
  ] = useShareSamplesWithProjectMutation();

  const { data: existingIds = [] } = useGetSampleIdsForProjectQuery(projectId, {
    skip: !projectId,
  });

  const samples = originalSamples.filter(
    (sample) => !existingIds.includes(sample.id)
  );

  /**
   * Server call to actually share samples with another project.
   */
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
    <Space direction="vertical" style={{ display: "block" }} size="large">
      <ShareProject />
      <ShareSamples samples={samples} />
      {samples.length > 0 && (
        <ShareButton
          shareSamples={shareSamples}
          isLoading={isLoading}
          disabled={disabled}
        />
      )}
      {isError && <Result status="error" title={error.data.error} />}
    </Space>
  );
}
