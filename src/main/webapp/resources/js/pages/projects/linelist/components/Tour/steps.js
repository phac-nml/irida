import React from "react";

export const steps = [
  {
    selector: ".ag-root",
    content() {
      return (
        <div>
          <strong>Welcome to the new Line List Table</strong>
          <p>
            There are a lot of new features for exploring and manipulating your
            data. Lets take a quick tour to see what is here. You can use your
            arrow keys to navigate through this tour.
          </p>
        </div>
      );
    }
  },
  {
    selector: ".ag-header-cell:nth-of-type(2)",
    content() {
      return (
        <div>
          <strong>Table Headers</strong>
          <p>The table headers are incredibly powerful. You can:</p>
          <ol>
            <li>
              <strong>Sort</strong> - on any number of columns. Hold down the{" "}
              <kbd>Shift</kbd> key while clicking on multiple headers. Clicking
              on a header more than once will change the sort between{" "}
              <em>ascending</em>, <em>descending</em>, and <em>no sort</em>
            </li>
            <li>
              <strong>Drag</strong> - organize your metadata they way you want
              to see it
              <ul>
                <li>
                  Click and the header and while the mouse in pressed, drag the
                  column to the position you want.
                </li>
                <li>
                  Dragging the column to the left hand side will allow you to
                  pin it in place. This will keep the column visible during
                  horizontal scrolls
                </li>
              </ul>
            </li>
            <li>
              <strong>Filter the column</strong> - hovering over the header will
              reveal a menu icon that, when clicked, will allow you to filter
              the data in the column. These column filters allow for a more
              granular search, as well as multiple filters on the same column.
            </li>
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
          <strong>Columns</strong>
          <p>
            Clicking the columns button will open up a panel that will allow you
            to:
          </p>
          <ul>
            <li>Toggle the visibility of the columns</li>
            <li>Select a metadata template</li>
          </ul>
          <p>
            Any time the table is modified, column order or visibility, a
            &quot;Save&quot; button will appear next to the current templates
            name. This allows you to either update the current template or save
            a new template.
          </p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-search"]`,
    content() {
      return (
        <div>
          <strong>General Search</strong>
          <p>
            You can search any field in the table through this search field. The
            table will be updated while you type.
          </p>
          <p>
            You can search multiple columns. All words provided are checked
            against a row. For example if you entered
          </p>
          <pre>chicken alberta</pre>
          <p>
            the search would find all rows that have a column with chicken and
            alberta
          </p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-filter-counts"]`,
    content() {
      return (
        <div>
          <strong>Table Counts</strong>
          <p>
            You can always see how many items are displayed after a filter and
            how many total items are in the table.
          </p>
          <p>
            e.g. &quot;Displaying 12 of 34&quot; would indicate that there are a
            total of 34 samples in this project, but after the filters have been
            applied only 12 are displayed in the table.
          </p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-export"]`,
    content() {
      return (
        <div>
          <strong>Table Export</strong>
          <p>
            Data that is currently displayed in the table can be exported as
            either <strong>Excel</strong> or <strong>CSV</strong> formatted
            files.
          </p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-import"]`,
    content() {
      return (
        <div>
          <strong>Metadata Importer</strong>
          <p>
            New metadata can be imported via and excel file. This file must
            have:
          </p>
          <ul>
            <li>A header row with the metadata label</li>
            <li>Each sample must be on its own row</li>
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
          <strong>Row Selection</strong>
          <p>
            Rows can be selected individually or in multiple by holding down the{" "}
            <kbd>Shift</kbd> key and selecting another row. All rows in between
            will be selected.
          </p>
        </div>
      );
    }
  },
  {
    selector: ".ag-header-select-all",
    content() {
      return (
        <div>
          <strong>Select All / None</strong>
          <p>
            Selection or deselection of all rows in the table can be done by
            clicking this checkbox.
          </p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-counts"]`,
    content() {
      return (
        <div>
          <strong>Selected Row Counts</strong>
          <p>
            At any point, the number of rows selected will be displayed here.
          </p>
        </div>
      );
    }
  },
  {
    selector: `[tour="tour-cart"]`,
    content() {
      return (
        <div>
          <strong>Add Selected Samples to Cart</strong>
          <p>
            Selected samples can be added to the cart by clicking this button.
          </p>
        </div>
      );
    }
  }
];
