import React, { useState } from "react";
import { render } from "react-dom";
import { Button, PageHeader } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { ViewAssociatedProjects } from "./ViewAssociatedProjects";

function AssociatedProjects() {
  const [edit, setEditing] = useState(false);

  return (
    <section>
      <PageHeader
        style={{ padding: 0, paddingBottom: SPACE_MD }}
        title="Associated Projects"
        extra={[<Button onClick={() => setEditing(!edit)}>Modify</Button>]}
      />
      {edit ? <p>Editing</p> : <ViewAssociatedProjects />}
    </section>
  );
}

render(<AssociatedProjects />, document.querySelector("#root"));
