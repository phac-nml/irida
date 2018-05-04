export const types = {
  INIT_APP: "APP/INIT"
};

const initialState = {
  projectId: null
};

export default (state = initialState, action = {}) => {
  switch (action.type) {
    case types.INIT_APP:
      return { ...state, projectId: action.id };
    default:
      return state;
  }
};

export const actions = {
  initialize: id => ({ type: types.INIT_APP, id })
};
