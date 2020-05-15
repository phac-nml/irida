import { useEffect, useRef } from "react";

/**
 * React hook specifically for Ant Design forms inside a modal.  This will
 * reset the form when the modal closes.
 *
 * @param {object} form - a reference to the current form (created use the hook useForm)
 * @param {boolean} visible - whether the modal is displayed or note
 */
export function useResetFormOnCloseModal({ form, visible }) {
  const prevVisibleRef = useRef();
  useEffect(() => {
    prevVisibleRef.current = visible;
  }, [visible]);
  const prevVisible = prevVisibleRef.current;
  useEffect(() => {
    if (!visible && prevVisible) {
      form.resetFields();
    }
  }, [visible, form, prevVisible]);
}
