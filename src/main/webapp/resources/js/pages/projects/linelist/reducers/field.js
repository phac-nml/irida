import { defaultState } from "../defaultState";
import {
  FIELD_API_CALL_ERROR,
  FIELD_API_CALL_REQUEST,
  FIELD_API_CALL_SUCCESS
} from "../actions";

export function fieldReducer(state = defaultState.fields, action) {
  switch (action.type) {
    case FIELD_API_CALL_REQUEST:
      return { ...state, fetching: true, error: null };
    case FIELD_API_CALL_SUCCESS:
      return { ...state, fetching: false, error: false, fields: action.fields };
    case FIELD_API_CALL_ERROR:
      return { ...state, fetching: false, error: true, fields: null };
    default:
      return state;
  }
}
