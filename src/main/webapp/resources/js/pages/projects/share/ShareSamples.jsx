import { Alert, Checkbox, Space, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import ShareAssociated from "./ShareAssociated";
import { SharedSamplesList } from "./SharedSamplesList";
import { updatedLocked, updateMoveSamples } from "./shareSlice";

/**
 * React component to review the samples to be shared with another project.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareSamples({ samples = [] }) {
  const dispatch = useDispatch();

  const {
    associated,
    samples: originalSamples,
    locked,
    remove,
  } = useSelector((state) => state.shareReducer);

  return (
    <Space direction="vertical" style={{ width: `100%` }}>
      <Typography.Title level={5}>
        {i18n("ShareSamplesList.title")}
      </Typography.Title>
      {associated.length > 0 && <ShareAssociated />}
      {samples.length > 0 && (
        <>
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
        </>
      )}
      {samples.length === 0 && (
        <Alert
          type="warning"
          className="t-no-sample-warning"
          showIcon
          message={i18n("ShareSamples.no-samples.message")}
          description={i18n("ShareSamples.no-samples.description")}
        />
      )}
      {originalSamples.length - samples.length > 0 && (
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
    </Space>
  );
}
