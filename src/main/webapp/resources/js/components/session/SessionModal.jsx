import React from "react";
import PropTypes from "prop-types";
import { Modal } from "antd";
import axios from "axios";

const { i18n } = window.TL.session;

/**
 * Component to display a modal warning the user that their session is about
 * to expire.  User is presented with options to keep their session or logout.
 */
export default class SessionModal extends React.Component {
  static propTypes = {
    resetTimeout: PropTypes.func.isRequired,
    time: PropTypes.number.isRequired
  };

  /**
   * Initialize the modal when this component is loaded.
   */
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

  /**
   * To prevent any possible memory leaks in the future, when this component
   * is removed from the page, clear the timeout.
   */
  componentWillUnmount() {
    window.clearTimeout(this._timeout);
  }

  /**
   * Log the user out by going to the logout URL.
   */
  logout = () => {
    window.location = `${window.TL.BASE_URL}logout`;
  };

  /**
   * Keep the users session alive by hitting the server with a head call.
   */
  keepSession = () => {
    /*
    Let the server know that the user is still active.
     */
    axios.head(window.location.href).then(() => {
      this.setState({ visible: false });
      this.props.resetTimeout();
    });
  };

  /**
   * Display the modal to the user.
   */
  showModal = () => {
    Modal.confirm({
      title: i18n.title.replace("[TIME]", this.props.time / 60000), // Convert time to minutes.
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
