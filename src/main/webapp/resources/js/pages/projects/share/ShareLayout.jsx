import { Button, Card, Result, Space } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  useGetSampleIdsForProjectQuery,
  useShareSamplesWithProjectMutation,
} from "../../../apis/projects/samples";
import { ShareButton } from "./ShareButton";
import { ShareProject } from "./ShareProject";
import { ShareSamples } from "./ShareSamples";
import { setProject } from "./shareSlice";

export function ShareLayout() {
  const dispatch = useDispatch();
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

  const reset = () => {
    dispatch(setProject(undefined));
  };

  const SHOW_BUTTON = samples.length > 0;
  const DISABLED = samples.length === 0 || typeof projectId === "undefined";

  return (
    <Space direction="vertical" style={{ display: "block" }} size="large">
      {typeof projectId !== "undefined" && isError ? (
        <Card>
          <Result
            status="error"
            title={error.data.error}
            extra={[
              <Button key="back" onClick={reset} type="primary">
                Try Again?
              </Button>,
            ]}
          />
        </Card>
      ) : (
        <>
          <ShareProject />
          <ShareSamples samples={samples} />
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
