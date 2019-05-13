import React, { useEffect, useState } from "react";
import styled from "styled-components";
import { Icon } from "antd";

const Header = styled.div`
  display: flex;
  align-items: center;
`;

const SortIcon = styled(Icon)``;
const FilterIcon = styled.div`
  padding: 0 5px;
`;

const AscSortIcon = () => <SortIcon type="arrow-up" />;
const DescSortIcon = () => <SortIcon type="arrow-down" />;
const NoSortIcon = () => null;

const FilterMenu = React.forwardRef((props, ref) => (
  <FilterIcon ref={ref} onClick={e => props.displayFilter(e, ref)}>
    <Icon type="filter" />
  </FilterIcon>
));

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
  const filterRef = React.createRef();

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

  function showMenu(event, ref) {
    event.stopPropagation();
    console.log(ref)
    showColumnMenu(ref);
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
      {enableMenu ? (
        <FilterMenu ref={filterRef} displayFilter={showMenu} />
      ) : null}
    </Header>
  );
}

HeaderRenderer.propTypes = {};
