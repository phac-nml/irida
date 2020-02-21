/**
 * @fileOverview Renders a component to view markdown.
 */
import React, { useState } from "react";
import * as Showdown from "showdown";
import styled from "styled-components";

const StyledViewer = styled.span`
  p {
    margin: 0;
  }
`;

export const markdownConverter = new Showdown.Converter({
  tables: true,
  simplifiedAutoLink: true,
  strikethrough: true,
  tasklists: true
});

/**
 * Render markdown viewer to a react component.
 * @type {Converter}
 */
export function MarkdownViewer({ markdown }) {
  const [value, setValue] = useState("");

  /*
  We can use `dangerouslySetInnerHTML` here since we know where the content of
  the markdown is coming from.
   */
  Promise.resolve(markdownConverter.makeHtml(markdown)).then(md =>
    setValue(md)
  );
  return <StyledViewer dangerouslySetInnerHTML={{ __html: value }} />;
}
