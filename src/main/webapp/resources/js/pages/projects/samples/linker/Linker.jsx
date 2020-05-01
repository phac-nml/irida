import React, { useEffect, useState } from "react";
import { render } from "react-dom";
import { Button, Checkbox, Form, Modal, Typography } from "antd";

import { grey2, grey9 } from "../../../../styles/colors";
import { getNGSLinkerCode } from "../../../../apis/linker/linker";
import { SPACE_SM } from "../../../../styles/spacing";
import { BORDER_RADIUS, BORDERED_LIGHT } from "../../../../styles/borders";
import styled from "styled-components";
import { IconCode } from "../../../../components/icons/Icons";

const { Paragraph, Text } = Typography;

const CommandText = styled(Paragraph)`
  font-family: monospace;
  font-size: 14px;
  margin-top: ${SPACE_SM};
  padding: 2px;
  background-color: ${grey2};
  border: ${BORDERED_LIGHT};
  border-radius: ${BORDER_RADIUS};
`;

/**
 * React component to display the ngs-linker command to a user based
 * on the currently selected samples.
 * @returns {*}
 * @constructor
 */
function Linker() {
  const [visible, setVisible] = useState(false);
  const [types, setTypes] = useState(["fastq"]);
  const [scriptString, setScriptString] = useState();
  const [command, setCommand] = useState();

  const options = [
    { label: i18n("Linker.fastq"), value: "fastq" },
    { label: i18n("Linker.assembly"), value: "assembly" }
  ];

  function handleSampleIds({ detail }) {
    getNGSLinkerCode(detail).then(({ data }) => {
      // Post data to the server to get the linker command.
      setScriptString(data);
      setVisible(true);
    });
  }

  useEffect(() => {
    setCommand(
      types.length ? `${scriptString} -t ${types.join(",")}` : scriptString
    );
  }, [scriptString, types]);

  /*
  These Listeners and Dispatchers are a way to get around the separation between react
  components and the legacy JS code already on the page.
  */
  document.addEventListener("sample-ids-return", handleSampleIds, false);

  function getIds() {
    document.dispatchEvent(new Event("sample-ids"));
  }

  return (
    <>
      <Button
        type="link"
        style={{
          margin: `0 inherit`,
          padding: 0,
          color: grey9
        }}
        className="t-linker-btn"
        onClick={getIds}
      >
        {/* constant marginRight here to match old styles */}
        <IconCode style={{ marginRight: 2 }} />
        {i18n("project.samples.export.linker")}
      </Button>
      <Modal
        title={i18n("Linker.title")}
        className="t-linker-modal"
        visible={visible}
        footer={null}
        onCancel={() => setVisible(false)}
      >
        <Paragraph>{i18n("Linker.details")}</Paragraph>
        <Text type="secondary">
          <span dangerouslySetInnerHTML={{ __html: i18n("Linker.note") }} />
        </Text>
        <Form layout="inline">
          <Form.Item label={i18n("Linker.types")}>
            <Checkbox.Group
              options={options}
              defaultValue={["fastq"]}
              onChange={setTypes}
            />
          </Form.Item>
        </Form>
        <CommandText
          className="t-cmd-text"
          ellipsis={{ rows: 1 }}
          copyable={command}
        >
          {command}
        </CommandText>
      </Modal>
    </>
  );
}

render(<Linker />, document.querySelector("#linker"));
