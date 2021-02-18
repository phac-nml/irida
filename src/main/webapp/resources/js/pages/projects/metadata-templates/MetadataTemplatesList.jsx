import React from "react";
import { Avatar, Button, List, PageHeader, Tag } from "antd";
import {
  IconDownloadFile,
  IconMetadataTemplate,
} from "../../../components/icons/Icons";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { MetadataTemplateCreate } from "./MetadataTemplateCreate";
import { Link } from "@reach/router";
import {
  createProjectMetadataTemplate,
  getProjectMetadataTemplates,
} from "../../../apis/metadata/metadata-templates";

export function MetadataTemplatesList() {
  const [BASE_URL] = React.useState(() =>
    setBaseUrl(`/projects/${window.project.id}/metadata-templates`)
  );
  const [templates, setTemplates] = React.useState([]);

  React.useEffect(() => {
    getProjectMetadataTemplates(window.project.id).then((data) =>
      setTemplates(data)
    );
  }, []);

  const createTemplate = async (details) => {
    try {
      const template = await createProjectMetadataTemplate(
        window.project.id,
        details
      );
      setTemplates([template, ...templates]);
    } catch (e) {
      return Promise.reject(e);
    }
  };

  return (
    <PageHeader
      title={i18n("ProjectMetadataTemplates.title")}
      extra={[
        <MetadataTemplateCreate key="create" createTemplate={createTemplate} />,
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
              <Button
                shape="circle"
                icon={<IconDownloadFile />}
                href={`${BASE_URL}/${item.id}/excel`}
                key="list-download"
              />,
            ]}
          >
            <List.Item.Meta
              avatar={<Avatar icon={<IconMetadataTemplate />} />}
              title={
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                  }}
                >
                  <Link to={`${item.id}`}>{item.label}</Link>
                  <Tag>
                    {i18n("ProjectMetadataTemplates.fields", item.numFields)}
                  </Tag>
                </div>
              }
              description={item.description}
            />
          </List.Item>
        )}
      />
    </PageHeader>
  );
}
