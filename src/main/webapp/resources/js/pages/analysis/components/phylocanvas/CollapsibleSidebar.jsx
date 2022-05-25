import { Button } from "antd";
import React from "react";
import styled from "styled-components";

const SidebarContainer = styled.div`
  display: flex;
  flex-direction: row;
  align-items: start;
  flex: 0 1 auto;
  height: 100%;
`;

const SidebarMenu = styled.div`
  -webkit-transform: rotate(90deg);
  -moz-transform: rotate(90deg);
  -o-transform: rotate(90deg);
  -ms-transform: rotate(90deg);
  transform: rotate(90deg);
  width: 32px;
  overflow: visible;
`;

const SidebarContent = styled.div`
  position: relative;
  border: 1px solid lightgray;
  background-color: #ffffff;
  width: 250px;
  min-height: 0px;
  height: inherit;
`;

export function CollapsibleSidebar({ items, onToggle }) {
  const [activeItem, setActiveItem] = React.useState(null);

  const onMenuItemClick = (index) => {
    if (activeItem === null || activeItem !== index) {
      setActiveItem(index);
    } else {
      setActiveItem(null);
    }
  };

  React.useEffect(() => {
    onToggle();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeItem]);

  return (
    <SidebarContainer>
      {activeItem !== null ? (
        <SidebarContent>{items[activeItem].component}</SidebarContent>
      ) : null}
      <SidebarMenu>
        {items.map((item, index) => (
          <Button
            key={index}
            value={index}
            onClick={() => onMenuItemClick(index)}
            type={index === activeItem ? "primary" : "default"}
          >
            {item.text}
          </Button>
        ))}
      </SidebarMenu>
    </SidebarContainer>
  );
}
