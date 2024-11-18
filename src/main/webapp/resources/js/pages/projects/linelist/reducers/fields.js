import { isDate } from "../../../../utilities/date-utilities";
import { FIELDS, TYPES } from "../constants";
import { types as templateActionTypes } from "./templates";

/*
Formatting for date fields
 */
const dateColumn = {
  cellRenderer: "DateCellRenderer",
  filter: "agDateColumnFilter",
  cellStyle(params) {
    if (!params.value || isDate(params.value)) {
      return {
        paddingRight: 2,
        backgroundColor: "transparent",
      };
    }
    return {
      paddingRight: 2,
      backgroundColor: "#FFF1F0",
    };
  },
  filterParams: {
    comparator(d1, d2) {
      if (typeof d1 === "undefined" || !isDate(d1)) {
        return 1;
      } else if (typeof d2 === "undefined" || !isDate(d2)) {
        return -1;
      } else {
        const date1 = new Date(d1);
        let date2 = new Date(d2);
        date2.setHours(0, 0, 0, 0);
        return date2 - date1;
      }
    },
  },
};

/**
 * Based on the MetadataTemplateFields create the appropriate
 * column type
 * @param {array} col
 * @returns {*}
 */
function getColumnDefinition(col) {
  const { type, field } = col;
  delete col.type; // Cannot have a type as an attribute on the columnDef.

  if (field === FIELDS.icons) {
    Object.assign(col, {
      filter: undefined,
      suppressHeaderMenuButton: true,
      floatingFilterComponentParams: { suppressFilterButton: true },
      width: 105,
      cellRenderer: "IconCellRenderer",
    });
  } else if (type === TYPES.date) {
    Object.assign(col, dateColumn);
  } else if (field === FIELDS.sampleName) {
    Object.assign(col, {
      cellRenderer: "SampleNameRenderer",
      filter: "agTextColumnFilter",
    });
  } else {
    // Default to text filter
    Object.assign(col, { filter: "agTextColumnFilter" });
  }

  /*
    Set editability of the cell.
    1) If the column is not editable then the cell is not editable at all.
    2) If the user is not the owner the the data, then it is not editable.
    3) If the data itself is not editable, then it cannot be edited either.
     */
  if (col.editable) {
    Object.assign(col, {
      editable: (params) => {
        return JSON.parse(params.data.owner) && window.project.canManage;
      },
    });
  }
  return col;
}

/**
 * Fields need to be formatted properly to go into the column headers.
 * @param {array} cols
 * @returns {*}
 */
const formatColumns = (cols) => cols.map(getColumnDefinition);

export const types = {
  LOAD: "METADATA/FIELDS/LOAD_REQUEST",
  LOAD_ERROR: "METADATA/FIELDS/LOAD_ERROR",
  LOAD_SUCCESS: "METADATA/FIELDS/LOAD_SUCCESS",
};

export const initialState = {
  initializing: true, // Is the API call currently being made
  error: false, // Was there an error making the api call}
  fields: [], // List of metadata fields ==> used for table headers
};

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.LOAD:
      return { ...state, initializing: true, error: false };
    case templateActionTypes.LOAD_SUCCESS: {
      /*
      Get the default template index if there is one
      */
      let defaultTemplateIndex = action.templates.findIndex(
        (template) => template.id === window.project.defaultMetadataTemplateId
      );

      /*
      If a default template index is not found then it returns a -1,
      in which case we set the defaultTemplateIndex to 0 which is the
      "all fields" template
      */
      if (defaultTemplateIndex < 0) {
        defaultTemplateIndex = 0;
      }

      return {
        ...state,
        initializing: false,
        error: false,
        fields: formatColumns(action.templates[defaultTemplateIndex].fields),
      };
    }
    case types.LOAD_ERROR:
      return {
        ...state,
        initializing: false,
        error: true,
        fields: [],
      };
    default:
      return state;
  }
};

export const actions = {
  load: () => ({ type: types.LOAD }),
  success: (fields) => ({ type: types.LOAD_SUCCESS, fields }),
  error: (error) => ({ type: types.LOAD_ERROR, error }),
};
