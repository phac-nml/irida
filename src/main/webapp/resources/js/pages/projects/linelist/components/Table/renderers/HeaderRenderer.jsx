import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import styled from "styled-components";
import { Icon } from "antd";
import { MetadataFieldMenu } from "../../MetadataFieldMenu";

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

const AscSortIcon = () => <SortIcon type="arrow-up" />;
const DescSortIcon = () => <SortIcon type="arrow-down" />;
const NoSortIcon = () => null;

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
    column.addEventListener("sortChanged", onSortChanged);
    onSortChanged();
    return () => column.removeEventListener("sortChanged", onSortChanged);
  }, []);

  const sortColumn = event => {
    const order =
      sortDirection === SORTS.ASC
        ? SORTS.DESC
        : sortDirection === SORTS.DESC
        ? SORTS.NONE
        : SORTS.ASC;
    setSort(order, event.shiftKey);
  };

  const onSortChanged = () => {
    if (column.isSortAscending()) {
      setSortDirection(SORTS.ASC);
    } else if (column.isSortDescending()) {
      setSortDirection(SORTS.DESC);
    } else {
      setSortDirection(SORTS.NONE);
    }
  };

  const showMenu = event => {
    event.stopPropagation();
    showColumnMenu(menuButton);
  };

  const deleteColumnData = field => api.removeColumnData(field);

  const canDeleteField = ({ field }) =>
    !(field.includes("irida-static") || field === "icons");

  return (
    <Header onClick={sortColumn}>
      <div style={{ flexGrow: 1 }}>
        <span className="ag-header-cell-text">{displayName}</span>
        {sortDirection === SORTS.ASC ? (
          <AscSortIcon />
        ) : sortDirection === SORTS.DESC ? (
          <DescSortIcon />
        ) : (
          <NoSortIcon />
        )}
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
        {canDeleteField(column.colDef) ? (
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
