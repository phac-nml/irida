import React from "react";
import { render } from "react-dom";
import { PageHeader } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { ViewAssociatedProjects } from "./ViewAssociatedProjects";

function AssociatedProjects() {
  return (
    <section>
      <PageHeader
        style={{ padding: 0, paddingBottom: SPACE_MD }}
        title="Associated Projects"
        subTitle="These will only reflect projects that you have permissions on."
      />
      <ViewAssociatedProjects />
    </section>
  );
}

render(<AssociatedProjects />, document.querySelector("#root"));
