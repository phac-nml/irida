import React from "react";
import { render } from "react-dom";
import Loadable from "react-loadable";

/**
 * Using react-loader to only load the modal when it is needed. since most users
 * will not reach the timeout, there is no point in having it loaded onto the page.
 * THe modal code and css does not need to be loaded at this time.
 */
const LoadableModal = Loadable({
  loader: () => import(/* webpackChunkName: 'SessionModal' */ "./SessionModal"),
  loading: () => <div style={{ display: "none" }} /> // Requires something.
});

/**
 * Component to keep track of whether or not the user has made any interactive
 * requests withing the default session timeout time.  If they have, nothing
 * happens. If they hit the session timeout - 2 minutes, a modal dialogue is
 * downloaded giving them the option to logout or continue the session.  If
 * nothing is clicked the page will refresh after the default session time,
 * allowing the user to return to their current page when they log back in.
 * If the user chooses to keep there session, the sever is sent a little poke
 * to allow it to keep the current session active.
 */
export default class SessionTimer extends React.Component {
  /**
   * Length of time the modal window is displayed to the user before the page
   * will refresh (logging out the user)
   * 2 minutes in milliseconds (2 * 60 * 1000)
   * @type {number}
   * @private
   */
  _MODAL_TIMEOUT = 120000;

  /**
   * Inactive session time (passed from server onto templates/page.html).  Usually
   * 30 minutes.  Here we convert it into milliseconds from the seconds that the
   * server returns.
   * @type {number}
   * @private
   */
  _SESSION_LENGTH = window.TL.SESSION_LENGTH * 1000; // Session length originally in seconds

  state = {
    visible: false
  };

  /**
   * Called when the component is loaded into the UI.
   * 1. Hijack all ajax requests and add a function call to clear and reset
   *    the timeout, since the user is obviously actively using the site.
   * 2. Initialize the timeout
   */
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

  /**
   * To prevent any possible memory leaks in the future, when this component
   * is removed from the page, clear the timeout.
   */
  componentWillUnmount() {
    this.clearTimeout();
  }

  /**
   * Create a timeout.  When this timeout expires, the session modal window
   * will be loaded and displayed.
   */
  setTimeout = () => {
    this._timeout = window.setTimeout(
      () => this.setState({ visible: true }),
      this._SESSION_LENGTH - this._MODAL_TIMEOUT
    );
  };

  /**
   * Remove the current timeout.  Called whenever the user makes an ajax
   * request or clicks the keep session button in the modal window.
   */
  clearTimeout = () => {
    window.clearTimeout(this._timeout);
  };

  /**
   * Reset the timout.  This is called whenever the user makes an ajax request.
   */
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
