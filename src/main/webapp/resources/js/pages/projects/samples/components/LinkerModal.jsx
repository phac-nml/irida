import React from "react";
import {
  Alert,
  Button,
  Checkbox,
  Form,
  Input,
  Modal,
  Space,
  Typography,
} from "antd";
import styled from "styled-components";
import { SPACE_SM } from "../../../../styles/spacing";
import { BORDER_RADIUS, BORDERED_LIGHT } from "../../../../styles/borders";
import { grey2 } from "../../../../styles/colors";
import { LoadingOutlined } from "@ant-design/icons";
import { getNGSLinkerCode } from "../../../../apis/linker/linker";

const CommandText = styled(Typography.Paragraph)`
  margin: 0px !important;
  font-family: monospace;
  font-size: 14px;
`;

const CommandWrapper = styled.div`
  margin-top: ${SPACE_SM};
  padding: 2px;
  background-color: ${grey2};
  border: ${BORDERED_LIGHT};
  border-radius: ${BORDER_RADIUS};
`;

export default function LinkerModal({
  visible,
  sampleIds,
  projectId,
  onFinish,
}) {
  const [form] = Form.useForm();
  const [scriptString, setScriptString] = React.useState();
  const [command, setCommand] = React.useState();
  const [error, setError] = React.useState(false);

  const updateCommand = () => {
    const types = form.getFieldValue("type");
    setCommand(
      types.length ? `${scriptString} -t ${types.join(",")}` : scriptString
    );
  };

  const options = [
    { label: i18n("Linker.fastq"), value: "fastq" },
    { label: i18n("Linker.assembly"), value: "assembly" },
  ];

  React.useEffect(() => {
    getNGSLinkerCode({ sampleIds, projectId })
      .then(({ data }) => {
        // Post data to the server to get the linker command.
        setScriptString(data);
      })
      .catch(() => setError(true));
  }, [projectId, sampleIds]);

  React.useEffect(updateCommand, [scriptString]);

  return (
    <Modal
      className="t-linker-modal"
      open={visible}
      title={i18n("Linker.title")}
      onCancel={onFinish}
      footer={
        <div style={{ display: "flex", flexDirection: "row-reverse" }}>
          <Button type="primary" onClick={onFinish}>
            {i18n("Linker.close")}
          </Button>
        </div>
      }
    >
      <Typography.Paragraph>{i18n("Linker.details")}</Typography.Paragraph>
      <Typography.Text type="secondary">
        <span dangerouslySetInnerHTML={{ __html: i18n("Linker.note") }} />
      </Typography.Text>
      <Form
        form={form}
        layout="inline"
        initialValues={{ type: ["fastq"] }}
        onValuesChange={updateCommand}
      >
        <Form.Item label={i18n("Linker.types")} name="type">
          <Checkbox.Group options={options} />
        </Form.Item>
        <Form.Item hidden name="command" value={command}>
          <Input className="t-linker-cmd" />
        </Form.Item>
      </Form>
      {error ? (
        <Alert message={i18n("Linker.error")} type="error" showIcon />
      ) : (
        <CommandWrapper>
          {scriptString === undefined ? (
            <Space>
              <LoadingOutlined />
              <span>{i18n("Linker.loading")}</span>
            </Space>
          ) : (
            <CommandText
              className="t-cmd-text"
              ellipsis={{ rows: 1 }}
              copyable={command}
            >
              {command}
            </CommandText>
          )}
        </CommandWrapper>
      )}
    </Modal>
  );
}
