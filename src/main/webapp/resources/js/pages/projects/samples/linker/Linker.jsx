import React, { useRef } from "react";
import { render } from "react-dom";
import { Button, Input, Modal } from "antd";
import { CodeOutlined, CopyOutlined } from "@ant-design/icons";
import { grey9 } from "../../../../styles/colors";
import { getNGSLinkerCode } from "../../../../apis/linker/linker";

function Linker() {
  const inputRef = useRef();

  function copyToClipboard() {
    inputRef.current.select();
    document.execCommand("copy");
  }

  function handleSampleIds(e) {
    // Post data to get linker command.
    const { detail } = e;
    getNGSLinkerCode(detail).then(({ data }) => {
      Modal.success({
        title: i18n("ngs.linker.title"),
        content: (
          <div>
            <Input
              ref={inputRef}
              addonAfter={
                <CopyOutlined onClick={copyToClipboard} />
              }
              value={data}
            />
          </div>
        )
      });
    });
  }

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
      onClick={showModal}
    >
      {/* constant marginRight here to match old styles */}
      <CodeOutlined style={{ marginRight: 2 }} />
      {i18n("project.samples.export.linker")}
    </Button>
  );
}

render(<Linker />, document.querySelector("#linker"));
