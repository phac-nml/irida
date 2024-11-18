import { Button, Modal, Table } from "antd";
import React from "react";

/**
 * Component for adding metadata fields to an existing metadata template
 *
 * @param {Object[]} fields - available metadata fields that are not on the template.
 * @param {Function} onAddFields - callback function to add the fields to the template.
 * @returns {JSX.Element}
 * @constructor
 */
export function MetadataAddTemplateField({ fields = [], onAddFields }) {
  const [open, setOpen] = React.useState(false);
  const [selected, setSelected] = React.useState([]);
  const [selectedFields, setSelectedFields] = React.useState([]);

  React.useEffect(() => {
    /*
    When fields are selected, Ant Table only passes the key for the entry,
    here we are setting the selected fields as the entire field value, based
    on the selected keys.
     */
    if (fields && selected.length) {
      const set = new Set(selected);
      setSelectedFields(fields.filter((field) => set.has(field.key)));
    }
  }, [fields, selected]);

  /**
   * Send the currently selected to the callback function.  When the callback
   * is successfully completed, close the modal.
   */
  const addFieldsToTemplate = () =>
    onAddFields(selectedFields).then(() => setOpen(false));

  return (
    <>
      <Button disabled={fields.length === 0} onClick={() => setOpen(true)}>
        {i18n("MetadataAddTemplateField.button")}
      </Button>
      <Modal
        title={i18n("MetadataAddTemplateField.title")}
        open={open}
        onCancel={() => setOpen(false)}
        onOk={addFieldsToTemplate}
        okText={i18n("MetadataAddTemplateField.ok-text")}
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
  );
}
