import React from "react";
import { Button, List, notification, Popconfirm, Tag } from "antd";
import { IconDownloadFile, IconRemove } from "../../../components/icons/Icons";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { Link } from "@reach/router";
import { deleteMetadataTemplate } from "../../../apis/metadata/metadata-templates";
import { blue6 } from "../../../styles/colors";
import { useSelector } from "react-redux";

export function MetadataTemplatesList({ projectId }) {
  const { templates, loading } = useSelector((state) => state.templates);
  const [BASE_URL] = React.useState(() =>
    setBaseUrl(`/projects/${projectId}/metadata-templates`)
  );

  const deleteTemplate = async (templateId) => {
    try {
      const message = await deleteMetadataTemplate(
        window.project.id,
        templateId
      );
      notification.success({ message });
      // setTemplates(templates.filter((template) => template.id !== templateId));
    } catch (e) {
      notification.error({ message: e });
    }
  };

  return (
    <List
      loading={loading}
      bordered
      itemLayout="horizontal"
      dataSource={templates}
      renderItem={(item) => (
        <List.Item
          className="t-template"
          actions={[
            <Tag key={`fields-${item.id}`}>
              {i18n("ProjectMetadataTemplates.fields", item.numFields)}
            </Tag>,
            <Button
              shape="circle"
              size="small"
              icon={<IconDownloadFile />}
              href={`${BASE_URL}/${item.id}/excel`}
              key={`download-${item.id}`}
            />,
            window.project.canManage ? (
              <Popconfirm
                key={`remove-${item.id}`}
                placement="bottomRight"
                title={"Delete this template?"}
                onConfirm={() => deleteTemplate(item.id)}
              >
                <Button shape="circle" size="small" icon={<IconRemove />} />
              </Popconfirm>
            ) : null,
          ]}
        >
          <List.Item.Meta
            title={
              <Link style={{ color: blue6 }} to={`${item.id}`}>
                {item.label}
              </Link>
            }
            description={item.description}
          />
        </List.Item>
      )}
    />
  );
}
