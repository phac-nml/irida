/**
 * @file Display a column for icons based on specific data from the entry.
 */
import React from "react";

import PropTypes from "prop-types";
import { Tooltip } from "antd";
import { LockTwoTone } from "@ant-design/icons";

function LockedIcon() {
  return (
    <Tooltip title={i18n("project.samples.locked-title")} placement="right">
      <div>
        <LockTwoTone />
      </div>
    </Tooltip>
  );
}

export class IconCellRenderer extends React.Component {
  static propTypes = {
    data: PropTypes.object.isRequired
  };

  constructor(props) {
    super(props);
  }

  render() {
    const { owner } = this.props.data;
    return !JSON.parse(owner) ? <LockedIcon /> : null;
  }
}
