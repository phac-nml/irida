import React from "react";
import PropTypes from "prop-types";
import { FilteredCounts } from "./FilteredCounts";
import { SelectedCount } from "./SelectedCount";
import styled from "styled-components";
import { grey2, grey5 } from "../../../../../styles/colors";

const Wrapper = styled.div`
  height: 30px;
  line-height: 30px;
  padding-left: 1em;
  padding-right: 19px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    Oxygen-Sans, Ubuntu, Cantarell, "Helvetica Neue", sans-serif;
  background-color: ${grey2};
  border: 1px solid ${grey5};
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
