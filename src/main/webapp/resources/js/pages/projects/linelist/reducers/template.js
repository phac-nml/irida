import { fromJS } from "immutable";

export const types = {
  LOAD: `METADATA/TEMPLATE/LOAD`,
  SUCCESS: `METADATA/TEMPLATE/SUCCESS`
};

export const initialState = fromJS({
  current: -1
});

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.SUCCESS:
      return state.set("template", action.template).set("current", action.id);
    default:
      return state;
  }
};

export const actions = {
  load: id => ({ type: types.LOAD, id }),
  success: (template, id) => ({ type: types.SUCCESS, template, id })
};
