/**
 * @file Default layout for all ag-grid tables.  Cleans up icons and styles to
 * match as close as possible to ant.design principles.  Use this to wrap all
 * all React instances of ag-grid.
 */
import React from "react";
import styled from "styled-components";
import { blue6, grey2, grey5, grey7, grey8, grey9 } from "../../styles/colors";
import { Layout } from "antd";
import { ANT_DESIGN_FONT_FAMILY } from "../../styles/fonts";

const LayoutStyles = styled(Layout)`
  // Wrapper for the entire ag-grid and any associated components
  box-sizing: border-box;
  height: ${(props) => props.height}px;
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

  .ag-checkbox-input-wrapper {
    font-size: 14px !important;
  }

  .ag-checkbox-input-wrapper:active,
  .ag-checkbox-input-wrapper:focus,
  .ag-checkbox-input-wrapper:focus-within {
    box-shadow: none !important;
  }

  // Overwrite the default material colours.
  .ag-checkbox-input-wrapper.ag-checked:after,
  .ag-checkbox-input-wrapper.ag-indeterminate:after {
    color: ${blue6} !important;
  }

  .ag-checkbox-input-wrapper:after {
    color: ${grey7} !important;
  }

  // Style the default editor within ag-grid
  .ag-cell-inline-editing {
    border-color: ${grey9} !important;
  }
`;

export function AgGridLayout({ children, ...props }) {
  return (
    <LayoutStyles className="ag-theme-material" {...props}>
      {children}
    </LayoutStyles>
  );
}