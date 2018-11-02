import React from "react";
import PropTypes from "prop-types";
import { Modal } from "antd";
import axios from "axios";

const { i18n } = window.TL.session;

export default class SessionModal extends React.Component {
  static propTypes = {
    resetTimeout: PropTypes.func.isRequired,
    time: PropTypes.number.isRequired
  };

  componentDidMount() {
    // This timer will allow the modal to be displayed until the session expires
    // Once the session has expired the window will reload --> this will allow
    // the user to return to the same page when they login again.
    this._timeout = window.setTimeout(
      () => window.location.reload(false),
      this.props.time + 2000 // 2 seconds added to make sure that the server session has expired
    );
    this.showModal();
  }

  componentWillUnmount() {
    window.clearTimeout(this._timeout);
  }

  logout = () => {
    window.location = `${window.TL.BASE_URL}logout`;
  };

  keepSession = () => {
    /*
    Let the server know that the user is still active.
     */
    axios.head(window.location.href).then(() => {
      this.setState({ visible: false });
      this.props.resetTimeout();
    });
  };

  showModal = () => {
    Modal.confirm({
      okType: "warning",
      title: i18n.title,
      onOk: this.keepSession,
      onCancel: this.logout,
      okText: i18n.keep,
      cancelText: i18n.logout
    });
  };

  render() {
    return <div />;
  }
}
