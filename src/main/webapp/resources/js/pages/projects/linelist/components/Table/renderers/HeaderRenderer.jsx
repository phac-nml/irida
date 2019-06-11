import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import styled from "styled-components";
import { Icon } from "antd";
import { MetadataFieldMenu } from "../../MetadataFieldMenu";

/**
 * Renderer for the line list table headers.
 */
const Header = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;

  .utility-icon {
    opacity: 0;
    display: inline-block;
    transition: opacity 0.25s ease-in-out;
    padding: 0 0.3rem;
    &:hover {
      background-color: #e4e6e6;
    }
  }

  &:hover .utility-icon {
    opacity: 1;
  }
`;

const SortIcon = styled(Icon)`
  margin: 0.3rem;
`;

const AscSortIcon = () => (
  <span className="t-sort-asc">
    <SortIcon type="arrow-up" />
  </span>
);
const DescSortIcon = () => (
  <span className="t-sort-desc">
    <SortIcon type="arrow-down" />
  </span>
);
const NoSortIcon = () => <span className="t-sort-none" />;

const SORTS = {
  ASC: "asc",
  DESC: "desc",
  NONE: ""
};

export function HeaderRenderer({
  api,
  displayName,
  enableMenu,
  reactContainer,
  column,
  showColumnMenu,
  setSort
}) {
  reactContainer.style.display = "inline-block";
  let menuButton;
  const [sortDirection, setSortDirection] = useState(SORTS.NONE);

  useEffect(() => {
    // Since this overwrites the default ag-grid table headers,
    // we need to hook into their sort system.
    column.addEventListener("sortChanged", onSortChanged);
    onSortChanged();
    // This reoves the listener when the header is removed from the page.
    return () => column.removeEventListener("sortChanged", onSortChanged);
  }, []);

  /**
   * Handle updating the UI during a sort event.
   * @param event
   */
  const sortColumn = event => {
    const order =
      sortDirection === SORTS.ASC
        ? SORTS.DESC
        : sortDirection === SORTS.DESC
        ? SORTS.NONE
        : SORTS.ASC;
    setSort(order, event.shiftKey);
  };

  /**
   * Determine the direction of the sort.
   */
  const onSortChanged = () => {
    if (column.isSortAscending()) {
      setSortDirection(SORTS.ASC);
    } else if (column.isSortDescending()) {
      setSortDirection(SORTS.DESC);
    } else {
      setSortDirection(SORTS.NONE);
    }
  };

  /**
   * Click handler for displaying the sort menu.
   * @param event
   */
  const showMenu = event => {
    // Stop the event from propagating, because it will
    // trigger the column sort.
    event.stopPropagation();
    showColumnMenu(menuButton);
  };

  /**
   * Redux method call to remove the column data.
   * @param {object} field
   * @returns {*}
   */
  const deleteColumnData = field => api.removeColumnData(field);

  return (
    <Header onClick={sortColumn}>
      <div style={{ flexGrow: 1 }}>
        <span className="ag-header-cell-text">{displayName}</span>
        <span className="t-sort">
          {sortDirection === SORTS.ASC ? (
            <AscSortIcon />
          ) : sortDirection === SORTS.DESC ? (
            <DescSortIcon />
          ) : (
            <NoSortIcon />
          )}
        </span>
      </div>
      <div onClick={e => e.stopPropagation()}>
        {enableMenu ? (
          <span
            className={"utility-icon"}
            ref={button => {
              menuButton = button;
            }}
            onClick={e => showMenu(e)}
          >
            <Icon type="filter" />
          </span>
        ) : null}
        {column.colDef.editable ? (
          <span className="utility-icon t-header-menu">
            <MetadataFieldMenu
              field={column.colDef}
              removeColumnData={deleteColumnData}
            />
          </span>
        ) : null}
      </div>
    </Header>
  );
}

HeaderRenderer.propTypes = {
  api: PropTypes.object.isRequired,
  displayName: PropTypes.string.isRequired,
  enableMenu: PropTypes.bool.isRequired,
  reactContainer: PropTypes.object.isRequired,
  column: PropTypes.object.isRequired,
  showColumnMenu: PropTypes.func.isRequired,
  setSort: PropTypes.func.isRequired
};
