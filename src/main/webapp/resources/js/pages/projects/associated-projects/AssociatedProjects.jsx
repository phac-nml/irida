import React from "react";
import { PageHeader } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import ViewAssociatedProjects from "./ViewAssociatedProjects";
import { getI18N } from "../../../utilities/i18n-utilties";

export default function AssociatedProjects() {
  return (
    <section>
      <PageHeader
        style={{ padding: 0, paddingBottom: SPACE_MD }}
        title={getI18N("AssociatedProjects.title")}
        subTitle={getI18N("AssociatedProjects.subTitle")}
      />
      <ViewAssociatedProjects />
    </section>
  );
}
