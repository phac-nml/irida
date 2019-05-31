import React from "react";

import PropTypes from "prop-types";
import { Icon, Tooltip } from "antd";

function LockedIcon() {
  return (
    <Tooltip title={i18n("project.samples.locked-title")} placement="right">
      <Icon type="lock" theme="twoTone" />
    </Tooltip>
  );
}

export class IconCellRenderer extends React.Component {
  static propTypes = {
    data: PropTypes.object.isRequired
  };

  render() {
    const { owner } = this.props.data;
    return (
      <React.Fragment>
        {!JSON.parse(owner) ? <LockedIcon /> : null}
      </React.Fragment>
    );
  }
}
