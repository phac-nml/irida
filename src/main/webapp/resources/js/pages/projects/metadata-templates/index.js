import React, { useEffect, useState } from "react";
import { render } from "react-dom";
import { getProjectMetadataTemplates } from "../../../apis/metadata/metadata-templates";
import { Avatar, Button, List, Space, Tag, Typography } from "antd";
import {
  IconDownloadFile,
  IconMetadataTemplate,
} from "../../../components/icons/Icons";
import { setBaseUrl } from "../../../utilities/url-utilities";

const { Title } = Typography;

function ProjectMetadataTemplates() {
  const [templates, setTemplates] = useState([]);

  useEffect(() => {
    getProjectMetadataTemplates(4).then((data) => setTemplates(data));
  }, []);

  return (
    <>
      <Title level={2}>{i18n("ProjectMetadataTemplates.title")}</Title>
      <List
        itemLayout="horizontal"
        dataSource={templates}
        renderItem={(item) => (
          <List.Item
            actions={[
              <Button
                shape="circle"
                icon={<IconDownloadFile />}
                href={setBaseUrl(
                  `/projects/${window.project.id}/metadata-templates/${item.id}/excel`
                )}
                key="list-download"
              />,
            ]}
          >
            <List.Item.Meta
              avatar={<Avatar icon={<IconMetadataTemplate />} />}
              title={
                <Space>
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
                </Space>
              }
              description={item.description}
            />
          </List.Item>
        )}
      />
    </>
  );
}

render(<ProjectMetadataTemplates />, document.querySelector("#templates-root"));
