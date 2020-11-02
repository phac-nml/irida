import React from "react";
import { Button, Form, Input } from "antd";
import { useLaunchState } from "../../launch-context";

export function SaveParametersAsFooter({ onCancel, onSaveAs }) {
  const { parameterSet } = useLaunchState();
  const inputRef = React.useRef();

  const [name, setName] = React.useState(
    i18n("SaveParametersAsFooter.default", parameterSet.label)
  );

  React.useEffect(() => {
    setTimeout(() => {
      inputRef.current.focus();
      inputRef.current.select();
    });
  }, []);

  return (
    <Form layout="vertical">
      <Form.Item label={i18n("SaveParametersAsFooter.name")}>
        <Input
          ref={inputRef}
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
      </Form.Item>
      <Button onClick={onCancel}>
        {i18n("SaveParametersAsFooter.cancel")}
      </Button>
      <Button onClick={() => onSaveAs(name)} type="primary">
        {i18n("SaveParametersAsFooter.save")}
      </Button>
    </Form>
  );
}
