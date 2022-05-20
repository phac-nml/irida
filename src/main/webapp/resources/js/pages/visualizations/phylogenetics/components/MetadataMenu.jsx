import React from "react";
import { useSelector, useDispatch } from "react-redux";
import { Button, Checkbox, Popover, Select, Space, Switch, Typography } from "antd";
import { FilterOutlined } from "@ant-design/icons";
const { Option } = Select;
import { fetchMetadataTemplateFields, selectAllTerms, setFieldVisibility } from '../redux/treeSlice';

export function MetadataMenu() {
  const { templates, terms, treeProps } = useSelector((state) => state.tree);
  const dispatch = useDispatch();

  const metadataTemplateOptions = [];
  metadataTemplateOptions.push(<Option key="template-default" value={-1}>{i18n("visualization.phylogenomics.select-template.all-field")}</Option>);
  for (let i=0; i<templates?.length; i++) {
    metadataTemplateOptions.push(<Option key={`template-${i}`} value={i}>{templates[i]["label"]}</Option>);
  }

  const onFieldChecked = (event, field) => {
    event.stopPropagation();
    dispatch(setFieldVisibility({field, visible: event.target.checked}));
  };

  const onSelectAllChange = (event, checked) => {
    event.stopPropagation();
    dispatch(selectAllTerms({checked}));
  };

  const onTemplateChange = (templateIdx) => {
    dispatch(fetchMetadataTemplateFields(templateIdx));
  };

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
              checked={JSON.stringify(terms.slice().sort()) === JSON.stringify(treeProps.blocks.slice().sort())}
              onChange={(checked, event) => onSelectAllChange(event, checked)}
              size="small"
            />
          </div>
          {terms.map((field) => (
            <Space key={field} direction="horizontal">
              <Checkbox
                checked={treeProps.blocks.includes(field)}
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