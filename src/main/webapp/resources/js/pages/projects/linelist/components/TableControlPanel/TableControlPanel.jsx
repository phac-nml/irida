import React from "react";
import { Button, Tooltip } from "antd";
import Columns from "./Columns/Columns";

import styled from "styled-components";
import { blue6, grey2, grey5, grey9 } from "../../../../../styles/colors";
import { GRID_BORDER } from "../../styles";
import { IconTable } from "../../../../../components/icons/Icons";

const ControlPanel = styled.div`
  height: 100%;
  position: relative;
  overflow: hidden;
  background-color: ${grey2};
  border-left: ${GRID_BORDER};
`;

const ColumnsButton = styled(Button)`
  .anticon {
    color: ${grey5};
    transition: color 0.5s;
  }
  &:hover:hover .anticon {
    color: ${blue6};
  }
`;

const ControlPanelContent = styled.div`
  overflow-x: hidden;
  width: 258px;
  margin-right: 42px;
`;

const ControlPanelButtons = styled.div`
  background-color: ${grey2};
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  padding: 5px;
  border-left: ${GRID_BORDER};

  button {
    color: ${grey9};
  }
`;

/**
 * Component to handle any controls that affect the table.  This includes
 * column visibility.
 */
export default function TableControlPanel(props) {
  return (
    <ControlPanel>
      <ControlPanelContent>
        <Columns {...props} />
      </ControlPanelContent>
      <ControlPanelButtons>
        <Tooltip
          title={i18n("linelist.controlPanel.columns.title")}
          placement="left"
        >
          <ColumnsButton
            tour="tour-columns"
            shape="circle"
            className="t-columns-panel-toggle"
            onClick={props.togglePanel}
          >
            <IconTable />
          </ColumnsButton>
        </Tooltip>
      </ControlPanelButtons>
    </ControlPanel>
  );
}