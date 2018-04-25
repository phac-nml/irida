import { connect } from "react-redux";
import { Table } from "./Table";

function formatFieldsByTemplate(fields, template) {
  if (template.length === 0) return fields;
  const sample = fields.shift();
  const result = new Array(template.length);
}

const mapStateToProps = state => ({
  fields: formatFieldsByTemplate(
    [...state.fields.fields],
    [...state.templates.template]
  ),
  entries: state.entries.entries
});
const mapDispatchToProps = dispatch => ({});

export const TableContainer = connect(mapStateToProps, mapDispatchToProps)(
  Table
);
