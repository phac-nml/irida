import { Space } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import {
  useGetSampleIdsForProjectQuery,
  useShareSamplesWithProjectMutation,
} from "../../../apis/projects/samples";
import { ShareButton } from "./ShareButton";
import { ShareError } from "./ShareError";
import { ShareProject } from "./ShareProject";
import { ShareSamples } from "./ShareSamples";

/**
 * React component to layout the components for sharing/moving samples between
 * projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareLayout({redirect}) {

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

  /*
  originalSampls contains all samples, here we are filtering it
  to only show samples that are not in the target project.
   */
  let samples = originalSamples.filter(
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

  const SHOW_BUTTON = samples.length > 0;
  const DISABLED = samples.length === 0 || typeof projectId === "undefined";

  return (
    <Space direction="vertical" style={{ display: "block" }} size="large">
      {typeof projectId !== "undefined" && isError ? (
        <ShareError error={error} redirect={redirect} />
      ) : (
        <>
          <ShareProject />
          <ShareSamples samples={samples} redirect={redirect} />
          {SHOW_BUTTON && (
            <ShareButton
              shareSamples={shareSamples}
              isLoading={isLoading}
              disabled={DISABLED}
            />
          )}
        </>
      )}
    </Space>
  );
}
