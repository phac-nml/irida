import React, { useEffect, useState } from "react";
import { EditableRowConsumer } from "../../../../../../contexts/EditableRowContext";
import { Form, Input } from "antd";

const { Item } = Form;

function InputCell({ value, blurred }) {
  let input;

  function keydownListener(e) {
    if (e.key === "Escape" || e.key === "Esc" || e.keyCode === 27) {
      blurred();
    }
  }

  useEffect(() => {
    document.addEventListener("keydown", keydownListener, false);
    input.focus();
    return () => document.removeEventListener("keydown", keydownListener);
  });

  const save = e => {
    console.log("HOW SO WE HANDLE SAVING NOW?");
  };

  return (
    <Input
      defaultValue={value}
      ref={node => (input = node)}
      onPressEnter={save}
      onBlur={blurred}
    />
  );
}

function EditableCell({
  record,
  handleSave,
  editable,
  dataIndex,
  title,
  index,
  children,
  ...props
}) {
  let input;
  const [editing, setEditing] = useState(false);
  useEffect(() => {
    if (editing) {
    }
  }, [editing]);

  const toggleEditing = () => setEditing(!editing);

  const blurred = () => {
    setEditing(false);
  };

  const renderCell = form =>
    editing ? (
      <Item style={{ margin: 0 }}>
        <InputCell value={record[dataIndex]} blurred={blurred} />
      </Item>
    ) : (
      <div
        className="editable-cell-value-wrap"
        style={{ paddingRight: 24 }}
        onClick={() => setEditing(true)}
      >
        {children}
      </div>
    );

  return (
    <td {...props}>
      {editable ? (
        <EditableRowConsumer>{renderCell}</EditableRowConsumer>
      ) : (
        children
      )}
    </td>
  );
}

export { EditableCell };
