import { Alert, Space, Switch, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { useGetSampleIdsForProjectQuery } from "../../../apis/projects/samples";
import { SharedSamplesList } from "./SharedSamplesList";
import { updateOwnership } from "./shareSlice";

/**
 * React component to review the samples to be shared with another project.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareSamples() {
  const dispatch = useDispatch();
  const { originalSamples, owner } = useSelector((state) => state.shareReducer);
  console.log({ owner });

  const { projectId } = useSelector((state) => state.shareReducer);

  const { data: existingIds = [] } = useGetSampleIdsForProjectQuery(projectId, {
    skip: !projectId,
  });

  const samples = originalSamples.filter(
    (sample) => !existingIds.includes(sample.id)
  );

  const SHOW_SAMPLES = samples.length > 0;
  const SHOW_NO_SAMPLES_WARNING = samples.length === 0;
  const SHOW_SOME_SAMPLES_WARNING =
    SHOW_SAMPLES && samples.length < originalSamples.length;

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      {SHOW_SAMPLES && (
        <>
          <SharedSamplesList list={samples} />
          <Space>
            <Switch
              checked={owner}
              onChange={(e) => dispatch(updateOwnership(e))}
            />
            {owner ? (
              <Typography.Text strong>
                {i18n("ShareSamples.owner")}
              </Typography.Text>
            ) : (
              <Typography.Text strong>
                {i18n("ShareSamples.locked")}
              </Typography.Text>
            )}
          </Space>
        </>
      )}
      {SHOW_NO_SAMPLES_WARNING && (
        <Alert
          type="warning"
          showIcon
          message={i18n("ShareSamples.no-samples.message")}
          description={i18n("ShareSamples.no-samples.description")}
        />
      )}
      {SHOW_SOME_SAMPLES_WARNING && (
        <Alert
          type="info"
          showIcon
          message={i18n(
            "ShareSamples.some-samples.message",
            originalSamples.length - samples.length
          )}
        />
      )}
    </Space>
  );
}
