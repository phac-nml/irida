import { Button, Card, Result } from "antd";
import React from "react";
import { useDispatch } from "react-redux";
import { setProject } from "./shareSlice";

export function ShareError({ error, redirect }) {
  const dispatch = useDispatch();

  /*
  If there was an error give the user the option to recover from it
  by reseting the form.
   */
  const reset = () => {
    dispatch(setProject(undefined));
  };

  return (
    <Card>
      <Result
        status="500"
        title={error?.data.error}
        extra={[
          <Button key="back" onClick={reset} type="primary">
            Try Again?
          </Button>,
          <Button key="samples" href={redirect}>
            Return To Samples
          </Button>,
        ]}
      />
      ,
    </Card>
  );
}
