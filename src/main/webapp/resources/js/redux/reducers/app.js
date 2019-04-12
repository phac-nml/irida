export const types = {
  INIT_APP: "APP/INIT"
};

const initialState = {};

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.INIT_APP:
      return { ...state, ...action.payload };
    default:
      return { ...state };
  }
};

export const actions = {
  initialize: payload => ({ type: types.INIT_APP, payload })
};
