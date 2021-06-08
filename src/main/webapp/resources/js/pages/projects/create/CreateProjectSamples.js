import { Button, Form, Input } from "antd";
import React from "react";
import { SampleDetailViewer } from "../../../components/samples/SampleDetailViewer";
import { Table } from "../linelist/components/Table";

export function CreateProjectSamples({ form, samples }) {
  const [organismFilter] = React.useState(() => {
    const exists = {};
    samples.unlocked.forEach((sample) => {
      if (!exists[sample.organism]) {
        exists[sample.organism] = {
          text: sample.organism,
          value: sample.organism,
        };
      }
    });
    return Object.values(exists);
  });
  const [selected, setSelected] = React.useState([]);
  const [lock, setLock] = React.useState(false);

  return (
    <>
      <Table
        rowSelection={{
          type: "checkbox",
          selectedRowKeys: selected,
          onChange: (selectedRowKeys, selectedRows) => {
            setSelected(selectedRowKeys);
            form.setFieldsValue({
              samples: selectedRows.map((s) => Number(s.identifier)),
            });
          },
        }}
        scroll={{ y: 600 }}
        pagination={false}
        dataSource={samples.unlocked}
        rowKey={(sample) => `sample-${sample.identifier}`}
        columns={[
          {
            title: "Name",
            dataIndex: "label",
            render: (text, sample) => (
              <SampleDetailViewer sampleId={sample.identifier}>
                <Button size="small">{sample.label}</Button>
              </SampleDetailViewer>
            ),
            onFilter: (value, record) =>
              record.label.toLowerCase().indexOf(value.toLowerCase()) >= 0,
          },
          {
            title: "Organism",
            dataIndex: "organism",
            filters: organismFilter,
            onFilter: (value, record) => record.organism === value,
          },
        ]}
      />
      <Form.Item name="samples" hidden>
        <Input />
      </Form.Item>
      <Form.Item name="lock" valuePropName="checked">
        <Checkbox
          disabled={selected.length === 0}
          checked={lock}
          onChange={(e) => setLock(e.target.checked)}
        >
          {i18n("projects.create.settings.sample.modification")}
        </Checkbox>
      </Form.Item>
    </>
  );
}
