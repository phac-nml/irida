import React from "react";

const { tour } = window.PAGE.i18n.linelist;

export const steps = [
  {
    selector: ".ag-root",
    content() {
      return (
        <div>
          <strong>{tour.table.title}</strong>
          <p>{tour.table.content}</p>
        </div>
      );
    }
  },
  {
    selector: ".ag-header-cell:nth-of-type(2)",
    content() {
      return (
        <div>
          <strong>{tour.headers.title}</strong>
          <p>{tour.headers.content.intro}</p>
          <ol>
            <li>{tour.headers.content.li1}</li>
            <li>
              {tour.headers.content.li2.title}
              <ul>
                <li>{tour.headers.content.li2.content.li1}</li>
                <li>{tour.headers.content.li2.content.li2}</li>
              </ul>
            </li>
            <li>{tour.headers.content.li3}</li>
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
          <strong>{tour.columns.title}</strong>
          <p>{tour.columns.content.intro}</p>
          <ul>
            <li>{tour.columns.content.li1}</li>
            <li>{tour.columns.content.li2}</li>
          </ul>
          <p>{tour.columns.content.end}</p>
        </div>
      );
    }
  },
  {
    selector: `.ag-body-viewport-wrapper .ag-row:nth-of-type(1) .ag-cell:nth-of-type(1)`,
    content() {
      return (
        <div>
          <strong>{tour.edit.title}</strong>
          <p>{tour.edit.content.intro}</p>
          <p>{tour.edit.content.undo}</p>
          <p>{tour.edit.content.cancel}</p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-search"]`,
    content() {
      return (
        <div>
          <strong>{tour.search.title}</strong>
          <p>{tour.search.content}</p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-filter-counts"]`,
    content() {
      return (
        <div>
          <strong>{tour.filterCounts.title}</strong>
          <p>{tour.filterCounts.content.intro}</p>
          <p>{tour.filterCounts.content.example}</p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-export"]`,
    content() {
      return (
        <div>
          <strong>{tour.export.title}</strong>
          <p>{tour.export.content}</p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-import"]`,
    content() {
      return (
        <div>
          <strong>{tour.import.title}</strong>
          <p>{tour.import.content.intro}</p>
          <ul>
            <li>{tour.import.content.li1}</li>
            <li>{tour.import.content.li2}</li>
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
          <strong>{tour.select.title}</strong>
          <p>{tour.select.content}</p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-counts"]`,
    content() {
      return (
        <div>
          <strong>{tour.selectCounts.title}</strong>
          <p>{tour.selectCounts.content}</p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-cart"]`,
    content() {
      return (
        <div>
          <strong>{tour.cart.title}</strong>
          <p>{tour.cart.content}</p>
        </div>
      );
    }
  },
  {
    selector: ".js-tour-button",
    content() {
      return (
        <div>
          <strong>{tour.end}</strong>
        </div>
      );
    }
  }
];
