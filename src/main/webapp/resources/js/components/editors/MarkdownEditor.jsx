import React, { forwardRef, useImperativeHandle, useState } from "react";
import RichTextEditor from "react-rte";
import styled from "styled-components";
import { Tabs } from "antd";
import { ANT_DESIGN_FONT_FAMILY } from "../../styles/fonts";

const StyledEditor = styled.div`
  .DraftEditor-root {
    min-height: 200px;
    font-family: ${ANT_DESIGN_FONT_FAMILY};
  }
`;

const TOOLBAR_CONFIG = {
  // Optionally specify the groups to display (displayed in the order listed).
  display: [
    "INLINE_STYLE_BUTTONS",
    "BLOCK_TYPE_BUTTONS",
    "LINK_BUTTONS",
    "BLOCK_TYPE_DROPDOWN",
    "HISTORY_BUTTONS"
  ],
  INLINE_STYLE_BUTTONS: [
    { label: "Bold", style: "BOLD", className: "custom-css-class" },
    { label: "Italic", style: "ITALIC" },
    { label: "Underline", style: "UNDERLINE" },
    { label: "Monospace", style: "CODE" }
  ],
  BLOCK_TYPE_DROPDOWN: [
    { label: "Normal", style: "unstyled" },
    { label: "Heading", style: "header-three" }
  ],
  BLOCK_TYPE_BUTTONS: [
    { label: "UL", style: "unordered-list-item" },
    { label: "OL", style: "ordered-list-item" }
  ]
};

export const MarkdownEditor = forwardRef((props, ref) => {
  const [editorState, setEditorState] = useState(
    props.markdown
      ? RichTextEditor.createValueFromString(props.markdown, "markdown")
      : RichTextEditor.createEmptyValue()
  );

  useImperativeHandle(ref, () => ({
    getMarkdown() {
      const markdown = editorState.toString("markdown");
      setEditorState(RichTextEditor.createEmptyValue());
      return markdown;
    }
  }));

  function onChange(value) {
    setEditorState(value);
  }

  function onTextChange(event) {
    setEditorState(
      editorState.setContentFromString(event.target.value, "markdown")
    );
  }

  return (
    <StyledEditor>
      <Tabs animated={false}>
        <Tabs.TabPane tab={i18n("MarkdownEditor.write")} key={"write"}>
          <RichTextEditor
            autoFocus
            toolbarConfig={TOOLBAR_CONFIG}
            value={editorState}
            onChange={onChange}
          />
        </Tabs.TabPane>
        <Tabs.TabPane tab={i18n("MarkdownEditor.paste")} key={"paste"}>
          <textarea
            value={editorState.toString("markdown")}
            style={{
              width: "100%",
              border: `1px solid #ddd`,
              borderRadius: 3,
              padding: 9
            }}
            onChange={onTextChange}
            rows={10}
          />
        </Tabs.TabPane>
        <Tabs.TabPane tab={i18n("MarkdownEditor.Preview")} key={"preview"}>
          <div>
            <RichTextEditor value={editorState} readOnly />
          </div>
        </Tabs.TabPane>
      </Tabs>
    </StyledEditor>
  );
});
