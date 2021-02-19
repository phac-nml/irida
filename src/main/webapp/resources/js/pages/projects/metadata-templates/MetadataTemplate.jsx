import React from "react";
import { List, PageHeader, Table, Tag, Transfer, Typography } from "antd";
import { navigate } from "@reach/router";
import difference from "lodash/difference";
import { getMetadataTemplate } from "../../../apis/metadata/metadata-templates";
import { MetadataFieldCreate } from "./MetadataFieldCreate";

const { Paragraph, Text } = Typography;

const TableTransfer = ({ leftColumns, rightColumns, ...restProps }) => (
  <Transfer {...restProps} showSelectAll={false}>
    {({
      direction,
      filteredItems,
      onItemSelectAll,
      onItemSelect,
      selectedKeys: listSelectedKeys,
      disabled: listDisabled,
    }) => {
      const columns = direction === "left" ? leftColumns : rightColumns;

      const rowSelection = {
        getCheckboxProps: (item) => ({
          disabled: listDisabled || item.disabled,
        }),
        onSelectAll(selected, selectedRows) {
          const treeSelectedKeys = selectedRows
            .filter((item) => !item.disabled)
            .map(({ key }) => key);
          const diffKeys = selected
            ? difference(treeSelectedKeys, listSelectedKeys)
            : difference(listSelectedKeys, treeSelectedKeys);
          onItemSelectAll(diffKeys, selected);
        },
        onSelect({ key }, selected) {
          onItemSelect(key, selected);
        },
        selectedRowKeys: listSelectedKeys,
      };

      return (
        <Table
          rowSelection={rowSelection}
          columns={columns}
          dataSource={filteredItems}
          size="small"
          style={{ pointerEvents: listDisabled ? "none" : null }}
          onRow={({ key, disabled: itemDisabled }) => ({
            onClick: () => {
              if (itemDisabled || listDisabled) return;
              onItemSelect(key, !listSelectedKeys.includes(key));
            },
          })}
        />
      );
    }}
  </Transfer>
);

const mockTags = ["cat", "dog", "bird"];

const mockData = [];
for (let i = 0; i < 20; i++) {
  mockData.push({
    key: i.toString(),
    title: `content${i + 1}`,
    description: `description of content${i + 1}`,
    disabled: i % 4 === 0,
    tag: mockTags[i % 3],
  });
}

const originTargetKeys = mockData
  .filter((item) => +item.key % 3 > 1)
  .map((item) => item.key);

const leftTableColumns = [
  {
    dataIndex: "title",
    title: "Name",
  },
  {
    dataIndex: "tag",
    title: "Tag",
    render: (tag) => <Tag>{tag}</Tag>,
  },
  {
    dataIndex: "description",
    title: "Description",
  },
];
const rightTableColumns = [
  {
    dataIndex: "title",
    title: "Name",
  },
];

export function MetadataTemplate({ id }) {
  const [template, setTemplate] = React.useState({});

  const [data, setData] = React.useState(mockData);
  const [selectedKeys, setSelectedKeys] = React.useState([]);
  const [targetKeys, setTargetKeys] = React.useState(
    mockData.filter((item) => +item.key % 3 > 1).map((item) => item.key)
  );

  const onChange = (nextTargetKeys) => {
    setTargetKeys(nextTargetKeys);
  };

  React.useEffect(() => {
    getMetadataTemplate(id).then((data) => setTemplate(data));
  }, []);

  return (
    <PageHeader title={template.name} onBack={() => navigate("./")}>
      <List itemLayout="vertical">
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
              <div style={{ display: "flex", justifyContent: "space-between" }}>
                <Text strong>Metadata Fields</Text>
                <MetadataFieldCreate />
              </div>
            }
          />
          <TableTransfer
            dataSource={mockData}
            targetKeys={targetKeys}
            showSearch={true}
            onChange={onChange}
            filterOption={(inputValue, item) =>
              item.title.indexOf(inputValue) !== -1 ||
              item.tag.indexOf(inputValue) !== -1
            }
            leftColumns={leftTableColumns}
            rightColumns={rightTableColumns}
          />
        </List.Item>
      </List>
    </PageHeader>
  );
}
