import { connect } from "react-redux";
import { Table } from "./Table";

const mapStateToProps = state => ({
  fields: state.fields.get("fields"),
  entries: state.entries.get("entries"),
  templates: state.templates.get("templates"),
  current: state.templates.get("current")
});
const mapDispatchToProps = dispatch => ({});

export const TableContainer = connect(mapStateToProps, mapDispatchToProps)(
  Table
);
