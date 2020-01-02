import React from "react";

import PropTypes from "prop-types";
import { Button, Tooltip } from "antd";
import Columns from "./Columns/Columns";

import styled from "styled-components";
import { grey2, grey5, grey9 } from "../../../../../styles/colors";
import { GRID_BORDER } from "../../styles";

const ControlPanel = styled.div`
  height: 100%;
  position: relative;
  overflow: hidden;
  background-color: ${grey2};
  border-left: ${GRID_BORDER};
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
        <Tooltip title={i18n("linelist.controlPanel.columns.title")} placement="left">
          <Button
            tour="tour-columns"
            shape="circle"
            icon="table"
            className="t-columns-panel-toggle"
            onClick={props.togglePanel}
          />
        </Tooltip>
      </ControlPanelButtons>
    </ControlPanel>
  );
}

TableControlPanel.propTypes = {
  /**
   * Function to handle opening and closing the panel
   */
  togglePanel: PropTypes.func.isRequired,
  /**
   * The height of the table.  Requires because the user can change the window height
   * which will affect the table and control panel components.
   */
  height: PropTypes.number.isRequired
};
