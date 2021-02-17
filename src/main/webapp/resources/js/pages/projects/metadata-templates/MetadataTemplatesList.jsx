import React from "react";
import { Avatar, Button, List, Tag } from "antd";
import {
  IconDownloadFile,
  IconMetadataTemplate,
} from "../../../components/icons/Icons";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { getProjectMetadataTemplates } from "../../../apis/metadata/metadata-templates";

export function MetadataTemplatesList() {
  const [templates, setTemplates] = React.useState([]);
  const [BASE_URL] = React.useState(() =>
    setBaseUrl(`/projects/${window.project.id}/metadata-templates`)
  );

  React.useEffect(() => {
    getProjectMetadataTemplates(window.project.id).then((data) =>
      setTemplates(data)
    );
  }, []);

  return (
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
                <a
                  href={setBaseUrl(
                    `/projects/${window.project.id}/metadata-templates/${item.id}`
                  )}
                >
                  {item.label}
                </a>
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
  );
}
