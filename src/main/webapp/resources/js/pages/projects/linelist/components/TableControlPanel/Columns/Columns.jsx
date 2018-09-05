import React from "react";
import ColumnVisibility from "./ColumnVisibility";
import { TemplatesPanel } from "../TemplatesPanel";

/**
 * Component to display all components related to column visibility.
 * this included managing and selecting templates and toggling
 * individual column visibility.
 */
export default class Columns extends React.Component {
  render() {
    return (
      <React.Fragment>
        <TemplatesPanel {...this.props} />
        <ColumnVisibility {...this.props} />
      </React.Fragment>
    );
  }
}
