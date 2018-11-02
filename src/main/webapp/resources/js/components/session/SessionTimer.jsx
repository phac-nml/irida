import React from "react";
import { render } from "react-dom";
import Loadable from "react-loadable";

const LoadableModal = Loadable({
  loader: () => import(/* webpackChunkName: 'SessionModal' */ "./SessionModal"),
  loading: () => <div style={{ display: "none" }}>FOOBAR</div>
});

export default class SessionTimer extends React.Component {
  _MODAL_TIMEOUT = 5 * 1000; //2 * 60 * 1000; // 2 minutes in milliseconds
  _SESSION_LENGTH = 30 * 1000; // window.TL.SESSION_LENGTH * 1000; // Session length originally in seconds

  state = {
    visible: false
  };

  setTimeout = () => {
    this._timeout = window.setTimeout(
      () => this.setState({ visible: true }),
      this._SESSION_LENGTH - this._MODAL_TIMEOUT
    );
  };

  clearTimeout = () => {
    window.clearTimeout(this._timeout);
  };

  componentDidMount() {
    // Capture all ajax requests, these will indicate a need to reset the timeout.
    const open = window.XMLHttpRequest.prototype.open;
    const resetTimeout = this.resetTimeout;

    function openReplacement(method, url, async, user, password) {
      // Restart the timeout
      resetTimeout();
      // Allow the regular open to happen.
      return open.apply(this, arguments);
    }
    window.XMLHttpRequest.prototype.open = openReplacement;
    this.setTimeout();
  }

  componentWillUnmount() {
    this.clearTimeout();
  }

  resetTimeout = () => {
    this.clearTimeout();
    this.setTimeout();
  };

  render() {
    return this.state.visible ? (
      <LoadableModal
        resetTimeout={this.resetTimeout}
        time={this._MODAL_TIMEOUT + 2000}
      />
    ) : null;
  }
}

render(<SessionTimer />, document.querySelector("#js-session"));
