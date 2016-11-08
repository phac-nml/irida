import {Colours} from './../../../../../utilities/colour.utilities';

const _terms = Symbol('terms');
const _metadata = Symbol('metadata');
const _ids = Symbol('keys');

const _formatMetadata = (terms, metadata) => {
  const result = {};
  const colourMap = {};
  const colourFns = {};

  const ids = Object.keys(metadata);

  // Generate a new colour function for each field.
  terms.forEach(term => {
    colourFns[term] = new Colours();
  });

  ids.forEach(id => {
    const data = metadata[id];
    result[id] = {};
    terms.forEach(term => {
      const label = data[term];
      colourMap[term] = colourMap[term] || {};
      colourMap[term][label] =
        colourMap[term][label] || colourFns[term].getNext();
      result[id][term] = {label, colour: colourMap[term][label]};
    });
  });
  return result;
};

export class MetadataManager {
  constructor(terms, metadata) {
    this[_terms] = terms;
    this[_metadata] = _formatMetadata(terms, metadata);
    this[_ids] = Object.keys(metadata);
  }
  getMetadataForKeys(keys) {
    const result = {};
    this[_ids].forEach(id => {
      const data = this[_metadata][id];
      const wanted = {};
      keys.forEach(key => {
        wanted[key] = data[key];
      });
      result[id] = wanted;
    });
    return result;
  }
}
