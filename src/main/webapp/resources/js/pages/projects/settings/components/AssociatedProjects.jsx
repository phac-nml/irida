/**
 * @File component responsible for the layout of the associated projects page.
 */
import React from "react";
import { TabPaneContent } from "../../../../components/tabs";
import ViewAssociatedProjects from "./associated/ViewAssociatedProjects";

export default function AssociatedProjects() {
  return (
    <TabPaneContent
      title={i18n("AssociatedProjects.title")}
      subTitle={i18n("AssociatedProjects.subTitle")}
    >
      <ViewAssociatedProjects />
    </TabPaneContent>
  );
}
