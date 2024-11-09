/*
 * This file stores the list of projects which
 * an analysis is shared with or can be shared with
 */

import React, { useState } from "react";

const initialContext = {
  sharedProjects: [],
};

const AnalysisShareContext = React.createContext(initialContext);

function AnalysisShareProvider(props) {
  const [analysisShareContext, setAnalysisShareContext] =
    useState(initialContext);

  function storeSharedProjects(sharedProjectObject) {
    setAnalysisShareContext((analysisShareContext) => {
      return {
        ...analysisShareContext,
        sharedProjects: sharedProjectObject.sharedProjects,
      };
    });
  }

  /*
   * This function updates whether or not an analysis is shared
   * with a specific project. We use this to update the sharedProjects
   * object so a server call is not required to reload the projects which
   * an analysis can be shared with
   */
  function updateSharedProjectShareStatus(sharedProjectObject) {
    const indexOfProjectToUpdate =
        analysisShareContext.sharedProjects.findIndex(
          (sharedProj) =>
            sharedProj.project.identifier === sharedProjectObject.projectId
        ),
      sharedProjects = [...analysisShareContext.sharedProjects];
    sharedProjects[indexOfProjectToUpdate].shared =
      sharedProjectObject.shareStatus;

    setAnalysisShareContext((analysisShareContext) => {
      return { ...analysisShareContext, sharedProjects: sharedProjects };
    });
  }

  return (
    <AnalysisShareContext.Provider
      value={{
        analysisShareContext,
        storeSharedProjects,
        updateSharedProjectShareStatus,
      }}
    >
      {props.children}
    </AnalysisShareContext.Provider>
  );
}
export { AnalysisShareContext, AnalysisShareProvider };
