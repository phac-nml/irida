/**
 * @file Default layout for all ag-grid tables.  Cleans up icons and styles to
 * match as close as possible to ant.design principles.  Use this to wrap all
 * all React instances of ag-grid.
 */
import React from "react";
import PropTypes from "prop-types";
import styled from "styled-components";
import { blue6, grey2, grey5, grey7, grey8, grey9 } from "../../styles/colors";
import { Layout } from "antd";
import { ANT_DESIGN_FONT_FAMILY } from "../../styles/fonts";

const LayoutStyles = styled(Layout)`
  // Wrapper for the entire ag-grid and any associated components
  box-sizing: border-box;
  height: ${props => props.height}px;
  width: 100%;
  border: 1px solid ${grey5};

  .ag-root {
    font-family: ${ANT_DESIGN_FONT_FAMILY};
    color: ${grey8};
  }

  .ag-header .ag-header-cell {
    background-color: ${grey2};
    color: ${grey9};
  }

  .ag-icon {
    font-size: 18px;
  }

  // Overwrite the default material colours.
  .ag-icon-checkbox-checked,
  .ag-wrapper.ag-input-wrapper .ag-icon-checkbox-checked,
  .ag-wrapper.ag-input-wrapper .ag-icon-checkbox-indeterminate {
    color: ${blue6};
  }

  .ag-icon-checkbox-unchecked,
  .ag-wrapper.ag-input-wrapper .ag-icon-checkbox-unchecked {
    color: ${grey7};
  }

  // Style the default editor within ag-grid
  .ag-cell-edit-input {
    color: ${grey9};
    font-size: 12px !important;
    padding: 0 10px;
  }
`;

export function AgGridLayout({ children, ...props }) {
  return (
    <LayoutStyles className="ag-theme-material" {...props}>
      {children}
    </LayoutStyles>
  );
}

AgGridLayout.propTypes = {
  children: PropTypes.oneOfType([
    PropTypes.element,
    PropTypes.arrayOf(PropTypes.element)
  ]),
  height: PropTypes.number
};
