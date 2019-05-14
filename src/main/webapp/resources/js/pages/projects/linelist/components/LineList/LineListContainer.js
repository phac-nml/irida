import { connect } from "react-redux";
import { LineList } from "./LineList";
import { actions } from "../../reducers/templates";
import { actions as entryActions } from "../../reducers/entries";

/*
Default react-redux boiler plate to connect the current state of the
application to the component. When the state of the application gets
updated (in this case so far it is the loading state), the this connect
method is what triggers the updates.
 */

const mapStateToProps = state => ({
  initializing: state.fields.initializing,
  error: state.fields.error,
  entries: state.entries.entries,
  templates: state.templates.templates,
  current: state.templates.current,
  modified: state.templates.modified,
  saving: state.templates.saving,
  saved: state.templates.saved,
  selectedCount: state.entries.selected.length
});

const mapDispatchToProps = dispatch => ({
  templateModified: fields => dispatch(actions.templateModified(fields)),
  useTemplate: index => dispatch(actions.use(index)),
  saveTemplate: (name, fields, id) =>
    dispatch(actions.saveTemplate(name, fields, id)),
  selectionChange: count => dispatch(entryActions.selection(count))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(LineList);
