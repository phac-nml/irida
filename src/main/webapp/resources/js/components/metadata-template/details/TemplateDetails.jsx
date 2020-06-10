import React from "react";
import { useMetadataTemplate } from "../../../contexts/metadata-template-context";
import { IconLoading } from "../../icons/Icons";
import { BasicList } from "../../lists";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { Typography } from "antd";

const { Paragraph } = Typography;

export function TemplateDetails() {
  const { template, loading, updateField } = useMetadataTemplate();

  const initField = (field, text) => {
    const editable = window.project.canManage
      ? {
          onChange: (value) => updateField(field, value),
        }
      : null;
    return <Paragraph editable={editable}>{text}</Paragraph>;
  };

  return loading ? (
    <IconLoading />
  ) : (
    <BasicList
      dataSource={[
        {
          title: "TEMPLATE NAME",
          desc: initField("name", template.name),
        },
        {
          title: "DESCRIPTION",
          desc: initField("description", template.description),
        },
        {
          title: "CREATED DATE",
          desc: (
            <Paragraph>
              {formatInternationalizedDateTime(template.createdDate)}
            </Paragraph>
          ),
        },
      ]}
    />
  );
}
