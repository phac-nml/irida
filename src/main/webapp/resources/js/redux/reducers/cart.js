export const types = {
  ADD: "CART/ADD"
};

const initialState = {};

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    default:
      return state;
  }
};

export const actions = {
  add: samples => ({ type: types.ADD, samples })
};
