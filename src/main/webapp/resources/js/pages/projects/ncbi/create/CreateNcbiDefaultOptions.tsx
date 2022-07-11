import { Collapse } from "antd";
import React from "react";

function CreateNcbiDefaultOptions() {
  return (
    <Collapse ghost>
      <Collapse.Panel header="Default Settings" key="1">
        <p>These will be applied to all samples:</p>
      </Collapse.Panel>
    </Collapse>
  );
}

export default CreateNcbiDefaultOptions;
