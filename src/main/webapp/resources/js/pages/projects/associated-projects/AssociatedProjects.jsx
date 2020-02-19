/**
 * @File component responsible for the layout of the associated projects page.
 */
import React from "react";
import ViewAssociatedProjects from "./ViewAssociatedProjects";
import { TabPaneContent } from "../../../components/tabs";

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
