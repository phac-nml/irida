import { Form, Popover, Select, Space, Tag, Tooltip } from "antd";
import React from "react";
import { IconInfoCircle } from "../../../../../components/icons/Icons";
import {
  getColourForRestriction
} from "../../../../../utilities/restriction-utilities";

/**
 * React component to allow the user to select the level of restiction for a
 * metadata field in the destination project.
 *
 * @param {object} field - field to set value on
 *  project, if it is not in the target project it get the current restriction.
 * @param {array} restrictions - list of available restrictions
 * @param {function} onChange - change handler
 * @returns {JSX.Element}
 * @constructor
 */
export function TargetMetadataRestriction({
                                            field = {},
                                            restrictions = [],
                                            onChange
                                          }) {
  const [feedback, setFeedback] = React.useState({
    hasFeedback: false,
    validateStatus: "",
  });

  console.log(field);

  const [tooltipVisible, setTooltipVisible] = React.useState(false);

  React.useEffect(() => {
    if (field.difference > 0) {
      setFeedback({
        hasFeedback: true,
        validateStatus: field.difference >= 0 ? "success" : "warning",
      });
      setTooltipVisible(field.difference < 0);
    }
  }, [field.difference]);

  function getRestrictionLabel(value) {
    const restriction = restrictions?.find((r) => r.value === value);
    return restriction?.label;
  }

  if (!field.new) {
    return <Space>
      <Tag color={getColourForRestriction(field.restriction)}>
        {getRestrictionLabel(field.restriction)}
      </Tag>
      <Popover title={"Higher Restictriction Level"}
               placement="right"
               content={"This value is set it the target project and can be updated in the project settings."}>
        <IconInfoCircle/>
      </Popover>
    </Space>;
  }

  return (
    <Tooltip
      title="Lower restriction"
      placement="right"
      visible={tooltipVisible}
    >
      <Form.Item {...feedback} style={{ marginBottom: 0 }}>
        <Select
          style={{ width: `100%` }}
          value={field.restriction}
          onChange={(value) => onChange({ ...field }, value)}
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
