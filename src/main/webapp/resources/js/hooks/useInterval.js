import { useEffect, useRef } from "react";

/**
 * setInterval does not work with React hooks.
 * This solutions is from {@see https://overreacted.io/making-setinterval-declarative-with-react-hooks/}
 * @param callback
 * @param delay
 */
export function useInterval(callback, delay) {
  const savedCallback = useRef();

  // Remember the latest callback.
  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  // Set up the interval.
  useEffect(() => {
    function tick() {
      savedCallback.current();
    }

    if (delay !== null) {
      let id = setInterval(tick, delay);
      return () => clearInterval(id);
    }
  }, [delay]);
}
