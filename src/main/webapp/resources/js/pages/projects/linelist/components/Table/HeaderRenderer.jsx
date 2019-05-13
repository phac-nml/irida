import React, { useEffect, useRef, useState } from "react";
import styled from "styled-components";
import { Icon } from "antd";

const Header = styled.div`
  display: flex;
  align-items: center;
`;

const SortIcon = styled(Icon)``;

const AscSortIcon = () => <SortIcon type="arrow-up" />;
const DescSortIcon = () => <SortIcon type="arrow-down" />;
const NoSortIcon = () => null;
const FilterIcon = () =>
  React.forwardRef((props, ref) => <Icon type="filter" />);

const SORTS = {
  ASC: "asc",
  DESC: "desc",
  NONE: ""
};

export function HeaderRenderer({
  column,
  displayName,
  setSort,
  enableMenu,
  showColumnMenu,
  api
}) {
  const [sortDirection, setSortDirection] = useState("");
  const menuRef = useRef();

  useEffect(() => {
    column.addEventListener("sortChanged", onSortChanged);
    onSortChanged();
    return () => column.removeEventListener("sortChanged", onSortChanged);
  }, []);

  function sortColumn(event) {
    const order =
      sortDirection === SORTS.ASC
        ? SORTS.DESC
        : sortDirection === SORTS.DESC
        ? SORTS.NONE
        : SORTS.ASC;
    setSort(order, event.shiftKey);
  }

  function onSortChanged() {
    if (column.isSortAscending()) {
      setSortDirection(SORTS.ASC);
    } else if (column.isSortDescending()) {
      setSortDirection(SORTS.DESC);
    } else {
      setSortDirection(SORTS.NONE);
    }
  }

  function showMenu() {
    showColumnMenu(menuRef.current);
  }

  return (
    <Header onClick={sortColumn}>
      {displayName}
      {sortDirection === SORTS.ASC ? (
        <AscSortIcon />
      ) : sortDirection === SORTS.DESC ? (
        <DescSortIcon />
      ) : (
        <NoSortIcon />
      )}
      {enableMenu ? <FilterIcon ref={menuRef} onClick={showMenu} /> : null}
    </Header>
  );
}

HeaderRenderer.propTypes = {};
