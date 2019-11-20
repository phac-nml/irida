/**
 * @File component responsible for the layout of the associated projects page.
 */
import React from "react";
import ViewAssociatedProjects from "./ViewAssociatedProjects";
import { getI18N } from "../../../utilities/i18n-utilities";
import { TabPaneContent } from "../../../components/tabs";

export default function AssociatedProjects() {
  return (
    <TabPaneContent
      title={getI18N("AssociatedProjects.title")}
      subTitle={getI18N("AssociatedProjects.subTitle")}
    >
      <ViewAssociatedProjects />
    </TabPaneContent>
  );
}
