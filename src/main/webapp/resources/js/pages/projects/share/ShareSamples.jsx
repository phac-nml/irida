import { Alert, Checkbox, Space, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { useGetSampleIdsForProjectQuery } from "../../../apis/projects/samples";
import { ShareButton } from "./ShareButton";
import { SharedSamplesList } from "./SharedSamplesList";
import { updatedLocked, updateMoveSamples } from "./shareSlice";

/**
 * React component to review the samples to be shared with another project.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareSamples() {
  const dispatch = useDispatch();
  const { originalSamples, locked, move } = useSelector(
    (state) => state.shareReducer
  );

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
          <Checkbox
            checked={move}
            onChange={(e) => dispatch(updateMoveSamples(e.target.checked))}
          >
            <Typography.Text strong>
              Remove samples from current project
            </Typography.Text>
          </Checkbox>
          <Checkbox
            checked={locked}
            onChange={(e) => dispatch(updatedLocked(e.target.checked))}
            disabled={move}
          >
            <Typography.Text strong>
              Prevent samples from modification in target project (only when
              copying samples)
            </Typography.Text>
          </Checkbox>
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
      {SHOW_SAMPLES && <ShareButton />}
    </Space>
  );
}
