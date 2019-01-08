import { connect } from "react-redux";
import SampleDetailsComponent from "../../../components/SampleDetails/SampleDetails";
import { actions } from "../reducer";

const mapStateToProps = state => ({
  visible: state.cartPageReducer.sampleVisible
});

const mapDispatchToProps = dispatch => ({
  hideSample: () => dispatch(actions.hideSample())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(SampleDetailsComponent);
