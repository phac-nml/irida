import { Form, Popover, Select, Space, Tag, Tooltip, Typography } from "antd";
import React from "react";
import { useDispatch } from "react-redux";
import {
  IconInfoCircle,
  IconWarningOutlined,
} from "../../../components/icons/Icons";
import { blue6, red6 } from "../../../styles/colors";
import { getColourForRestriction } from "../../../utilities/restriction-utilities";
import { updateMetadataRestriction } from "./shareSlice";

/**
 * React component to allow the user to select the level of restiction for a
 * metadata field in the destination project.
 *
 * @param {object} field - field to get the value on
 *  project, if it is not in the target project it get the current restriction.
 * @param {array} restrictions - list of available restrictions
 * @param {function} onChange - change handler
 * @returns {JSX.Element}
 * @constructor
 */
export function TargetMetadataRestriction({ field = {}, restrictions = [] }) {
  const dispatch = useDispatch();

  const [feedback, setFeedback] = React.useState({
    hasFeedback: false,
    validateStatus: "",
  });

  const [tooltipVisible, setTooltipVisible] = React.useState(false);

  React.useEffect(() => {
    /*
    Differnence is the calculated difference between the restrictions levels.
    If the difference is < 0 show a warning icon.
    */
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

  const onChange = (field, value) =>
    dispatch(updateMetadataRestriction({ field, value }));

  if (field.target) {
    if (field.difference >= 0) {
      return (
        <Space>
          <Tag color={getColourForRestriction(field.restriction)}>
            {getRestrictionLabel(field.restriction)}
          </Tag>
          <Popover
            title={i18n("TargetMetadataRestriction.higher")}
            placement="right"
            content={i18n("TargetMetadataRestriction.target-project-setting")}
          >
            <IconInfoCircle style={{ color: blue6 }} />
          </Popover>
        </Space>
      );
    }
    return (
      <Space>
        <Tag color={getColourForRestriction(field.restriction)}>
          {getRestrictionLabel(field.restriction)}
        </Tag>
        <Popover
          title={
            <Typography.Text strong style={{ color: red6 }}>
              {i18n("TargetMetadataRestriction.lower.caution")}
            </Typography.Text>
          }
          placement="right"
          content={i18n("TargetMetadataRestriction.target-project-setting")}
        >
          <IconWarningOutlined style={{ color: red6 }} />
        </Popover>
      </Space>
    );
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
