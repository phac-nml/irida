export const TYPES = {
  CART_INITIALIZE: "CART/INITIALIZE",
  CART_INITIALIZED: "CART/INITIALIZED"
};

const initialState = {
  initialized: false
};

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case TYPES.CART_INITIALIZED:
      return {
        ...state,
        ...{
          initialized: true,
          total: action.total
        }
      };
    default:
      return { ...state };
  }
};

export const actions = {
  initialize: () => ({ type: TYPES.CART_INITIALIZE }),
  initialized: total => ({ type: TYPES.CART_INITIALIZED, total })
};
