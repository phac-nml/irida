import React, { useEffect, useState } from "react";
import { Avatar, Button, List, Space, Tag, Typography } from "antd";
import { IconDownloadFile, IconMetadataTemplate } from "../../icons/Icons";
import { getProjectMetadataTemplates } from "../../../apis/metadata/metadata-templates";
import { Link, useLocation } from "@reach/router";
import { setBaseUrl } from "../../../utilities/url-utilities";

const { Paragraph } = Typography;

/**
 * React component to render a list of metadata templates associated with
 * the current project.
 * @param {number} projectId - Identifier for a Project
 * @returns {JSX.Element}
 * @constructor
 */
export default function ListProjectTemplates({ projectId }) {
  const [templates, setTemplates] = useState([]);
  const location = useLocation();

  /*
  When the component mounts, get a list of the metadata templates associated with this project
   */
  useEffect(() => {
    getProjectMetadataTemplates(projectId).then((data) => setTemplates(data));
  }, [projectId]);

  return (
    <Space direction="vertical" style={{ width: "100%" }}>
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
                download
                href={setBaseUrl(
                  `/ajax/metadata-templates/${item.id}/download`
                )}
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
                  <Link to={`${location.pathname}/${item.id}`}>
                    {item.label}
                  </Link>
                  <Tag>
                    {i18n("ProjectMetadataTemplates.fields", item.numFields)}
                  </Tag>
                </div>
              }
              description={
                <Paragraph
                  ellipsis={{ rows: 2, expandable: true, symbol: "more" }}
                >
                  {item.description}
                </Paragraph>
              }
            />
          </List.Item>
        )}
      />
    </Space>
  );
}
