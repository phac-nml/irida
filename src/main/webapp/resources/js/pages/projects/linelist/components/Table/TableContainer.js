import { connect } from "react-redux";
import { Table } from "./Table";
import { actions } from "../../redux/modules/template";

function formatFieldsByTemplate(fields, template) {
  if (template.length === 0) return fields;
  // Need to keep sample name as the first one so lets just hold onto it :)
  const sample = fields.shift();
  const final = new Array(template.length);

  // Go through the template and find out if the field is in the list of fields.
  // If it is, add it in the correct spot in the new fields list.
  template.forEach((t, i) => {
    const index = fields.findIndex(f => {
      return f.field === t.label;
    });

    if (index > -1) {
      final[i] = fields.splice(index, 1)[0];
    }
  });

  return [
    sample,
    ...final
      .filter(f => typeof f !== "undefined")
      .map(f => ({ ...f, hide: false })),
    ...fields.map(f => ({ ...f, hide: true }))
  ];
}

const mapStateToProps = state => ({
  fields: formatFieldsByTemplate(
    [...state.fields.fields],
    [...state.template.fields]
  ),
  entries: state.entries.entries
});

const mapDispatchToProps = dispatch => ({
  templateModified: () => dispatch(actions.templateModified())
});

export const TableContainer = connect(mapStateToProps, mapDispatchToProps)(
  Table
);
