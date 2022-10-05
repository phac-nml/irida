import { FilterOutlined } from "@ant-design/icons";
import { Button, Dropdown, Menu, Select } from "antd";
import React, { useMemo } from "react";
import {
  fetchMetadataTemplateFieldsThunk,
  selectAllTerms,
  setFieldVisibility,
} from "../../redux/treeSlice";
import { useAppDispatch, useAppSelector } from "../../store";
import { MetadataFieldSelect } from "./MetadataFieldToggle";
import { MetadataSelectAll } from "./MetadataSelectAll";
const { Option } = Select;

/**
 * React component to display a drop-down menu for selecting the metadata that
 * is displayed with the tree.
 */
export function MetadataMenu(): JSX.Element {
  const { templates, terms, treeProps } = useAppSelector((state) => state.tree);
  const dispatch = useAppDispatch();
  const width = 300;

  const metadataTemplateOptions: JSX.Element[] = useMemo(() => {
    const options = templates.map((template, index) => (
      <Option
        className="t-template-field"
        key={`template-${template.id}`}
        value={index}
      >
        {template.label}
      </Option>
    ));

    options.unshift(
      <Option key="template-default" value={-1}>
        {i18n("visualization.phylogenomics.select-template.all-field")}
      </Option>
    );

    return options;
  }, [templates]);

  const allFieldsSelected: boolean = useMemo(
    () =>
      JSON.stringify(terms.slice().sort()) ===
      JSON.stringify(treeProps.blocks.slice().sort()),
    [terms, treeProps.blocks]
  );

  const menu = (
    <Menu className="t-metadata-dd">
      <Menu.Item key="templates">
        <Select
          className="t-metadata-select"
          defaultValue={-1}
          style={{ width }}
          onChange={(templateIdx) =>
            dispatch(fetchMetadataTemplateFieldsThunk(templateIdx))
          }
          onClick={(e) => e.stopPropagation()}
        >
          {metadataTemplateOptions}
        </Select>
      </Menu.Item>
      {terms.length > 0 ? (
        <Menu.Item key="select-all-fields" className="t-fields-all">
          <MetadataSelectAll
            checked={allFieldsSelected}
            onChange={(checked: boolean) =>
              dispatch(selectAllTerms({ checked }))
            }
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

  return terms.length > 0 ? (
    <Dropdown overlay={menu} placement="bottomRight" trigger={["click"]}>
      <Button
        title={i18n("PhylocanvasShapeDropDown.metadata.tooltip")}
        shape="circle"
        icon={<FilterOutlined />}
      />
    </Dropdown>
  ) : null;
}
