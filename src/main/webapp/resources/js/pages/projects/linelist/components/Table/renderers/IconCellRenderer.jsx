import React from "react";
import PropTypes from "prop-types";
import { Icon, Tooltip } from "antd";

const { i18n } = window.PAGE;

function LockedIcon() {
  return (
    <Tooltip title={i18n.linelist.icons.locked} placement="right">
      <Icon type="lock" theme="twoTone" />
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
