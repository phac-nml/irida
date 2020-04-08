import React from "react";
import { render } from "react-dom";
import { Button, Modal, Typography } from "antd";
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
  const options = [
    { label: i18n("Linker.fastq"), value: "fastq" },
    { label: i18n("Linker.assembly"), value: "assembly" }
  ];

  //this will hold the base string of the script with the projects & samples.  The filetypes will be appended before displaying.
  let scriptString = undefined;

  let modal = undefined; 

  const ModalContents = (
    { command } 
  ) => (
    <>
      {" "}
      <Paragraph>{i18n("Linker.details")}</Paragraph>
      <Text type="secondary">
        <span dangerouslySetInnerHTML={{ __html: i18n("Linker.note") }} />
      </Text>
      <br/><br/>
      <Text strong>{i18n("Linker.types")}</Text> <Checkbox.Group
        options={options}
        defaultValue={["fastq"]}
        onChange={updateCommand}
      />
      <CommandText
        className="t-cmd-text"
        ellipsis={{ rows: 1 }}
        copyable={ command }
      >
        {command}
      </CommandText>
      
    </>
  );

  function handleSampleIds(e) {
    // Post data to the server to get the linker command.
    const { detail } = e;
    getNGSLinkerCode(detail).then(({ data }) => {
      scriptString = data;
      
      //initialize the modal
      modal = Modal.success({
        className: "t-linker-modal",
        width: 500,
        title: i18n("Linker.title"),
        content: "..."
      }); 

      //set the default modal contents
      updateCommand(["fastq"]);
    });
  }

  /**
   * Update the command in the modal showing the apporpirate file tpes
   * @param {*} checkedValues the filetypes selected
   */
  function updateCommand(checkedValues) {
    let typeString = "";

    //join the checked values
    if(checkedValues.length > 0){
      typeString = " -t " + checkedValues.join(",");
    }

    //update the modal contents with the new info
    modal.update({
      content: <ModalContents command={scriptString + typeString} /> 
    }); 
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
      <IconCode style={{ marginRight: 2 }} />
      {i18n("project.samples.export.linker")}
    </Button>
  );
}

render(<Linker />, document.querySelector("#linker"));
