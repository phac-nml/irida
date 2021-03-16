import React from "react";
import {
  Button,
  List,
  notification,
  PageHeader,
  Skeleton,
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
 * Component for displaying and modifying a metadata template.
 *
 * @param {number} id - identifier for the current metadata template
 * @returns {JSX.Element|string}
 * @constructor
 */
export function MetadataTemplateAdmin({ id }) {
  const dispatch = useDispatch();
  const { templates, loading } = useSelector((state) => state.templates);
  const { fields: allFields } = useSelector((state) => state.fields);
  const [template, setTemplate] = React.useState({});
  const [fields, setFields] = React.useState();
  const [newFields, setNewFields] = React.useState();

  React.useEffect(() => {
    // Undefined templates == loading sate
    if (templates !== undefined) {
      if (templates.length === 0) {
        navigate(`../templates`);
      } else {
        const found = templates.find((template) => template.identifier === id);
        if (found) {
          const { fields, ...newTemplate } = found;
          setTemplate(newTemplate);
          setFields(addKeysToList(fields, "field"));
        }
        // Need to go to templates listing if template not found
      }
    }
  }, [id, templates]);

  React.useEffect(() => {
    if (Array.isArray(fields) && Array.isArray(allFields)) {
      setNewFields(differenceBy(allFields, fields, "id"));
    }
  }, [fields, allFields]);

  /**
   * Update the current template (any field updated will call this).
   * @param updated
   */
  const completeUpdate = async (updated) =>
    dispatch(updateTemplate(updated))
      .then(unwrapResult)
      .then(({ message }) => notification.info({ message }))
      .catch(({ message }) => notification.info({ message }));

  const onChange = async (field, text) => {
    if (template[field] !== text) {
      const updated = { ...template, [field]: text, fields };
      await completeUpdate(updated);
    }
  };

  const onRowUpdate = async (newOrder) => {
    const updated = { ...template, fields: newOrder };
    await completeUpdate(updated);
  };

  const onAddFields = async (newFields) => {
    const updated = {
      ...template,
      fields: [...fields, ...newFields],
    };
    await completeUpdate(updated);
  };

  const removeField = async (item) => {
    const updated = {
      ...template,
      fields: fields.filter((field) => field.id !== item.id),
    };
    await completeUpdate(updated);
  };

  return (
    <PageHeader title={template.name} onBack={() => navigate("./")}>
      <Skeleton loading={loading}>
        <List itemLayout="vertical" size="small">
          <List.Item>
            <List.Item.Meta
              title={<Text strong>Name</Text>}
              description={
                <Paragraph
                  editable={{ onChange: (text) => onChange("name", text) }}
                >
                  {template.name}
                </Paragraph>
              }
            />
          </List.Item>
          <List.Item>
            <List.Item.Meta
              title={<Text strong>Description</Text>}
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
                <div
                  style={{ display: "flex", justifyContent: "space-between" }}
                >
                  <span>
                    <Text strong>Metadata Fields</Text>
                    <HelpPopover
                      content={
                        <div>
                          You can drag and drop to re-arrange the order of the
                          fields
                        </div>
                      }
                    />
                  </span>
                  <MetadataAddTemplateField
                    fields={newFields}
                    onAddFields={onAddFields}
                  />
                </div>
              }
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
                ...(window.project.canManage
                  ? [
                      {
                        title: i18n("MetadataField.permissions"),
                        dataIndex: "type",
                        key: "permissions",
                        render() {
                          return "All";
                        },
                      },
                      {
                        align: "right",
                        width: 100,
                        render(item) {
                          return (
                            <Button
                              onClick={() => removeField(item)}
                              shape="circle"
                              size="small"
                              icon={<IconRemove />}
                            />
                          );
                        },
                      },
                    ]
                  : []),
              ]}
              onRowUpdate={onRowUpdate}
            />
          </List.Item>
        </List>
      </Skeleton>
    </PageHeader>
  );
}
