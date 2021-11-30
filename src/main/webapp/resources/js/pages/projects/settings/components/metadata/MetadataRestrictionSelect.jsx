import { Form, Select, Tooltip } from "antd";
import React from "react";
import {
  compareRestrictionLevels
} from "../../../../../utilities/restriction-utilities";

/**
 * React component to allow the user to select the level of restiction for a
 * metadata field in the destination project.
 *
 * @param {string} fieldKey - unique field identifier
 * @param {string} currentRestriction - current restriction for the field
 * @param {string} restriction - restriction for this field currently in the target
 *  project, if it is not in the target project it get the current restriction.
 * @param {array} restrictions - list of available restrictions
 * @param {function} onChange - change handler
 * @returns {JSX.Element}
 * @constructor
 */
export function MetadataRestrictionSelect({
  fieldKey,
  currentRestriction,
  restriction,
  restrictions = [],
  onChange,
}) {
  const [feedback, setFeedback] = React.useState({
    hasFeedback: false,
    validateStatus: "",
  });
  const [tooltipVisible, setTooltipVisible] = React.useState(false);

  React.useEffect(() => {
    const difference = compareRestrictionLevels(
      currentRestriction,
      restriction
    );
    setFeedback({
      hasFeedback: true,
      validateStatus: difference >= 0 ? "success" : "warning",
    });
    setTooltipVisible(difference < 0);
  }, [currentRestriction, restriction]);

  return (
    <Tooltip
      title="Lower restriction"
      placement="right"
      visible={tooltipVisible}
    >
      <Form.Item {...feedback} style={{ marginBottom: 0 }}>
        <Select
          style={{ width: `100%` }}
          value={restriction}
          onChange={(value) => onChange(fieldKey, value)}
        >
          {restrictions.map(({ label, value }) => (
            <Select.Option key={value} value={value}>
              {label}
            </Select.Option>
          ))}
        </Select>
      </Form.Item>
    </Tooltip>
  );
}
