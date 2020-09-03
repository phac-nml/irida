import { useEffect, useState } from "react";

/**
 * Simple debounce function to be used to slow ajax requests from inputs.
 * @param value
 * @param delay
 * @returns {*}
 */
export function useDebounce(value, delay = 350) {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);
    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
}
