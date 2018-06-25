import React from "react";

const { i18n } = window.PAGE;

export function InfoBar(props) {
  return (
    <div className="ag-grid-info-panel">
      {props.selectedCount === 0
        ? i18n.linelist.selected.none
        : props.selectedCount === 1
          ? i18n.linelist.selected.one
          : i18n.linelist.selected.multiple.replace(
              "_COUNT_",
              props.selectedCount
            )}
    </div>
  );
}
