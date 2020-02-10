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

export function MarkdownViewer({ markdown }) {
  const [value, setValue] = useState("");

  Promise.resolve(markdownConverter.makeHtml(markdown)).then(md => setValue(md));
  return <StyledViewer dangerouslySetInnerHTML={{ __html: value }} />;
}
