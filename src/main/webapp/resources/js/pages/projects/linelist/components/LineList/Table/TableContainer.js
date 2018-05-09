import { connect } from "react-redux";
import { Table } from "./Table";
import { actions } from "../../../reducers/template";

const mapStateToProps = state => ({
  fields: state.fields.get("fields"),
  entries: state.entries.get("entries"),
  template: state.template.get("template")
});
const mapDispatchToProps = dispatch => ({
  templateModified: fields => dispatch(actions.modified(fields))
});

export const TableContainer = connect(mapStateToProps, mapDispatchToProps)(
  Table
);
