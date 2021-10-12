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
            {i18n("ShareError.reset")}
          </Button>,
          <Button key="samples" href={redirect}>
            {i18n("ShareError.return")}
          </Button>,
        ]}
      />
      ,
    </Card>
  );
}
