import { useRouteError } from "react-router-dom";
import { Button, Card, Result } from "antd";
import { CONTEXT_PATH } from "../../data/routes";
import React from "react";

export default function PageBoundary() {
  const error = useRouteError() as {
    status: 404 | 500;
    data: string;
  };

  return (
    <Card style={{ margin: 25 }}>
      <Result
        status={error.status}
        title={error.data}
        extra={
          <Button type="primary" href={CONTEXT_PATH}>
            Back Home
          </Button>
        }
      />
    </Card>
  );
}
