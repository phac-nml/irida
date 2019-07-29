import React from "react";
import { Form } from "antd";

let EditableRowContext;
const { Provider, Consumer } = (EditableRowContext = React.createContext());

function EditableRowProvider({ form, ...props }) {
  return (
    <Provider value={form}>
      <tr {...props} />
    </Provider>
  );
}

const EditableFormRow = Form.create()(EditableRowProvider);

export { EditableRowContext, EditableFormRow, Consumer as EditableRowConsumer };
