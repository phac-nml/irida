import React from "react";
import { Button, Form, Input } from "antd";
import { useLaunchState } from "../../launch-context";

/**
 * React component to render the footer of the saved parameters modal
 * Complicated due to the nature of having to either save or just store
 * modified parameters.
 * @param {function} onCancel - called when the modal is cancelled
 * @param {function} onSaveAs - called when the parameters are saved as a new name
 * @returns {JSX.Element}
 * @constructor
 */
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
