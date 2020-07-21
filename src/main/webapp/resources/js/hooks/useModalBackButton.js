import { useCallback, useEffect, useState } from "react";

export function useModalBackButton(handler) {
  const [location, setLocation] = useState();

  useEffect(() => setLocation(window.location.href), []);

  const callback = useCallback(() => {
    if (window.location.href === location) {
      handler();
    }
  }, [handler, location]);

  useEffect(() => {
    window.addEventListener("popstate", callback);
    return () => window.removeEventListener("popstate", callback);
  }, [callback]);
}
