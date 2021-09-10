import { Alert, Space, Typography } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { useGetSampleIdsForProjectQuery } from "../../../apis/projects/samples";
import { SharedSamplesList } from "./SharedSamplesList";

/**
 * React component to review the samples to be shared with another project.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareSamples() {
  const { originalSamples } = useSelector((state) => state.shareReducer);

  const { projectId } = useSelector((state) => state.shareReducer);

  const { data: existingIds = [], isFetching } = useGetSampleIdsForProjectQuery(
    projectId,
    {
      skip: !projectId,
    }
  );
  // const showExisting = !!samples.length && !!existing.length;
  // const space = showExisting ? { md: 12, xs: 24 } : { xs: 24 };

  const samples = originalSamples.filter(
    (sample) => !existingIds.includes(sample.id)
  );

  const SHOW_SAMPLES = samples.length > 0;
  const SHOW_NO_SAMPLES_WARNING = samples.length === 0;
  const SHOW_SOME_SAMPLES_WARNING =
    SHOW_SAMPLES && samples.length < originalSamples.length;

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Typography.Text>{"Samples available to copy"}</Typography.Text>
      {SHOW_SAMPLES && <SharedSamplesList list={samples} />}
      {SHOW_NO_SAMPLES_WARNING && (
        <Alert
          type="warning"
          showIcon
          message={`All samples exist in the target project`}
          description={`Since these samples exists, there is no reason to re-copy them.`}
        />
      )}
      {SHOW_SOME_SAMPLES_WARNING && (
        <Alert
          type="info"
          showIcon
          message={`6 samples exist in the target project and will not be re-shared`}
        />
      )}
    </Space>
  );
}
