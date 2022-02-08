import { Alert, Checkbox, Space, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { SharedSamplesList } from "./SharedSamplesList";
import { updatedLocked, updateMoveSamples } from "./shareSlice";

/**
 * React component to review the samples to be shared with another project.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareSamples({ samples = [], redirect }) {
  const dispatch = useDispatch();
  const { originalSamples, locked, remove } = useSelector(
    (state) => state.shareReducer
  );

  const SHOW_SAMPLES = samples.length > 0;
  const SHOW_NO_SAMPLES_WARNING = samples.length === 0;
  const SHOW_SOME_SAMPLES_WARNING =
    SHOW_SAMPLES && samples.length < originalSamples.length;

  return (
    <>
      {SHOW_SAMPLES && (
        <Space direction="vertical" style={{ width: `100%` }}>
          <SharedSamplesList list={samples} />
          <Checkbox
            className="t-move-checkbox"
            checked={remove}
            onChange={(e) => dispatch(updateMoveSamples(e.target.checked))}
          >
            <Typography.Text strong>
              {i18n("ShareSamples.checkbox.remove")}
            </Typography.Text>
          </Checkbox>
          <Checkbox
            className="t-lock-checkbox"
            checked={locked}
            onChange={(e) => dispatch(updatedLocked(e.target.checked))}
            disabled={remove}
          >
            <Typography.Text strong>
              {i18n("ShareSamples.checkbox.lock")}
            </Typography.Text>
          </Checkbox>
        </Space>
      )}
      {SHOW_NO_SAMPLES_WARNING && (
        <Alert
          type="warning"
          className="t-no-sample-warning"
          showIcon
          message={i18n("ShareSamples.no-samples.message")}
          description={i18n("ShareSamples.no-samples.description")}
        />
      )}
      {SHOW_SOME_SAMPLES_WARNING && (
        <Alert
          className="t-same-samples-warning"
          type="info"
          showIcon
          message={i18n(
            "ShareSamples.some-samples.message",
            originalSamples.length - samples.length
          )}
        />
      )}
    </>
  );
}
