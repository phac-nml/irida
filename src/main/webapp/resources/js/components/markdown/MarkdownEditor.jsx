/**
 * @fileOverview Default component to be used when a markdown editor is
 * required on a page.
 */

import React, { forwardRef, useImperativeHandle } from "react";
import ReactMde from "react-mde";
import "react-mde/lib/styles/css/react-mde-all.css";
import ReactMarkdown from "react-markdown";
import styled from "styled-components";
import { blue6, grey1, grey4, grey6 } from "../../styles/colors";
import {
  IconBold,
  IconCode,
  IconItalic,
  IconLinkOut,
  IconOrderedList,
  IconStrikeThrough,
  IconUnorderedList
} from "../icons/Icons";

const StyledMde = styled(ReactMde)`
  border-color: ${grey4};
  .mde-header {
    background-color: ${grey1};
    border-color: ${grey4};
    height: 46px;
  }
  .mde-tabs button {
    border-radius: 2px;
    border: 1px solid ${grey1};

    &.selected {
      color: ${blue6};
      border: 1px solid ${blue6};
    }
  }
  .grip {
    background-color: ${grey1};
    border-top: 1px solid ${grey4};
    height: 20px;
    svg {
      color: ${grey6};
    }
  }
`;

/**
 * Render a markdown editor to a react component.
 * Must pass in a `ref` (created using useRef).  See usage in CreateNewAnnouncement.jsx
 * @type {React.ForwardRefExoticComponent<React.PropsWithoutRef<{readonly markdown?: *}> & React.RefAttributes<unknown>>}
 */
export const MarkdownEditor = forwardRef(({ markdown }, ref) => {
  const [value, setValue] = React.useState(markdown || "");
  const [selectedTab, setSelectedTab] = React.useState("write");

  useImperativeHandle(ref, () => ({
    getMarkdown() {
      return value;
    }
  }));

  function formatIcon(cmd) {
    switch (cmd) {
      case "bold":
        return <IconBold />;
      case "italic":
        return <IconItalic />;
      case "strikethrough":
        return <IconStrikeThrough />;
      case "link":
        return <IconLinkOut />;
      case "code":
        return <IconCode />;
      case "unordered-list":
        return <IconUnorderedList />;
      case "ordered-list":
        return <IconOrderedList />;
    }
  }

  return (
    <StyledMde
      value={value}
      getIcon={formatIcon}
      onChange={setValue}
      selectedTab={selectedTab}
      onTabChange={setSelectedTab}
      generateMarkdownPreview={(markdown) =>
        Promise.resolve(<ReactMarkdown>{markdown}</ReactMarkdown>)
      }
    />
  );
});
