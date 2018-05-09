import { fromJS, List } from "immutable";

export const NO_TEMPLATE_INDEX = -1;
export const MODIFIED_SELECT_INDEX = -2;

export const types = {
  LOAD: `METADATA/TEMPLATE/LOAD`,
  SUCCESS: `METADATA/TEMPLATE/SUCCESS`,
  TEMPLATE_MODIFIED: `METADATA/TEMPLATE/TEMPLATE_MODIFIED`
};

export const initialState = fromJS({
  current: -1,
  template: List(),
  modified: null
});

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.SUCCESS:
      return state
        .set("template", action.template)
        .set("current", action.id)
        .set("modified", null);
    case types.TEMPLATE_MODIFIED:
      const { current, modified } = Object.assign({}, state.toJS());
      const prevCurrent = modified || current;
      return state
        .set("modified", { id: prevCurrent, fields: action.fields })
        .set("current", MODIFIED_SELECT_INDEX);
    default:
      return state;
  }
};

export const actions = {
  load: id => ({ type: types.LOAD, id }),
  success: (template, id) => ({
    type: types.SUCCESS,
    template: fromJS(template),
    id
  }),
  modified: fields => ({ type: types.TEMPLATE_MODIFIED, fields })
};
