import React, { useEffect, useRef } from "react";
import { Form, Input } from "antd";
import { useLaunchDispatch, useLaunchState } from "./launch-context";
import * as CONSTANTS from "./lauch-constants";

/**
 * React component for the editing of launching a pipeline base details, including
 * name and description.
 * @returns {JSX.Element}
 * @constructor
 */
export function PipelineDetails({}) {
  const { name, description } = useLaunchState();
  const dispatch = useLaunchDispatch();
  const nameRef = useRef();

  useEffect(() => {
    // Set focus on the name input at mount time.
    setTimeout(() => nameRef.current.focus(), 100);
  }, []);

  // Select all the text in the name input
  // Used so that when focus is set it all gets selected.
  const selectNameText = () => nameRef.current.select();

  return (
    <>
      <Form.Item label={"NAME"}>
        <Input
          type={"text"}
          value={name}
          ref={nameRef}
          onFocus={selectNameText}
          onChange={(e) =>
            dispatch({
              type: CONSTANTS.DISPATCH_DETAILS_UPDATE,
              value: e.target.value,
              field: "name",
            })
          }
        />
      </Form.Item>
      <Form.Item label={"DESCRIPTION"}>
        <Input.TextArea
          value={description}
          onChange={(e) =>
            dispatch({
              type: CONSTANTS.DISPATCH_DETAILS_UPDATE,
              value: e.target.value,
              field: "description",
            })
          }
        />
      </Form.Item>
    </>
  );
}
