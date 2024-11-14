/**
 * @fileOverview Default component to be used when a markdown editor is
 * required on a page.
 */

import React, { forwardRef, useImperativeHandle } from "react";
import {MDXEditor, UndoRedo, BoldItalicUnderlineToggles, CreateLink, Separator, ListsToggle, InsertImage, toolbarPlugin, linkPlugin, linkDialogPlugin, listsPlugin } from "@mdxeditor/editor";
import "@mdxeditor/editor/style.css";

import styled from "styled-components";

const MarkdownEditorContainer = styled.div`

  .mdxeditor-popup-container {  z-index: 1100;}

`;


/**
 * Render a markdown editor to a react component.
 * Must pass in a `ref` (created using useRef).  See usage in CreateNewAnnouncement.jsx
 * @type {React.ForwardRefExoticComponent<React.PropsWithoutRef<{readonly markdown?: *}> & React.RefAttributes<unknown>>}
 */
export const MarkdownEditor = forwardRef(({ markdown }, ref) => {
  const [value, setValue] = React.useState(markdown || "");

  useImperativeHandle(ref, () => ({
    getMarkdown() {
      return value;
    }
  }));

  return (
    <MarkdownEditorContainer>
      <MDXEditor
        markdown={value}
        onChange={setValue}
        plugins={[
        toolbarPlugin({
          toolbarClassName: 'my-classname',
          toolbarContents: () => (
            <>
              <UndoRedo />
              <Separator />
              <BoldItalicUnderlineToggles />
              <Separator />
              <ListsToggle />
              <Separator />
              <CreateLink />
              <Separator />
            </>
          )
        }),
        linkPlugin(),
        linkDialogPlugin(),
        listsPlugin()
      ]}
        ref={ref}
      />
    </MarkdownEditorContainer>
  );
});
