import {Colours} from './../../../../../utilities/colour.utilities';

export const formatMetadata = metadata => {
  const result = {};
  const colourMap = {};
  const colourFns = {};

  const ids = Object.keys(metadata);
  const fields = Object.keys(metadata[ids[0]]);

  // Generate a new colour function for each field.
  fields.forEach(field => {
    colourFns[field] = new Colours();
  });

  ids.forEach(id => {
    const data = metadata[id];
    result[id] = {};
    fields.forEach(field => {
      const label = data[field];
      colourMap[field] = colourMap[field] || {};
      colourMap[field][label] =
        colourMap[field][label] || colourFns[field].getNext();
      result[id][field] = {label, colour: colourMap[field][label]};
    });
  });
  return result;
};
