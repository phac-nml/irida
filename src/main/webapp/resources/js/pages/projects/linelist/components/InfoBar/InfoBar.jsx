/**
 * @file This component is rendered at the bottom on the bottom of an
 * ag-grid table and used to display the number of items selected, and the
 * total number of elements in the table.
 */

import React from "react";
import PropTypes from "prop-types";
import { FilteredCounts } from "./FilteredCounts";
import { SelectedCount } from "./SelectedCount";
import styled from "styled-components";
import { grey2 } from "../../../../../styles/colors";
import { GRID_BORDER } from "../../styles";

const Wrapper = styled.div`
  height: 30px;
  line-height: 30px;
  padding-left: 1em;
  padding-right: 19px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    Oxygen-Sans, Ubuntu, Cantarell, "Helvetica Neue", sans-serif;
  background-color: ${grey2};
  border: ${GRID_BORDER};
  border-top: none;
  display: flex;
  justify-content: space-between;
`;

/**
 * Displays selected counts and filtered counts at the bottom of the table.
 */
export function InfoBar(props) {
  return (
    <Wrapper>
      <SelectedCount count={props.selectedCount} />
      <FilteredCounts
        filterCount={props.filterCount}
        totalSamples={props.totalSamples}
      />
    </Wrapper>
  );
}

InfoBar.propTypes = {
  selectedCount: PropTypes.number,
  filterCount: PropTypes.number.isRequired,
  totalSamples: PropTypes.number.isRequired
};
