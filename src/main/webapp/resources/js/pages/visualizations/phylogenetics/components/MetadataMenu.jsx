import React from "react";
import { Button, Checkbox, Popover, Select, Space, Switch, Typography } from "antd";
import { FilterOutlined } from "@ant-design/icons";
const { Option } = Select;

export function MetadataMenu({fields, templates, allSelected, onTemplateChange, onFieldChecked, onSelectAllChange}) {
  const metadataTemplateOptions = [];
  metadataTemplateOptions.push(<Option key="template-default" value={-1}>{i18n("visualization.phylogenomics.select-template.all-field")}</Option>);
  for (let i=0; i<templates?.length; i++) {
    metadataTemplateOptions.push(<Option key={`template-${i}`} value={i}>{templates[i]["label"]}</Option>);
  }

  return (
    <Popover
      content={
        <Space direction="vertical">
          <Select defaultValue={-1} style={{ width: 150 }} onChange={onTemplateChange}>
            {metadataTemplateOptions}
          </Select>
          <div
            key={"select-all"}
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center"
            }}
          >
            <Typography.Text strong>{i18n("visualization.phylogenomics.select-all")}</Typography.Text>
            <Switch
              checked={allSelected}
              onChange={(checked, event) => onSelectAllChange(event, checked)}
              size="small"
            />
          </div>
          {Object.keys(fields)?.map((field) => (
            <Space key={field} direction="horizontal">
              <Checkbox
                checked={fields[field]}
                onChange={(event) => onFieldChecked(event, field)}
              >
                <Typography.Text strong>{field}</Typography.Text>
              </Checkbox>
            </Space>
          ))}
        </Space>
      }
      placement="bottomRight"
    >
      <Button shape="circle" icon={<FilterOutlined />} />
    </Popover>
  );
}