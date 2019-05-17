import React from "react";
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
    padding: 0 .3rem;
    &:hover {
      background-color: #e4e6e6;
    }
  }

  &:hover .utility-icon {
    opacity: 1;
  }
`;

const SortIcon = styled(Icon)`
  margin: .3rem;
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

  deleteColumnData = field => this.props.api.removeColumnData(field);

  canDeleteField = ({ field }) =>
    !(field.includes("irida-static") || field === "icons");

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
        <span onClick={e => e.stopPropagation()}>
          {this.props.enableMenu ? (
            <div
              className={"utility-icon"}
              ref={menuButton => {
                this.menuButton = menuButton;
              }}
              onClick={e => this.showMenu(e)}
            >
              <Icon type="filter" />
            </div>
          ) : null}
          {this.canDeleteField(this.props.column.colDef) ? (
            <div className={"utility-icon"}>
              <MetadataFieldMenu
                field={this.props.column.colDef}
                removeColumnData={this.deleteColumnData}
              />
            </div>
          ) : null}
        </span>
      </Header>
    );
  }
}

HeaderRenderer.propTypes = {};
