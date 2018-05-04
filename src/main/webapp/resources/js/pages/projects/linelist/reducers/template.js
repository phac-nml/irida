import { fromJS, List } from "immutable";

export const NO_TEMPLATE_INDEX = -1;
export const MODIFIED_SELECT_INDEX = -2;

export const types = {
  LOAD: `METADATA/TEMPLATE/LOAD`,
  SUCCESS: `METADATA/TEMPLATE/SUCCESS`,
  TEMPLATE_MODIFIED: `METADATA/TEMPLATE/TEMPLATE_MODIFIED`,
  VALIDATE_TEMPLATE_NAME: `METADATA/TEMPLATE/VALIDATE_TEMPLATE_NAME`
};

export const initialState = fromJS({
  current: -1,
  template: List(),
  modified: false,
  validating: false
});

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.SUCCESS:
      return state
        .set("template", action.template)
        .set("current", action.id)
        .set("modified", false);
    case types.TEMPLATE_MODIFIED:
      return state.set("modified", true).set("current", MODIFIED_SELECT_INDEX);
    case types.VALIDATE_TEMPLATE_NAME:
      return state.set("validating", true);
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
  validateTemplateName: name => ({ type: types.VALIDATE_TEMPLATE_NAME, name }),
  modified: () => ({ type: types.TEMPLATE_MODIFIED })
};
