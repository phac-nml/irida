import {
  Button,
  List,
  notification,
  PageHeader,
  Skeleton,
  Space,
  Tag,
  Tooltip,
  Typography,
} from "antd";
import differenceBy from "lodash/differenceBy";
import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useGetMetadataFieldsForProjectQuery } from "../../../../../apis/metadata/field";
import {
  useGetTemplatesForProjectQuery,
  useUpdateMetadataTemplateMutation,
} from "../../../../../apis/metadata/metadata-templates";
import {
  useGetProjectDetailsQuery,
  useUpdateDefaultMetadataTemplateMutation,
} from "../../../../../apis/projects/project";
import DnDTable from "../../../../../components/ant.design/DnDTable";
import {
  IconCheckCircle,
  IconRemove,
} from "../../../../../components/icons/Icons";
import { HelpPopover } from "../../../../../components/popovers";
import { blue6 } from "../../../../../styles/colors";
import { addKeysToList } from "../../../../../utilities/http-utilities";
import { MetadataAddTemplateField } from "./MetadataAddTemplateField";

const { Paragraph, Text } = Typography;

/**
 * Component for displaying and modifying a metadata template for users who
 * can manage the current project.
 *
 * @param {number} id - identifier for the current metadata template
 * @returns {JSX.Element|string}
 * @constructor
 */
