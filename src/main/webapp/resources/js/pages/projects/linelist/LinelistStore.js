export function LinelistStore() {
  const state = {
    data: {
      order: []
    }
  };

  return {
    get() {
      return state.data;
    },
    set(order){
      Object.assign(state.data, order);
    }
  };
}