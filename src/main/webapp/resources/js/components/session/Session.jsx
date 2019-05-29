import React, { lazy, Suspense, useEffect, useState } from "react";

const SessionModal = lazy(() => import("./SessionModal"));

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

  const SESSION_LENGTH = window.TL.SESSION_LENGTH * 1000; // Originally stored in seconds

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
    <Suspense fallback={<div />}>
      <SessionModal
        displayTime={MODAL_TIMEOUT}
        resetTimeout={resetTimeout}
        visibility={visible}
      />
    </Suspense>
  ) : null;
}
