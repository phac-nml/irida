import { useCallback, useEffect } from "react";

export function useModalBackButton(
  openHandler = () => {},
  closeHandler = () => {},
  modalKey
) {
  const callback = useCallback(() => {
    if (window.location.href.includes(modalKey)) {
      openHandler();
    } else {
      closeHandler();
    }
  }, [openHandler, closeHandler, modalKey]);

  useEffect(() => {
    window.addEventListener("popstate", callback);
    return () => window.removeEventListener("popstate", callback);
  }, [callback]);

  /**
   * When mounted, check to see if the modal should be open.
   */
  useEffect(() => {
    if (window.location.href.includes("add-sample")) {
      openHandler();
    }
  }, [openHandler]);
}
