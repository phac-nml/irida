import { Button } from "antd";
import React from "react";
import styled from "styled-components";
import { Legend } from "./Legend";

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
  border: 1px solid lightgray;
  width: 250px;
  height: 100%;
  min-height: 0px;
  overflow: auto;
`

export function CollapsibleSidebar({onToggle}) {
  const [activeItem, setActiveItem] = React.useState();

  const onLegendBtnClick = () => {
    if (!activeItem || activeItem !== "legend") {
      setActiveItem("legend");
    } else {
      setActiveItem(null);
    }
  }

  React.useEffect(() => {
    onToggle();
  }, [activeItem]);

  return (
    <SidebarContainer>
      { activeItem ? (
        <SidebarContent>
          <Legend />
        </SidebarContent>
      ) : null }
      <SidebarMenu>
        <Button onClick={onLegendBtnClick}>Legend</Button>
      </SidebarMenu>
    </SidebarContainer>
  );
};