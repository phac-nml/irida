import { connect } from "react-redux";
import { Table } from "./Table";

const mapStateToProps = state => ({
  fields: state.fields.fields,
  entries: state.entries.entries
});
const mapDispatchToProps = dispatch => ({});

export const TableContainer = connect(mapStateToProps, mapDispatchToProps)(
  Table
);
