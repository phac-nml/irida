import React from "react";
import { connect } from "react-redux";
import { LineList } from "../components/LineList";
import { actions as templateActions } from "../redux/modules/templates";

/*
Default react-redux boiler plate to connect the current state of the
application to the component. When the state of the application gets
updated (in this case so far it is the loading state), the this connect
method is what triggers the updates.
 */
const mapStateToProps = state => ({
  loading: state.fields.fetching && state.fields.fields === null,
  fields: state.fields.fields,
  error: state.fields.error,
  entries: state.entries.entries,
  templates: state.templates.templates,
  template: state.templates.template,
  current: state.templates.current
});

const mapDispatchToProps = dispatch => ({
  useTemplate: id => dispatch(templateActions.useTemplate(id))
});

export default connect(mapStateToProps, mapDispatchToProps)(LineList);
