import React, { forwardRef, useImperativeHandle } from "react";
import ReactMde from "react-mde";
import "react-mde/lib/styles/css/react-mde-all.css";
import {
  BoldOutlined,
  CodeOutlined,
  ItalicOutlined,
  LinkOutlined,
  OrderedListOutlined,
  StrikethroughOutlined,
  UnorderedListOutlined
} from "@ant-design/icons";
import styled from "styled-components";
import { markdownConverter } from "./MarkdownViewer";
import { blue6, grey1, grey4, grey6 } from "../../styles/colors";

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
        return <BoldOutlined />;
      case "italic":
        return <ItalicOutlined />;
      case "strikethrough":
        return <StrikethroughOutlined />;
      case "link":
        return <LinkOutlined />;
      case "code":
        return <CodeOutlined />;
      case "unordered-list":
        return <UnorderedListOutlined />;
      case "ordered-list":
        return <OrderedListOutlined />;
    }
  }

  return (
    <StyledMde
      value={value}
      getIcon={formatIcon}
      onChange={setValue}
      selectedTab={selectedTab}
      onTabChange={setSelectedTab}
      generateMarkdownPreview={markdown =>
        Promise.resolve(markdownConverter.makeHtml(markdown))
      }
    />
  );
});
