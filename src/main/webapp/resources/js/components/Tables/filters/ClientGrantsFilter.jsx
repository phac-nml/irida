import React from "react";
import PropTypes from "prop-types";
import { Select } from "antd";
import { SPACE_XS } from "../../../styles/spacing";

/**
 * Filter for ag-grid Clients table to render a select box
 * with the available grant types to filter by.
 */
export class ClientGrantsFilter extends React.Component {
  static propTypes = {
    valueGetter: PropTypes.func.isRequired,
    filterChangedCallback: PropTypes.func.isRequired
  };

  constructor(props) {
    super(props);

    this.state = {
      value: ""
    };
  }

  isFilterActive() {
    return this.state.value !== "";
  }

  doesFilterPass(params) {
    return this.props.valueGetter(params.node).toString() === this.state.value;
  }
  getModel() {
    return { value: this.state.value };
  }

  setModel(model) {
    this.setState(model ? model.value : "");
  }

  onChange = newValue => {
    if (this.state.value !== newValue) {
      this.setState(
        {
          value: newValue
        },
        () => {
          this.props.filterChangedCallback();
        }
      );
    }
  };

  render() {
    return (
      <div style={{ padding: SPACE_XS }}>
        <Select
          defaultValue={this.state.value}
          style={{ width: 200 }}
          onChange={this.onChange}
        >
          <Select.Option value="">All</Select.Option>
          <Select.Option value="password">password</Select.Option>
          <Select.Option value="authorization_code">
            authorization_code
          </Select.Option>
        </Select>
      </div>
    );
  }
}
