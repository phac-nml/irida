import React from "react";
import { render } from "react-dom";
import { Button, Checkbox, Modal, Typography } from "antd";
import { CodeOutlined } from "@ant-design/icons";
import { grey2, grey9 } from "../../../../styles/colors";
import { getNGSLinkerCode } from "../../../../apis/linker/linker";
import { SPACE_SM } from "../../../../styles/spacing";
import { BORDER_RADIUS, BORDERED_LIGHT } from "../../../../styles/borders";
import styled from "styled-components";

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
  const options = [
    { label: "FASTQ", value: "fastq" },
    { label: "Assembly", value: "assembly" }
  ];

  let modal = undefined; // <-- variable to hold a reference to the modal

  const ModalContents = (
    { command } // <-- A react component holding the contents of the modal
  ) => (
    <>
      {" "}
      // NOTE: passing in the command not storing as a state object
      <Paragraph>{i18n("Linker.details")}</Paragraph>
      <Text type="secondary">
        <span dangerouslySetInnerHTML={{ __html: i18n("Linker.note") }} />
      </Text>
      <CommandText
        className="t-cmd-text"
        ellipsis={{ rows: 1 }}
        copyable={{ text: { command } }}
      >
        {command}
      </CommandText>
      <Checkbox.Group
        options={options}
        defaultValue={["fastq"]}
        onChange={updateCommand}
      />
    </>
  );

  function handleSampleIds(e) {
    // Post data to get linker command.
    const { detail } = e;
    getNGSLinkerCode(detail).then(({ data }) => {
      modal = Modal.success({
        className: "t-linker-modal",
        width: 500,
        title: i18n("Linker.title"),
        content: <ModalContents command={data} /> // <-- Set the content to an instance of the ModalContents and pas in the command
      }); //     This allows it to be updatable (see below_
    });
  }

  function updateCommand(checkedValues) {
    //at some point here i'm going to add some filetype params.  it just absolutely won't update the state of the command.
    console.log(checkedValues);
    modal.update({
      content: <ModalContents command={`UPDATED: ${Date.now()}`} /> // <-- This is how to update the contents of the modal, create a new instance of the contents and
    }); //     pass in the updated command
  }

  /*
  These Listeners and Dispatchers are a way to get around the separation between react
  components and the legacy JS code already on the page.
  */
  document.addEventListener("sample-ids-return", handleSampleIds, false);

  function showModal() {
    document.dispatchEvent(new Event("sample-ids"));
  }

  return (
    <Button
      type="link"
      style={{
        margin: `0 inherit`,
        padding: 0,
        color: grey9
      }}
      className="t-linker-btn"
      onClick={showModal}
    >
      {/* constant marginRight here to match old styles */}
      <CodeOutlined style={{ marginRight: 2 }} />
      {i18n("project.samples.export.linker")}
    </Button>
  );
}

render(<Linker />, document.querySelector("#linker"));
