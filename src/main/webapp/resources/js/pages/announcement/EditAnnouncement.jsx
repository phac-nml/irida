import React, { useRef, useState } from "react";
import { SPACE_XS } from "../../styles/spacing";
import { EditOutlined } from "@ant-design/icons";
import { Button, Modal } from "antd";
import { MarkdownEditor } from "../../components/editors/MarkdownEditor";

export function EditAnnouncement({ announcement, updateAnnouncement }) {
  const [visibility, setVisibility] = useState(false);
  const markdownRef = useRef();

  function saveMarkdown() {
    setVisibility(false);
    const md = markdownRef.current.getMarkdown();
    updateAnnouncement({ message: md, id: announcement.id });
  }

  return (
    <span>
      <Button shape={"circle"} onClick={() => setVisibility(true)}>
        <EditOutlined />
      </Button>
      <Modal
        title={i18n("announcement.create.title")}
        width={"80%"}
        visible={visibility}
        onCancel={() => setVisibility(false)}
        onOk={saveMarkdown}
      >
        <MarkdownEditor ref={markdownRef} markdown={announcement.message} />
      </Modal>
    </span>
  );
}
