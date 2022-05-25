import React from "react";
import { useSelector, useDispatch } from "react-redux";
import { Button, Dropdown, Menu, Select } from "antd";
import { FilterOutlined } from "@ant-design/icons";
const { Option } = Select;
import {
  fetchMetadataTemplateFields,
  selectAllTerms,
  setFieldVisibility,
} from "../../redux/treeSlice";
import { MetadataSelectAll } from "./MetadataSelectAll";
import { MetadataFieldSelect } from "./MetadataFieldToggle";

export function MetadataMenu() {
  const { templates, terms, treeProps } = useSelector((state) => state.tree);
  const dispatch = useDispatch();
  const width = 300;

  const metadataTemplateOptions = [];
  metadataTemplateOptions.push(
    <Option key="template-default" value={-1}>
      {i18n("visualization.phylogenomics.select-template.all-field")}
    </Option>
  );
  for (let i = 0; i < templates?.length; i++) {
    metadataTemplateOptions.push(
      <Option key={`template-${i}`} value={i}>
        {templates[i]["label"]}
      </Option>
    );
  }

  const menu = (
    <Menu>
      <Menu.Item key="1">
        <Select
          defaultValue={-1}
          style={{ width: width }}
          onChange={(templateIdx) =>
            dispatch(fetchMetadataTemplateFields(templateIdx))
          }
          onClick={(e) => e.stopPropagation()}
        >
          {metadataTemplateOptions}
        </Select>
      </Menu.Item>
      {terms.length > 0 ? (
        <Menu.Item key="2">
          <MetadataSelectAll
            checked={
              JSON.stringify(terms.slice().sort()) ===
              JSON.stringify(treeProps.blocks.slice().sort())
            }
            onChange={(checked) => dispatch(selectAllTerms({ checked }))}
          />
        </Menu.Item>
      ) : null}
      {terms.map((field) => (
        <Menu.Item key={field}>
          <MetadataFieldSelect
            checked={treeProps.blocks.includes(field)}
            field={field}
            width={width}
            onChange={(visible, only) =>
              dispatch(setFieldVisibility({ field, visible, only }))
            }
          />
        </Menu.Item>
      ))}
    </Menu>
  );

  return (
    <Dropdown overlay={menu} placement="bottomRight" trigger="click">
      <Button shape="circle" icon={<FilterOutlined />} />
    </Dropdown>
  );
}
