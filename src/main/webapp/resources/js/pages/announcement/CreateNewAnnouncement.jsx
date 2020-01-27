import React, { useRef, useState } from "react";
import { Modal } from "antd";
import { AddNewButton } from "../../components/Buttons/AddNewButton";
import { MarkdownEditor } from "../../components/editors/MarkdownEditor";

export function CreateNewAnnouncement({createAnnouncement}) {
  const [visibility, setVisibility] = useState(false);
  const markdownRef = useRef();

  function saveMarkdown() {
    setVisibility(false);
    const md = markdownRef.current.getMarkdown();
    createAnnouncement(md);
  }

  return (
    <>
      <AddNewButton
        onClick={() => setVisibility(true)}
        text={i18n("announcement.create.title")}
      />
      <Modal
        title={i18n("announcement.create.title")}
        width={"80%"}
        visible={visibility}
        onCancel={() => setVisibility(false)}
        onOk={saveMarkdown}
      >
        <MarkdownEditor ref={markdownRef} />
      </Modal>
    </>
  );
}
