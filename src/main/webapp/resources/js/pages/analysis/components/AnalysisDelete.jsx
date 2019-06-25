import React from "react";
import PropTypes from "prop-types";
import { Button, Checkbox, Alert, Popconfirm, message } from "antd";

export default function AnalysisDelete() {
  return (
      <>
        <h2 style={{fontWeight: "bold"}}>Delete Analysis</h2>
        <br />
        <strong><Alert message="Warning! Deletion of an analysis is a permanent action!" type="warning" /></strong>
        <br />
        <Checkbox>Confirm analysis deletion</Checkbox>
        <br /><br />
        <Popconfirm placement="top" title="Delete Analysis 1?" okText="Confirm" cancelText="Cancel"> <Button type="danger">Delete</Button> </Popconfirm>
      </>
  );
}
