import React, { useEffect, useState } from "react";
import { setBaseUrl } from "../../utilities/url-utilities";
import { Checkbox, Form, Input, Modal, Select } from "antd";

export default function PipelineDetailsModal({ visible, id, onCancel }) {
  const [details, setDetails] = useState({});

  useEffect(() => {
    fetch(setBaseUrl(`/ajax/pipelines/${id}`), {})
      .then((response) => response.json())
      .then((json) => setDetails(json));
  }, [id, visible]);

  return (
    <Modal
      title={i18n("PipelineDetailsModal.title", details.name)}
      visible={visible}
      onCancel={onCancel}
    >
      <Form layout="vertical">
        <Form.Item label={"NAME"}>
          <Input type={"text"} />
        </Form.Item>
        {details.files !== null ? (
          <Form.Item>
            <Select defaultValue={details.files[0].id}>
              {details.files.map((file) => (
                <Select.Option key={file.id} value={file.id}>
                  file.label
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
      </Form>
    </Modal>
  );
}
