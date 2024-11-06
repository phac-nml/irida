import { useEffect, useRef, useState } from "react";

/**
 * setInterval does not work with React hooks.
 * This solutions is from {@see https://overreacted.io/making-setinterval-declarative-with-react-hooks/}
 * @param callback
 * @param delay
 */
export function useInterval(callback, delay) {
  const savedCallback = useRef(callback);
  const [intervalId, setIntervalId] = useState();

  // Remember the latest callback.
  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  // Set up the interval.
  useEffect(() => {
    function tick() {
      savedCallback.current();
    }

    if (delay === null) return;
    else {
      const id = setInterval(tick, delay);
      setIntervalId(id);
      return () => clearInterval(id);
    }
  }, [delay]);

  /* The interval id returned which can be used to clear an interval
   * in cases where we don't want to wait till the component unmounts
   */
  return intervalId;
}
