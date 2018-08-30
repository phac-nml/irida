import React from "react";
import PropTypes from "prop-types";
import ColumnVisibility from "./ColumnVisibility";
import { TemplatesPanel } from "../TemplatesPanel";

export default class Columns extends React.Component {
  static propTypes = {};

  render() {
    return (
      <React.Fragment>
        <TemplatesPanel {...this.props} />
        <ColumnVisibility {...this.props} />
      </React.Fragment>
    );
  }
}
