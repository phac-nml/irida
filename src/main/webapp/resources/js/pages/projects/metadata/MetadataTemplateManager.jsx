import React from "react";
import {
  Button,
  List,
  notification,
  PageHeader,
  Skeleton,
  Space,
  Tooltip,
  Typography,
} from "antd";
import { navigate } from "@reach/router";
import DnDTable from "../../../components/ant.design/DnDTable";
import { HelpPopover } from "../../../components/popovers";
import { updateTemplate } from "../redux/templatesSlice";
import { useDispatch, useSelector } from "react-redux";
import { addKeysToList } from "../../../utilities/http-utilities";
import { unwrapResult } from "@reduxjs/toolkit";
import { MetadataAddTemplateField } from "./MetadataAddTemplateField";
import differenceBy from "lodash/differenceBy";
import { IconRemove } from "../../../components/icons/Icons";

const { Paragraph, Text } = Typography;

/**
 * Component for displaying and modifying a metadata template for users who
 * can manage the current project.
 *
 * @param {number} id - identifier for the current metadata template
 * @returns {JSX.Element|string}
 * @constructor
 */
export function MetadataTemplateManager({ id }) {
  const dispatch = useDispatch();

  const { templates, loading } = useSelector((state) => state.templates);
  const { fields: allFields } = useSelector((state) => state.fields);
  const [template, setTemplate] = React.useState({});
  const [fields, setFields] = React.useState();
  const [newFields, setNewFields] = React.useState();

  React.useEffect(() => {
    /*
    On mount we need to find the current template in the list of all templates.
    If it is not found the we redirect to all templates, if no templates at all
    are found then we redirect to the metadata fields page so the user can
    create one.
     */
    if (!loading) {
      const found = templates.find((template) => template.identifier === id);

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
  }, [id, templates]);

  React.useEffect(() => {
    if (Array.isArray(fields) && Array.isArray(allFields)) {
      /*
      Whenever the fields on the template are updated (or initial loaded), we
      determine which fields from the project are not currently on the template.
      This allows us to be able to only display the fields that need to be displayed
      when the user want to add a new field to the template.
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
    dispatch(updateTemplate(template))
      .then(unwrapResult)
      .then(({ message }) => notification.info({ message }))
      .catch((message) => notification.info({ message }));

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

  return (
    <PageHeader
      title={<span className="t-t-header-name">{template.label}</span>}
      onBack={() => navigate("./")}
    >
      <Skeleton loading={loading}>
        <List itemLayout="vertical" size="small">
          <List.Item>
            <List.Item.Meta
              title={<Text strong>{i18n("MetadataTemplate.label")}</Text>}
              description={
                <Paragraph
                  className="t-t-edit-name"
                  editable={{ onChange: (text) => onChange("label", text) }}
                >
                  {template.label}
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
            <Space direction="vertical" style={{ display: "block" }}>
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
