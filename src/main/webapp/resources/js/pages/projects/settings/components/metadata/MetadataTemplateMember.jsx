import {
  List,
  notification,
  PageHeader,
  Skeleton,
  Table,
  Typography,
} from "antd";
import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useGetTemplatesForProjectQuery } from "../../../../../apis/metadata/metadata-templates";
import { addKeysToList } from "../../../../../utilities/http-utilities";

const { Paragraph, Text } = Typography;

/**
 * Component for viewing a metadata template.  This is for members who cannot
 * manage the current project.
 *
 * @param {number} id - identifier for the current template
 * @param {number} projectId - identifier for the current project
 * @returns {JSX.Element}
 * @constructor
 */
export default function MetadataTemplateMember() {
  const navigate = useNavigate();
  const { id, projectId } = useParams();
  const { data: templates, isLoading } =
    useGetTemplatesForProjectQuery(projectId);
  const [template, setTemplate] = React.useState({});

  React.useEffect(() => {
    /*
    On mount we need to find the current template in the list of all templates.
    If it is not found the we redirect to all templates, if no templates at all
    are found then we redirect to the metadata fields page so the user can
    create one.
     */
    if (!isLoading) {
      const found = templates.find(
        (template) => Number(template.identifier) === Number(id)
      );

      if (found) {
        setTemplate(found);
      } else if (templates.length === 0) {
        navigate(`../fields`).then(() =>
          notification.warn({
            message: i18n("MetadataTemplateManager.no-templates"),
          })
        );
      } else {
        navigate(`../templates`).then(() =>
          notification.warn({
            message: i18n("MetadataTemplateManager.no-templates-exists"),
          })
        );
      }
    }
  }, [id, templates]);

  return (
    <PageHeader title={template.name} onBack={() => navigate(-1)}>
      <Skeleton loading={isLoading}>
        <List itemLayout="vertical" size="small">
          <List.Item>
            <List.Item.Meta
              title={<Text strong>{i18n("MetadataTemplate.label")}</Text>}
              description={template.name}
            />
          </List.Item>
          <List.Item>
            <List.Item.Meta
              title={<Text strong>{i18n("MetadataTemplate.description")}</Text>}
            />
            <Paragraph type="secondary">{template.description || ""}</Paragraph>
          </List.Item>
          <List.Item>
            <List.Item.Meta
              title={<Text strong>{i18n("MetadataTemplate.fields")}</Text>}
              description={
                <Table
                  pagination={false}
                  columns={[
                    { title: i18n("MetadataField.label"), dataIndex: "label" },
                    { title: i18n("MetadataField.type"), dataIndex: "type" },
                  ]}
                  dataSource={addKeysToList(template.fields || [], "field")}
                />
              }
            />
          </List.Item>
        </List>
      </Skeleton>
    </PageHeader>
  );
}
