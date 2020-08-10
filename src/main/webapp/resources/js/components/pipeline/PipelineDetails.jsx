import React, { useEffect, useRef } from "react";
import { Form, Input } from "antd";
import { useLaunchDispatch, useLaunchState } from "./launch-context";
import * as CONSTANTS from "./lauch-constants";

export function PipelineDetails({}) {
  const { name, description } = useLaunchState();
  const dispatch = useLaunchDispatch();
  const nameRef = useRef();

  useEffect(() => {
    setTimeout(() => nameRef.current.focus(), 100);
  }, []);

  const selectNameText = () => nameRef.current.select();

  return (
    <Form layout="vertical">
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
    </Form>
  );
}
