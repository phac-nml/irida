import React from "react";
import { Button, Col, List, notification, PageHeader, Row, Tag } from "antd";
import { IconDownloadFile, IconRemove } from "../../../components/icons/Icons";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { MetadataTemplateCreate } from "./MetadataTemplateCreate";
import { Link } from "@reach/router";
import {
  createProjectMetadataTemplate,
  deleteMetadataTemplate,
  getProjectMetadataTemplates,
} from "../../../apis/metadata/metadata-templates";

export function MetadataTemplatesList({ navigate }) {
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
      navigate(`${template.id}`);
    } catch (e) {
      return Promise.reject(e);
    }
  };

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
    <Row>
      <Col xs={24} lg={18} xxl={12}>
        <PageHeader
          title={i18n("ProjectMetadataTemplates.title")}
          extra={[
            <MetadataTemplateCreate
              key="create"
              createTemplate={createTemplate}
            />,
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
                    <Button
                      shape="circle"
                      size="small"
                      icon={<IconRemove />}
                      onClick={() => deleteTemplate(item.id)}
                    />
                  ) : null,
                ]}
              >
                <List.Item.Meta
                  // avatar={<Avatar icon={<IconMetadataTemplate />} />}
                  title={<Link to={`${item.id}`}>{item.label}</Link>}
                  description={item.description}
                />
              </List.Item>
            )}
          />
        </PageHeader>
      </Col>
    </Row>
  );
}
