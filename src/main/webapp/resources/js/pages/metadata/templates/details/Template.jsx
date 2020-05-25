import React, { useState } from "react";
import { useMetadataTemplate } from "../../../../contexts/metadata-template-context";
import { Button, Col, Row, Space, Table } from "antd";

export function Template() {
  const { template } = useMetadataTemplate();
  const { fields } = template;

  const [columns, setColumns] = useState([
    {
      title: "Address",
      children: [
        {
          title: "Street",
          dataIndex: "street",
        },
        {
          title: "City",
          dataIndex: "city",
        },
      ],
    },
    {
      title: "What Pipeline?",
      dataIndex: "p0",
      children: [],
    },
    {
      title: "Another Pipeline",
      dataIndex: "p1",
    },
  ]);

  const addNewGroup = () => {
    const c = [...columns];
    c.push({
      title: "FOOBAR",
    });
    setColumns(c);
  };

  const addField = () => {
    const c = [...columns];
    c.find((i) => i.dataIndex === "p0").children.push({ title: "ADDED" });
    setColumns(c);
  };

  return (
    <>
      <Space direction="vertical">
        <Space>
          <Button onClick={addNewGroup}>New Field Group</Button>
          <Button onClick={addField}>New Field</Button>
        </Space>
        <Table
          style={{ width: 900 }}
          scroll={{ x: true }}
          bordered
          columns={columns}
          dataSource={[]}
          size="middle"
        />
      </Space>
    </>
  );
}
