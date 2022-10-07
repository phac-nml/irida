/*
 * This file contains the state and functions
 * required for displaying analysis outputs.
 */

import React, { useContext, useState } from "react";
import { AnalysisContext } from "../contexts/AnalysisContext";

// Functions required by context
import { getOutputInfo } from "../apis/analysis/analysis";

const initialContext = {
  outputs: null,
  fileTypes: [
    {
      hasJsonFile: false,
      hasTabularFile: false,
      hasTextFile: false,
      hasHtmlFile: false,
    },
  ],
};

const AnalysisOutputsContext = React.createContext(initialContext);
const blocklistExtSet = new Set(["zip", "pdf", "xls"]);
const jsonExtSet = new Set(["json"]);
const tabExtSet = new Set(["tab", "tsv", "tabular", "csv"]);
const excelFileExtSet = new Set(["xlsx"]);
const imageFileExtSet = new Set(["png", "jpeg", "jpg"]);
const htmlFileExtSet = new Set(["html", "html-zip"]);

function AnalysisOutputsProvider(props) {
  const [analysisOutputsContext, setAnalysisOutputsContext] =
    useState(initialContext);
  const { analysisIdentifier } = useContext(AnalysisContext);

  function getPreviewForFileType(fileExt, type) {
    if (type === "text") {
      return (
        !tabExtSet.has(fileExt) &&
        !jsonExtSet.has(fileExt) &&
        !blocklistExtSet.has(fileExt) &&
        !excelFileExtSet.has(fileExt) &&
        !imageFileExtSet.has(fileExt) &&
        !htmlFileExtSet.has(fileExt)
      );
    } else if (type === "excel") {
      return excelFileExtSet.has(fileExt);
    } else if (type === "image") {
      return imageFileExtSet.has(fileExt);
    } else if (type === "json") {
      return jsonExtSet.has(fileExt);
    } else if (type === "tab") {
      return tabExtSet.has(fileExt);
    } else if (type === "html") {
      return htmlFileExtSet.has(fileExt);
    }
  }

  function getAnalysisOutputs() {
    let hasJsonFile = false;
    let hasTabularFile = false;
    let hasTextFile = false;
    let hasExcelFile = false;
    let hasImageFile = false;
    let hasHtmlFile = false;

    getOutputInfo(analysisIdentifier).then((data) => {
      console.log(data);
      // Check if json, tab, and/or text files exist
      // Used by output file preview to only display
      // tabs that are required

      if (data !== "") {
        data.find(function (el) {
          if (!hasJsonFile) {
            hasJsonFile = getPreviewForFileType(el.fileExt, "json");
          }

          if (!hasTabularFile) {
            hasTabularFile = getPreviewForFileType(el.fileExt, "tab");
          }

          if (!hasTextFile) {
            hasTextFile = getPreviewForFileType(el.fileExt, "text");
          }

          if (!hasExcelFile) {
            hasExcelFile = getPreviewForFileType(el.fileExt, "excel");
          }

          if (!hasImageFile) {
            hasImageFile = getPreviewForFileType(el.fileExt, "image");
          }

          if (!hasHtmlFile) {
            hasHtmlFile = getPreviewForFileType(el.fileExt, "html");
          }
        });
      }

      setAnalysisOutputsContext((analysisOutputsContext) => {
        return {
          ...analysisOutputsContext,
          outputs: data,
          fileTypes: [
            {
              hasJsonFile,
              hasTabularFile,
              hasTextFile,
              hasExcelFile,
              hasImageFile,
              hasHtmlFile,
            },
          ],
        };
      });
    });
  }

  return (
    <AnalysisOutputsContext.Provider
      value={{
        analysisOutputsContext,
        getAnalysisOutputs,
        getPreviewForFileType,
      }}
    >
      {props.children}
    </AnalysisOutputsContext.Provider>
  );
}
export { AnalysisOutputsContext, AnalysisOutputsProvider };
