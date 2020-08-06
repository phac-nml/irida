import React, { useEffect, useRef, useState } from "react";
import { setBaseUrl } from "../../utilities/url-utilities";
import { Checkbox, Divider, Form, Input, Modal, Select } from "antd";

export default function PipelineDetailsModal({
  visible,
  id,
  onCancel,
  automated,
}) {
  const [details, setDetails] = useState({});
  const [name, setName] = useState("");
  const [reference, setReference] = useState(undefined);
  const nameRef = useRef();

  const selectNameText = () => nameRef.current.select();
  const formatName = (n) => `${n.replace(/ /g, "_")}_${Date.now()}`;

  useEffect(() => {
    if (visible) {
      setTimeout(() => nameRef.current.focus(), 100);
    }
  }, [visible]);

  useEffect(() => {
    fetch(setBaseUrl(`/ajax/pipelines/${id}?automated=${automated}`), {})
      .then((response) => response.json())
      .then((json) => {
        setDetails(json);
        setName(formatName(json.name));
        if (typeof json.files !== "undefined" && json.files !== null) {
          setReference(json.files[0].id);
        }
      });
  }, [id, visible]);

  const updateName = (event) => setName(event.currentTarget.value);

  return (
    <Modal
      title={i18n("PipelineDetailsModal.title", details.name)}
      visible={visible}
      onCancel={onCancel}
    >
      <Form layout="vertical">
        <Form.Item label={"NAME"}>
          <Input
            type={"text"}
            value={name}
            ref={nameRef}
            onFocus={selectNameText}
            onChange={updateName}
          />
        </Form.Item>
        <Form.Item label={"DESCRIPTION"}>
          <Input.TextArea />
        </Form.Item>
        {typeof reference !== "undefined" ? (
          <Form.Item label={`REFERENCE FILES`}>
            <Select defaultValue={reference}>
              {details.files.map((file) => (
                <Select.Option key={file.id} value={file.id}>
                  {file.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
        ) : null}
        {details.canPipelineWriteToSamples ? (
          <Form.Item help={"Need to figure out help text"}>
            <Checkbox>Save results to sample</Checkbox>
          </Form.Item>
        ) : null}
        <Divider />
      </Form>
    </Modal>
  );
}
