import React, { useState } from "react";
import { useMetadataTemplate } from "../../../../contexts/metadata-template-context";
import { Button } from "antd";

import { SPACE_SM } from "../../../../styles/spacing";

export function Template() {
  const { template } = useMetadataTemplate();
  const [fields, setFields] = useState(template.fields.map((f) => f.label));

  const getFields = () => {};

  return (
    <div
      style={{
        width: "100%",
        overflow: "hidden",
        border: `1px solid transparent`,
      }}
    >
      <div style={{ marginBottom: SPACE_SM }}>
        <Button onClick={getFields}>New Field Group</Button>
      </div>
    </div>
  );
}
