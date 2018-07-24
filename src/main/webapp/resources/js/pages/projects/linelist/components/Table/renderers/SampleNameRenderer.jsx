import PropTypes from "prop-types";

const { BASE_URL } = window.TL;

export function SampleNameRenderer (props) {
    return (
      <a
        target="_blank"
        className="t-sample-name"
        href={`${BASE_URL}samples/${Number(props.data.sampleId)}`}
      >
        {props.value}
      </a>
    );
}

SampleNameRenderer.propTypes = {
  data: PropTypes.object.isRequired,
  value: PropTypes.string.isRequired
};
