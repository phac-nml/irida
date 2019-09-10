import React, { useEffect, useState } from "react";
import SessionModal from "./SessionModal";

function interceptHTTP(handler) {
  // Capture all ajax requests, these will indicate a need to reset the timeout.
  const open = window.XMLHttpRequest.prototype.open;

  function openReplacement() {
    // Restart the timeout
    handler();
    // Allow the regular open to happen.
    return open.apply(this, arguments);
  }

  window.XMLHttpRequest.prototype.open = openReplacement;
}

export function Session() {
  /**
   * Length of time the modal window is displayed to the user before the page
   * will refresh (logging out the user)
   * 2 minutes in milliseconds (2 * 60 * 1000)
   */
  const MODAL_TIMEOUT = 120000;

  /*
  Spring Session length.
  Initially set in seconds by spring, but we need it in milliseconds.
  Adding an extra 5 seconds to confirm the server has timed out.
   */
  const SESSION_LENGTH = window.TL.SESSION_LENGTH * 1000;

  const [visible, setVisibility] = useState(false);

  const resetTimeout = () => setVisibility(false);

  const timeout = setTimeout(
    () => setVisibility(true),
    SESSION_LENGTH - MODAL_TIMEOUT
  );

  useEffect(() => {
    return () => clearTimeout(timeout);
  }, []);

  useEffect(() => interceptHTTP(() => resetTimeout()), []);

  return visible ? (
    <SessionModal
      displayTime={MODAL_TIMEOUT}
      resetTimeout={resetTimeout}
      visibility={visible}
    />
  ) : null;
}
