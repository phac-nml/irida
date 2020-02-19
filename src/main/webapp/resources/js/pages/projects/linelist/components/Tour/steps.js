import React from "react";

export const steps = [
  {
    selector: ".ag-root",
    content() {
      return (
        <div>
          <strong>{i18n("linelist.tour.table.title")}</strong>
          <p>{i18n("linelist.tour.table.content")}</p>
        </div>
      );
    }
  },
  {
    selector: ".ag-header-cell:nth-of-type(2)",
    content() {
      return (
        <div>
          <strong>{i18n("linelist.tour.headers.title")}</strong>
          <p>{i18n("linelist.tour.headers.content.intro")}</p>
          <ol>
            <li>{i18n("linelist.tour.headers.content.li1")}</li>
            <li>
              {i18n("linelist.tour.headers.content.li2.title")}
              <ul>
                <li>{i18n("linelist.tour.headers.content.li2.content.li1")}</li>
                <li>{i18n("linelist.tour.headers.content.li2.content.li2")}</li>
              </ul>
            </li>
            <li>{i18n("linelist.tour.headers.content.li3")}</li>
          </ol>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-columns"]`,
    content() {
      return (
        <div>
          <strong>{i18n("linelist.tour.columns.title")}</strong>
          <p>{i18n("linelist.tour.columns.content.intro")}</p>
          <ul>
            <li>{i18n("linelist.tour.columns.content.li1")}</li>
            <li>{i18n("linelist.tour.columns.content.li2")}</li>
          </ul>
          <p>{i18n("linelist.tour.columns.content.end")}</p>
        </div>
      );
    }
  },
  {
    selector: `.ag-body-viewport-wrapper .ag-row:nth-of-type(1) .ag-cell:nth-of-type(1)`,
    content() {
      return (
        <div>
          <strong>{i18n("linelist.tour.edit.title")}</strong>
          <p>{i18n("linelist.tour.edit.content.intro")}</p>
          <p>{i18n("linelist.tour.edit.content.undo")}</p>
          <p>{i18n("linelist.tour.edit.content.cancel")}</p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-search"]`,
    content() {
      return (
        <div>
          <strong>{i18n("linelist.tour.search.title")}</strong>
          <p>{i18n("linelist.tour.search.content")}</p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-filter-counts"]`,
    content() {
      return (
        <div>
          <strong>{i18n("linelist.tour.filterCounts.title")}</strong>
          <p>{i18n("linelist.tour.filterCounts.content.intro")}</p>
          <p>{i18n("linelist.tour.filterCounts.content.example")}</p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-export"]`,
    content() {
      return (
        <div>
          <strong>{i18n("linelist.tour.export.title")}</strong>
          <p>{i18n("linelist.tour.export.content")}</p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-import"]`,
    content() {
      return (
        <div>
          <strong>{i18n("linelist.tour.import.title")}</strong>
          <p>{i18n("linelist.tour.import.content.intro")}</p>
          <ul>
            <li>{i18n("linelist.tour.import.content.li1")}</li>
            <li>{i18n("linelist.tour.import.content.li2")}</li>
          </ul>
        </div>
      );
    }
  },
  {
    selector: ".ag-row .ag-selection-checkbox:nth-of-type(1)",
    content() {
      return (
        <div>
          <strong>{i18n("linelist.tour.select.title")}</strong>
          <p>{i18n("linelist.tour.select.content")}</p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-counts"]`,
    content() {
      return (
        <div>
          <strong>{i18n("linelist.tour.selectCounts.title")}</strong>
          <p>{i18n("linelist.tour.selectCounts.content")}</p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-cart"]`,
    content() {
      return (
        <div>
          <strong>{i18n("linelist.tour.cart.title")}</strong>
          <p>{i18n("linelist.tour.cart.content")}</p>
        </div>
      );
    }
  },
  {
    selector: ".js-tour-button",
    content() {
      return (
        <div>
          <strong>{i18n("linelist.tour.end")}</strong>
        </div>
      );
    }
  }
];
