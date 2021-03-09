import React from "react";
import { Button, List, notification, PageHeader, Popconfirm, Tag } from "antd";
import { IconDownloadFile, IconRemove } from "../../../components/icons/Icons";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { MetadataTemplateCreate } from "./MetadataTemplateCreate";
import { Link } from "@reach/router";
import {
  deleteMetadataTemplate,
  getProjectMetadataTemplates,
} from "../../../apis/metadata/metadata-templates";
import { blue6 } from "../../../styles/colors";

export function MetadataTemplatesList({ navigate, projectId }) {
  const [BASE_URL] = React.useState(() =>
    setBaseUrl(`/projects/${projectId}/metadata-templates`)
  );
  const [templates, setTemplates] = React.useState([]);

  React.useEffect(() => {
    getProjectMetadataTemplates(window.project.id).then((data) =>
      setTemplates(data)
    );
  }, []);

  const deleteTemplate = async (templateId) => {
    try {
      const message = await deleteMetadataTemplate(
        window.project.id,
        templateId
      );
      notification.success({ message });
      setTemplates(templates.filter((template) => template.id != templateId));
    } catch (e) {
      notification.error({ message: e });
    }
  };

  return (
    <PageHeader
      title={i18n("ProjectMetadataTemplates.title")}
      extra={[
        <MetadataTemplateCreate>
          <Button>New Template</Button>
        </MetadataTemplateCreate>,
      ]}
    >
      <List
        bordered
        itemLayout="horizontal"
        dataSource={templates}
        renderItem={(item) => (
          <List.Item
            className="t-template"
            actions={[
              <Tag>
                {i18n("ProjectMetadataTemplates.fields", item.numFields)}
              </Tag>,
              <Button
                shape="circle"
                size="small"
                icon={<IconDownloadFile />}
                href={`${BASE_URL}/${item.id}/excel`}
                key="list-download"
              />,
              window.project.canManage ? (
                <Popconfirm
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
    </PageHeader>
  );
}
