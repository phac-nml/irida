import { Form, Popover, Radio, Space, Tag, Tooltip, Typography } from "antd";
import React from "react";
import { useDispatch } from "react-redux";
import { IconInfoCircle, IconWarningOutlined } from "../icons/Icons";
import { blue6, red6 } from "../../styles/colors";
import {
  getColourForRestriction,
  getRestrictionLabel,
  Restriction,
  RestrictionListItem,
} from "../../utilities/restriction-utilities";
import { updateNewProjectMetadataRestriction } from "../../pages/projects/create/newProjectSlice";
import { updateMetadataRestriction } from "../../pages/projects/share/shareSlice";
import { FormItemProps } from "antd/lib/form/FormItem";

export interface Field {
  difference: number;
  target: string;
  restriction: Restriction;
}

interface TargetMetadataRestrictionProps {
  field: Field;
  restrictions: RestrictionListItem[];
  newProject: boolean;
}

/**
 * React component to allow the user to select the level of restiction for a
 * metadata field in the destination project.
 *
 * @param  field - field to get the value on
 *  project, if it is not in the target project it get the current restriction.
 * @param restrictions - list of available restrictions
 * @param onChange - change handler
 * @param newProject - if a new project is being created or not
 * @returns {JSX.Element}
 * @constructor
 */
export function TargetMetadataRestriction({
  field,
  restrictions = [],
  newProject = false,
}: TargetMetadataRestrictionProps): JSX.Element {
  const dispatch = useDispatch();

  const [feedback, setFeedback] = React.useState<FormItemProps>({
    hasFeedback: false,
    validateStatus: "",
  });

  const [tooltipVisible, setTooltipVisible] = React.useState<boolean>(false);

  React.useEffect(() => {
    /*
    Difference is the calculated difference between the restrictions levels.
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

  const onChange = (field: Field, value: string) => {
    if (newProject) {
      dispatch(updateNewProjectMetadataRestriction({ field, value }));
    } else {
      dispatch(updateMetadataRestriction({ field, value }));
    }
  };

  if (field.target) {
    if (field.difference >= 0) {
      return (
        <Space>
          <Tag color={getColourForRestriction(field.restriction)}>
            {getRestrictionLabel(restrictions, field.restriction)}
          </Tag>
          <Popover
            title={i18n("TargetMetadataRestriction.title")}
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
          {getRestrictionLabel(restrictions, field.restriction)}
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
      title={i18n("TargetMetadataRestriction.title")}
      placement="right"
      visible={tooltipVisible}
    >
      <Form.Item {...feedback} style={{ marginBottom: 0 }}>
        <Radio.Group
          style={{ display: "flex", width: "100%" }}
          value={field.restriction}
          onChange={({ target: { value } }) => onChange({ ...field }, value)}
        >
          {/* Styles can be replaced with compact space in the future */}
          {restrictions.map(({ label, value }) => (
            <Radio.Button
              style={{ whiteSpace: "nowrap" }}
              key={value}
              value={value}
            >
              {label}
            </Radio.Button>
          ))}
        </Radio.Group>
      </Form.Item>
    </Tooltip>
  );
}
