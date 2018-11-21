import { fromJS, List } from "immutable";
import { types as templateActionTypes } from "./templates";
import { isDate } from "../../../../utilities/date-utilities";
import { FIELDS, TYPES } from "../constants";

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
        backgroundColor: "transparent"
      };
    }
    return {
      paddingRight: 2,
      backgroundColor: "#FFF1F0"
    };
  },
  comparator(d1, d2) {
    if (typeof d1 === "undefined" || !isDate(d1)) {
      return 1;
    } else if (typeof d2 === "undefined" || !isDate(d2)) {
      return -1;
    } else {
      return new Date(d2) - new Date(d1);
    }
  }
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

  let cellRenderer;
  if (field === FIELDS.icons) {
    Object.assign(col, {
      cellRenderer: "IconCellRenderer"
    });
  } else if (type === TYPES.date) {
    Object.assign(col, dateColumn);
  } else if (field === FIELDS.sampleName) {
    Object.assign(col, {
      cellRenderer: "SampleNameRenderer"
    });
  }
  /*
  Set editability of the cell
   */
  Object.assign(col, {
    editable: params => {
      if (!JSON.parse(params.data.owner)) {
        // Cannot edit anything if they don't own it.
        return false;
      }
      return col.editable;
    }
  });
  return col;
}

/**
 * Fields need to be formatted properly to go into the column headers.
 * @param {array} cols
 * @returns {*}
 */
const formatColumns = cols => cols.map(getColumnDefinition);

export const types = {
  LOAD: "METADATA/FIELDS/LOAD_REQUEST",
  LOAD_ERROR: "METADATA/FIELDS/LOAD_ERROR",
  LOAD_SUCCESS: "METADATA/FIELDS/LOAD_SUCCESS"
};

export const initialState = fromJS({
  initializing: true, // Is the API call currently being made
  error: false, // Was there an error making the api call}
  fields: List() // List of metadata fields ==> used for table headers
});

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.LOAD:
      return state.set("initializing", true).set("error", false);
    case templateActionTypes.LOAD_SUCCESS:
      return state
        .set("initializing", false)
        .set("error", false)
        .delete("fields")
        .set("fields", fromJS(formatColumns(action.templates[0].fields)));
    case types.LOAD_ERROR:
      return state
        .set("initializing", false)
        .set("error", true)
        .set("fields", List());
    default:
      return state;
  }
};

export const actions = {
  load: () => ({ type: types.LOAD }),
  success: fields => ({ type: types.LOAD_SUCCESS, fields }),
  error: error => ({ type: types.LOAD_ERROR, error })
};
