import React from "react";
import styled from "styled-components";
import { Icon } from "antd";
import { MetadataFieldMenu } from "../MetadataFieldMenu";

const Header = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;

  .hidden-icons {
    opacity: 0;
    transition: opacity 0.25s ease-in-out;
  }
  
  &:hover .hidden-icons {
    opacity: 1;
  }
`;

const SortIcon = styled(Icon)`
  padding: 0 0.5rem;
`;

const AscSortIcon = () => <SortIcon type="arrow-up" />;
const DescSortIcon = () => <SortIcon type="arrow-down" />;
const NoSortIcon = () => null;

const SORTS = {
  ASC: "asc",
  DESC: "desc",
  NONE: ""
};

export class HeaderRenderer extends React.Component {
  // filterRef = React.createRef();

  constructor(props) {
    super(props);
    props.reactContainer.style.display = "inline-block";
    this.state = {
      sortDirection: SORTS.NONE
    };
  }

  componentDidMount() {
    this.props.column.addEventListener("sortChanged", this.onSortChanged);
    this.onSortChanged();
  }

  componentWillUnmount() {
    this.props.column.removeEventListener("sortChanged", this.onSortChanged);
  }

  sortColumn = event => {
    const order =
      this.state.sortDirection === SORTS.ASC
        ? SORTS.DESC
        : this.state.sortDirection === SORTS.DESC
        ? SORTS.NONE
        : SORTS.ASC;
    this.props.setSort(order, event.shiftKey);
  };

  onSortChanged = () => {
    if (this.props.column.isSortAscending()) {
      this.setState({ sortDirection: SORTS.ASC });
    } else if (this.props.column.isSortDescending()) {
      this.setState({ sortDirection: SORTS.DESC });
    } else {
      this.setState({ sortDirection: SORTS.NONE });
    }
  };

  showMenu = event => {
    event.stopPropagation();
    this.props.showColumnMenu(this.menuButton);
  };

  render() {
    return (
      <Header onClick={this.sortColumn}>
        <span>
          {this.props.displayName}
          {this.state.sortDirection === SORTS.ASC ? (
            <AscSortIcon />
          ) : this.state.sortDirection === SORTS.DESC ? (
            <DescSortIcon />
          ) : (
            <NoSortIcon />
          )}
        </span>
        <span className="hidden-icons" onClick={e => e.stopPropagation()}>
          {this.props.enableMenu ? (
            <span
              ref={menuButton => {
                this.menuButton = menuButton;
              }}
              onClick={e => this.showMenu(e)}
            >
              <Icon type="filter" />
            </span>
          ) : null}
          <MetadataFieldMenu field={this.props.column.colDef} />
        </span>
      </Header>
    );
  }
}

HeaderRenderer.propTypes = {};
