import React from "react";
import {
  Button,
  Col,
  List,
  notification,
  PageHeader,
  Popconfirm,
  Row,
  Tag,
} from "antd";
import {
  IconDownloadFile,
  IconRemove,
  IconIsDefault,
  IconSetDefault,
} from "../../../components/icons/Icons";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { MetadataTemplateCreate } from "./MetadataTemplateCreate";
import { Link } from "@reach/router";
import {
  createProjectMetadataTemplate,
  deleteMetadataTemplate,
  getProjectMetadataTemplates,
  removeDefaultMetadataTemplate,
  setDefaultMetadataTemplate,
} from "../../../apis/metadata/metadata-templates";
import { blue6 } from "../../../styles/colors";

export function MetadataTemplatesList({ navigate }) {
  const [BASE_URL] = React.useState(() =>
    setBaseUrl(`/projects/${window.project.id}/metadata-templates`)
  );
  const [templates, setTemplates] = React.useState([]);
  const [hasDefaultTemplate, setHasDefaultTemplate] = React.useState(false);
  const [defaultTemplateId, setDefaultTemplateId] = React.useState(-1);

  React.useEffect(() => {
    getProjectMetadataTemplates(window.project.id).then((data) => {
      setTemplates(data);
      let templateIndex = data.findIndex(
        (template) => template.default === true
      );

      if (templateIndex >= 0) {
        setHasDefaultTemplate(true);
        setDefaultTemplateId(data[templateIndex].id);
      }
    });
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

  const setDefaultTemplate = async (templateId) => {
    try {
      const message = await setDefaultMetadataTemplate(
        window.project.id,
        templateId
      );
      notification.success({ message });
      setHasDefaultTemplate(true);
      setDefaultTemplateId(templateId);
    } catch (e) {
      notification.error({ message: e });
    }
  };

  const removeDefaultTemplate = async () => {
    try {
      const message = await removeDefaultMetadataTemplate(window.project.id);
      notification.success({ message });
      setHasDefaultTemplate(false);
      setDefaultTemplateId(-1);
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
              removeDefaultTemplate={
                hasDefaultTemplate ? (
                  <Button onClick={removeDefaultTemplate}>
                    Remove Default Template
                  </Button>
                ) : null
              }
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
                    icon={
                      defaultTemplateId === item.id ? (
                        <IconIsDefault />
                      ) : (
                        <IconSetDefault />
                      )
                    }
                    onClick={() => setDefaultTemplate(item.id)}
                    key="set-default-template"
                  />,
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
                      <Button
                        shape="circle"
                        size="small"
                        icon={<IconRemove />}
                      />
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
      </Col>
    </Row>
  );
}
