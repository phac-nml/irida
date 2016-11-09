import {getRandomColour} from './../../../../../utilities/colour.utilities';

const _terms = Symbol('terms');
const _metadata = Symbol('metadata');
const _ids = Symbol('keys');

const _formatMetadata = (ids, terms, metadata) => {
  const result = {};
  const colourMap = {};

  ids.forEach(id => {
    const data = metadata[id];
    result[id] = {};
    terms.forEach(term => {
      const label = data[term];
      colourMap[term] = colourMap[term] || {};
      colourMap[term][label] =
        colourMap[term][label] || getRandomColour();
      result[id][term] = {label, colour: colourMap[term][label]};
    });
  });

  return result;
};

export class MetadataManager {
  constructor(terms, metadata) {
    this[_terms] = terms;
    this[_ids] = Object.keys(metadata);
    this[_metadata] = _formatMetadata(this[_ids], terms, metadata);

    // Create a blank reference
    const reference = {};
    terms.forEach(term => {
      reference[term] = {label: '', colour: '#FFFFFF'};
    });
    this[_ids].push('reference');
    this[_metadata].reference = reference;
  }

  getAllMetadata() {
    return this.getMetadataForKeys(this[_terms]);
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
