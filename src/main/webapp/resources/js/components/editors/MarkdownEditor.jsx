import React, { forwardRef, useImperativeHandle, useState } from "react";
import RichTextEditor from "react-rte";
import styled from "styled-components";

import ReactMarkdown from "react-markdown";
import { Tabs } from "antd";

const StyledEditor = styled.div`
  .DraftEditor-root {
    min-height: 200px;
  }
`;

const TOOLBAR_CONFIG = {
  // Optionally specify the groups to display (displayed in the order listed).
  display: ['INLINE_STYLE_BUTTONS', 'BLOCK_TYPE_BUTTONS', 'LINK_BUTTONS', 'BLOCK_TYPE_DROPDOWN', 'HISTORY_BUTTONS'],
  INLINE_STYLE_BUTTONS: [
    {label: 'Bold', style: 'BOLD', className: 'custom-css-class'},
    {label: 'Italic', style: 'ITALIC'},
    {label: 'Underline', style: 'UNDERLINE'}
  ],
  BLOCK_TYPE_DROPDOWN: [
    {label: 'Normal', style: 'unstyled'},
    {label: 'Heading', style: 'header-three'}
  ],
  BLOCK_TYPE_BUTTONS: [
    {label: 'UL', style: 'unordered-list-item'},
    {label: 'OL', style: 'ordered-list-item'}
  ]
};

export const MarkdownEditor = forwardRef((props, ref) => {
  const [editorState, setEditorState] = useState(
    RichTextEditor.createEmptyValue()
  );

  useImperativeHandle(ref, () => ({
    getMarkdown() {
      return editorState.toString("markdown");
    }
  }));

  function onTextChange(value) {
    setEditorState(value);
  }

  return (
    <StyledEditor>
      <Tabs animated={false}>
        <Tabs.TabPane tab={"Write"} key={"write"}>
          <RichTextEditor toolbarConfig={TOOLBAR_CONFIG} value={editorState} onChange={onTextChange} />
        </Tabs.TabPane>
        <Tabs.TabPane tab={"Preview"} key={"preview"}>
          <div>
            <ReactMarkdown source={editorState.toString("markdown")} />
          </div>
        </Tabs.TabPane>
      </Tabs>
    </StyledEditor>
  );
});
