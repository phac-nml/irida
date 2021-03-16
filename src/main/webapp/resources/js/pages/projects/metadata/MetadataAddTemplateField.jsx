import React from "react";
import { Button, Modal, Table } from "antd";

export function MetadataAddTemplateField({ fields = [], onAddFields }) {
  const [visible, setVisible] = React.useState(false);
  const [selected, setSelected] = React.useState([]);
  const [selectedFields, setSelectedFields] = React.useState([]);

  React.useEffect(() => {
    /*
    When fields are selected, Ant Table only five the key, here we are setting
    the selected fields as the entire field value.
     */
    if (fields && selected.length) {
      const set = new Set(selected);
      setSelectedFields(fields.filter((field) => set.has(field.key)));
    }
  }, [fields, selected]);

  const onOk = () => onAddFields(selectedFields).then(() => setVisible(false));

  return fields.length ? (
    <>
      <Button onClick={() => setVisible(true)}>Add Field </Button>
      <Modal
        title={"Select fields to add to template"}
        visible={visible}
        onCancel={() => setVisible(false)}
        onOk={onOk}
      >
        <Table
          rowSelection={{
            selectedRowKeys: selected,
            onChange: setSelected,
          }}
          showHeader={false}
          pagination={false}
          columns={[
            {
              dataIndex: "label",
            },
          ]}
          dataSource={fields}
        />
      </Modal>
    </>
  ) : null;
}