export default function MetadataTemplateManager() {
  const navigate = useNavigate();
  const { id, projectId } = useParams();
  const { data: allFields } = useGetMetadataFieldsForProjectQuery(projectId);

  const { data: templates, isFetching } =
    useGetTemplatesForProjectQuery(projectId);
  const { data: project = {} } = useGetProjectDetailsQuery(projectId);
  const [updateMetadataTemplate] = useUpdateMetadataTemplateMutation();
  const [updateDefaultTemplate] = useUpdateDefaultMetadataTemplateMutation();

  const [template, setTemplate] = React.useState({});
  const [fields, setFields] = React.useState();
  const [newFields, setNewFields] = React.useState();

  const metadataRestrictionsTranslationsMap = {
    LEVEL_1: i18n("metadataRole.LEVEL_1"),
    LEVEL_2: i18n("metadataRole.LEVEL_2"),
    LEVEL_3: i18n("metadataRole.LEVEL_3"),
    LEVEL_4: i18n("metadataRole.LEVEL_4"),
  };

  React.useEffect(() => {
    /*
    On mount we need to find the current template in the list of all templates.
    If it is not found the we redirect to all templates, if no templates at all
    are found then we redirect to the metadata fields page so the user can
    create one.
     */
    if (!isFetching) {
      const found = templates.find((template) => template.identifier == id);

      if (found) {
        const { fields, ...newTemplate } = found;
        setTemplate(newTemplate);
        setFields(addKeysToList(fields, "field"));
      } else if (templates.length === 0) {
        navigate(`../fields`).then(() =>
          notification.warn({ message: i18n("MetadataTemplate.no-templates") })
        );
      } else {
        navigate(`../templates`).then(() =>
          notification.warn({ message: i18n("MetadataTemplate.not-found") })
        );
      }
    }
  }, [id, isFetching, templates]);

  React.useEffect(() => {
    if (Array.isArray(fields) && Array.isArray(allFields)) {
      /*
      Whenever the fields on the template are updated (or initially loaded), we
      determine which fields from the project are not currently on the template.
      This allows us to be able to only display the fields that need to be displayed
      when the user wants to add a new field to the template.
       */
      setNewFields(differenceBy(allFields, fields, "id"));
    }
  }, [fields, allFields]);

  /**
   * Update the current template.
   *
   * @param {Object} template - the updated template to save and re-render
   */
  const completeUpdate = async (template) =>
    updateMetadataTemplate(template)
      .then((response) => notification.info({ message: response.data.message }))
      .catch((error) =>
        notification.info({ message: error.response.data.error })
      );

  /**
   * Update an attribute on the current metadata template (except fields)
   *
   * @param {string} attribute - attribute on the template to update.
   * @param {string }text - new value for the template attribute.
   */
  const onChange = async (attribute, text) => {
    if (attribute in template && template[attribute] !== text) {
      const updated = { ...template, [attribute]: text, fields };
      await completeUpdate(updated);
    }
  };

  /**
   * Update the order of the metadata fields based on a drag and drop event
   *
   * @param {Object[]} newOrder - new order of metadata fields
   */
  const onDragFieldUpdate = async (newOrder) => {
    const updated = { ...template, fields: newOrder };
    await completeUpdate(updated);
  };

  /**
   * Add a metadata field to the current template.
   *
   * @param {Object} newFields - the field to add to the template
   */
  const onAddFields = async (newFields) => {
    const updated = {
      ...template,
      fields: [...fields, ...newFields],
    };
    await completeUpdate(updated);
  };

  /**
   * Remove a metadata field from the current template
   *
   * @param {Object} item - the field to remove from the template
   */
  const removeField = async (item) => {
    const updated = {
      ...template,
      fields: fields.filter((field) => field.id !== item.id),
    };
    await completeUpdate(updated);
  };

  /**
   * Returns either a default tag or a set default button depending
   * on if template is project default or not
   *
   * @param {Object} template - the template to return component for
   */
  const displayHeaderExtras = (template) => {
    if (template.identifier == project.defaultMetadataTemplateId) {
      return [
        <Tag
          key={`default-template-${template.identifier}`}
          color={blue6}
          icon={<IconCheckCircle />}
          className="t-t-default-tag"
        >
          {i18n("MetadataTemplateManager.default")}
        </Tag>,
      ];
    }
    return [
      <Button
        onClick={() => setDefaultTemplate(template)}
        key={`set-default-template-${template.identifier}`}
        className="t-t-set-default-button"
      >
        {i18n("MetadataTemplateManager.set-as-default")}
      </Button>,
    ];
  };

  /**
   * Sets the default template for the project
   *
   * @param {Object} template - the template to set as default
   */
  const setDefaultTemplate = async (template) => {
    updateDefaultTemplate({
      projectId,
      templateId: template.identifier,
    })
      .then((response) => {
        notification.success({ message: response.data.message });
      })
      .catch((error) =>
        notification.error({ message: error.response.data.message })
      );
  };

  return (
    <PageHeader
      title={<span className="t-t-header-name">{template.name}</span>}
      onBack={() => navigate(-1)}
      extra={displayHeaderExtras(template)}
    >
      <Skeleton loading={isFetching}>
        <List itemLayout="vertical" size="small">
          <List.Item>
            <List.Item.Meta
              title={<Text strong>{i18n("MetadataTemplate.label")}</Text>}
              description={
                <Paragraph
                  className="t-t-edit-name"
                  editable={{ onChange: (text) => onChange("name", text) }}
                >
                  {template.name}
                </Paragraph>
              }
            />
          </List.Item>
          <List.Item>
            <List.Item.Meta
              title={<Text strong>{i18n("MetadataTemplate.description")}</Text>}
              description={
                <Paragraph
                  editable={{
                    onChange: (text) => onChange("description", text),
                  }}
                >
                  {template.description || ""}
                </Paragraph>
              }
            />
          </List.Item>
          <List.Item>
            <List.Item.Meta
              title={
                <>
                  <Text strong>{i18n("MetadataTemplate.fields")}</Text>
                  <HelpPopover
                    content={<div>{i18n("MetadataTemplateManager.drag")}</div>}
                  />
                </>
              }
            />
            <Space direction="vertical" style={{ width: `100%` }}>
              <MetadataAddTemplateField
                fields={newFields}
                onAddFields={onAddFields}
              />
              <DnDTable
                data={fields}
                columns={[
                  {
                    title: i18n("MetadataField.label"),
                    dataIndex: "label",
                    key: "label",
                  },
                  {
                    title: i18n("MetadataField.type"),
                    dataIndex: "type",
                    key: "text",
                  },
                  {
                    title: i18n("MetadataFieldsListManager.restrictions"),
                    dataIndex: "restriction",
                    key: "restriction",
                    render: (text) => {
                      return metadataRestrictionsTranslationsMap[text];
                    },
                  },
                  {
                    align: "right",
                    width: 50,
                    render(item) {
                      return (
                        <Tooltip
                          placement="left"
                          title={i18n("MetadataTemplateManager.remove-field")}
                        >
                          <Button
                            onClick={() => removeField(item)}
                            shape="circle"
                            size="small"
                            icon={<IconRemove />}
                          />
                        </Tooltip>
                      );
                    },
                  },
                ]}
                onRowUpdate={onDragFieldUpdate}
              />
            </Space>
          </List.Item>
        </List>
      </Skeleton>
    </PageHeader>
  );
}
