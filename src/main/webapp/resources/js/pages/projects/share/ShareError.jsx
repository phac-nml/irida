import { Button, Card, Result } from "antd";
import React from "react";
import { useDispatch } from "react-redux";
import { setProject } from "./shareSlice";

/**
 * React component to display any errors that occur during sharing moving samples
 * @param {string} error - the error message returned by the server
 * @param {string} redirect - url to redirect the user to
 * @returns {JSX.Element}
 * @constructor
 */
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
