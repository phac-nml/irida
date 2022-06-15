/**
 * Initial state of the sample table
 * @type {string}
 */
export const INITIAL_TABLE_STATE = JSON.stringify({
  filters: { associated: null },
  pagination: {
    current: 1,
    pageSize: 10,
  },
  order: [{ property: "sample.modifiedDate", direction: "desc" }],
  search: [],
});
