import React from "react";
import { Form, Input, List, Modal, Tag } from "antd";
import { useSelector } from "react-redux";

export default function FilterByFileModal({ visible, onComplete, onCancel }) {
  const { options, projectId } = useSelector((state) => state.samples);
  const [contents, setContents] = React.useState("");
  const [valid, setValid] = React.useState([]);
  const [invalid, setInvalid] = React.useState([]);

  const onFileAdded = (e) => {
    const [file] = e.target.files;

    const reader = new FileReader();
    reader.addEventListener("load", (e) => {
      if (e.target.readyState === FileReader.DONE) {
        // DONE == 2
        setContents(e.target.result);
      }
    });

    const blob = file.slice(0, file.size - 1);
    reader.readAsText(blob);
  };

  React.useEffect(() => {
    if (contents.length) {
      const associated = options.filters.associated || [];
      let parsed = contents.split(/[\s,]+/);
      const projectIds = [projectId, ...associated];

      fetch(`/ajax/samples/validate`, {
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        method: "POST",
        body: JSON.stringify({
          projectIds,
          names: parsed,
        }),
      })
        .then((response) => response.json())
        .then(({ valid, invalid }) => {
          setValid(valid);
          setInvalid(invalid);
        });
    } else {
      setValid([]);
      setInvalid([]);
    }
  }, [contents]);

  return (
    <Modal visible={visible} onCancel={onCancel} onOk={onComplete}>
      <Form layout="vertical">
        <Form.Item label={"Select file containing sample names"}>
          <Input type="file" onChange={onFileAdded} />
        </Form.Item>
      </Form>
      {valid.length > 0 && (
        <List
          bordered
          size="small"
          dataSource={valid}
          renderItem={(sample) => (
            <List.Item>
              {name}
              <Tag>{sample.projectName}</Tag>
            </List.Item>
          )}
        />
      )}
      {invalid.length > 0 && (
        <List
          title={"SAMPLES NOT FOUND IN PROJECTS"}
          bordered
          size="small"
          dataSource={invalid}
          renderItem={(name) => <List.Item>{name}</List.Item>}
        />
      )}
    </Modal>
  );
}
