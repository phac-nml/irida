import React from "react";
import { Button, List, PageHeader, Skeleton, Typography } from "antd";
import { navigate } from "@reach/router";
import DnDTable from "../../../components/ant.design/DnDTable";
import { HelpPopover } from "../../../components/popovers";
import { updateTemplate } from "./templates/templatesSlice";
import { useDispatch, useSelector } from "react-redux";
import { addKeysToList } from "../../../utilities/http-utilities";

const { Paragraph, Text } = Typography;

export function MetadataTemplate({ id }) {
  const dispatch = useDispatch();
  const { templates, loading } = useSelector((state) => state.templates);
  const [template, setTemplate] = React.useState({});
  const [fields, setFields] = React.useState();

  React.useEffect(() => {
    console.log(templates);
    if (templates.length === 0) {
      console.log("NO TEMPLATES");
    } else if (templates) {
      const found = templates.find((template) => template.identifier === id);
      if (found) {
        const { fields, ...newTemplate } = found;
        setTemplate(newTemplate);
        setFields(addKeysToList(fields, "field"));
      }
      // Need to go to templates listing if template not found
    }
  }, [templates]);

  const completeUpdate = async (updated) => {
    await dispatch(updateTemplate(updated));
    // try {
    //   const message = await updateMetadataTemplate(updated);
    //   notification.success({ message });
    //   setTemplate(updated);
    // } catch (e) {}
  };

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
                  <Button>Add New Field</Button>
                </div>
              }
            />
            <DnDTable
              data={fields}
              columns={[
                { title: "Metadata Field", dataIndex: "label", key: "label" },
                { title: "Type", dataIndex: "type", key: "text" },
                window.project.canManage
                  ? {
                      title: "Permissions",
                      dataIndex: "type",
                      key: "permissions",
                      render() {
                        return "All";
                      },
                    }
                  : null,
              ]}
              onRowUpdate={onRowUpdate}
            />
          </List.Item>
        </List>
      </Skeleton>
    </PageHeader>
  );
}
