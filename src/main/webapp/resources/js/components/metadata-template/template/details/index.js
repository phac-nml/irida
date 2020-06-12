import React from "react";
import { useMetadataTemplate } from "../../../../contexts/metadata-template-context";
import { IconLoading } from "../../../icons/Icons";
import { BasicList } from "../../../lists";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { Typography } from "antd";

const { Paragraph } = Typography;

export default function TemplateDetails() {
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
          title: i18n("TemplateDetails.name"),
          desc: initField("name", template.name),
        },
        {
          title: i18n("TemplateDetails.description"),
          desc: initField("description", template.description),
        },
        {
          title: i18n("TemplateDetails.createdDate"),
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
